package kr.projectn.vdl.module.vlive;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.projectn.vdl.application.VDLMain;
import kr.projectn.vdl.module.ModuleInterface;
import kr.projectn.vdl.utils.HttpUtil;
import kr.projectn.vdl.utils.PrintUtil;
import kr.projectn.vdl.utils.RegexUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * VLive extractor & downloader class
 *
 * Created by qscx9512 on 2017-05-03.
 */
public class VOD implements ModuleInterface {

    private static final int main = 1;
    private static final int min = 1;
    private static final int rev = 0;
    private HttpUtil hutil;
    private RegexUtil regex;

    /**
     * Default constructor
     */
    public VOD() {
        regex = VDLMain.regex;
        hutil = VDLMain.util;
    }

    /**
     * Prints module version string
     */
    @Override
    public void getVersionString() {
        System.out.println("VLive download module ver. " + main + "." + min + "." + rev);
    }

    @Override
    public boolean Run(String url) {
        String status, vid_long, key;

        System.out.println("[vlive-vod] 페이지 불러오는 중...");
        hutil.setClientConnection(url);
        Matcher m = regex.setRegexString("\\bvlive\\.video\\.init\\(([^)]+)\\)")
                .Parse(hutil.requestByGet().getAsString());
        String[] param = m.group(1).split("[\\s\\W]*,[\\s\\W]*");
        status = param[2];
        vid_long = param[5];
        key = param[6];

        //Determine current status
        switch (status) {
            case "LIVE_ON_AIR":
            case "BIG_EVENT_ON_AIR":
                ModuleInterface mod = new Realtime();
                return mod.Run(url);
            case "VOD_ON_AIR":
            case "BIG_EVENT_INTRO":
                System.out.println("[vlive-vod] 영상 정보 불러오는 중...");
                break;
            case "LIVE_END":
                PrintUtil.printWarning("라이브 방송이 종료되었습니다. 현재 다시보기 준비중입니다.");
                break;
            case "COMING_SOON":
                PrintUtil.printWarning("방송 준비중입니다. 잠시만 기다려주세요.");
                break;
            case "CANCELED":
                PrintUtil.printWarning("방송이 예기치 않게 취소되었습니다.");
                break;
        }


        //Add parameter for requesting video info
        List<NameValuePair> reqParam = new ArrayList<NameValuePair>();
        reqParam.add(new BasicNameValuePair("videoId", vid_long));
        reqParam.add(new BasicNameValuePair("key", key));
        reqParam.add(new BasicNameValuePair("ptc", "http"));
        reqParam.add(new BasicNameValuePair("doct", "json"));
        reqParam.add(new BasicNameValuePair("cpt", "vtt"));

        //Request VOD information JSON
        hutil.setClientConnection("http://global.apis.naver.com/rmcnmv/rmcnmv/vod_play_videoInfo.json");
        hutil.setConnectionParameter(reqParam);
        String JsonString = hutil.requestByGet().getAsString();

        //Parse VOD title
        JsonObject jsonObj = new JsonParser().parse(JsonString).getAsJsonObject();
        //http://stackoverflow.com/questions/8233542/parse-a-nested-json-using-gson
        String title = jsonObj.get("meta")
                .getAsJsonObject().get("subject")
                .getAsString();

        if(title.isEmpty()) {
            hutil.setClientConnection(url);
            m = regex.setRegexString("og:title.+(\\[[^\"]*+)")
                    .Parse(hutil.requestByGet().getAsString());
            title = m.group(1);
        }

        //Get video data
        JsonArray videoList = jsonObj.get("videos")
                .getAsJsonObject().getAsJsonArray("list");
        String[] videoRes = new String[videoList.size()];
        String[] cdnUrl = new String[videoList.size()];
        long[] fileSize = new long[videoList.size()];
        int arraySize = jsonObj.get("videos")
                .getAsJsonObject().getAsJsonArray("list").size();
        for(int i = 0; i < arraySize; i++) {
            videoRes[i] = videoList.get(i).getAsJsonObject()
                    .get("encodingOption").getAsJsonObject()
                    .get("name").getAsString();
            cdnUrl[i] = videoList.get(i).getAsJsonObject()
                    .get("source").getAsString();
            fileSize[i] = videoList.get(i).getAsJsonObject()
                    .get("size").getAsLong();
        }

        System.out.println("[vlive-vod] 영상 내려받는 중...");
        System.out.println("영상 타이틀: " + title);
        System.out.println("영상 해상도: " + videoRes[arraySize - 1]);
        System.out.println("파일 사이즈: " + (float)(fileSize[arraySize - 1] / (1024 * 1024)) + "MB");

        hutil.setClientConnection(cdnUrl[arraySize - 1]);
        return hutil.requestByGet().writeFile(title + ".mp4");
    }
}