package kr.projectn.vdl.module.naver;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.projectn.vdl.application.VDLMain;
import kr.projectn.vdl.module.ModuleInterface;
import kr.projectn.vdl.utils.HttpUtil;
import kr.projectn.vdl.utils.RegexUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Naver TVCast extractor & downloader class
 *
 * Created by qscx9512 on 2017-05-03.
 */

public class tvcast implements ModuleInterface {
    private static final int main = 1;
    private static final int min = 1;
    private static final int rev = 0;
    private HttpUtil hutil;
    private RegexUtil regex;

    @Override
    public void getVersionString() {
        System.out.println("Naver Tvcast download module ver. " + main + "." + min + "." + rev);
    }

    public tvcast() {
        hutil = VDLMain.util;
        regex = VDLMain.regex;
    }

    @Override
    public boolean Run(String url) {

        System.out.println("[tvcast] 페이지 불러오는 중...");
        hutil.setClientConnection(url);
        Matcher m = regex.setRegexString("var rmcPlayer = new nhn\\.rmcnmv\\.RMCVideoPlayer\\(\\\"(.+?)\\\", \\\"(.+?)\\\"")
                .Parse(hutil.requestByGet().getAsString());

        //Extract Media ID
        System.out.println("[tvcast] Extracting Media ID...");
        String mid = m.group(1);
        String key = m.group(2);

        //Add parameter for requesting video info
        List<NameValuePair> reqParam = new ArrayList<NameValuePair>();
        reqParam.add(new BasicNameValuePair("key", key));

        //Request VOD information JSON
        hutil.setClientConnection("http://play.rmcnmv.naver.com/vod/play/v2.0/" + mid);
        hutil.setConnectionParameter(reqParam);
        String JsonString = hutil.requestByGet().getAsString();

        //Parse VOD title
        JsonObject jsonObj = new JsonParser().parse(JsonString).getAsJsonObject();
        //http://stackoverflow.com/questions/8233542/parse-a-nested-json-using-gson
        String title = jsonObj.get("meta")
                .getAsJsonObject().get("subject")
                .getAsString();

        //Get video data
        JsonArray videoList = jsonObj.get("videos")
                .getAsJsonObject().getAsJsonArray("list");
        String[] videoRes = new String[videoList.size()];
        String[] cdnUrl = new String[videoList.size()];
        long[] fileSize = new long[videoList.size()];
        int arraySize = jsonObj.get("videos")
                .getAsJsonObject().getAsJsonArray("list").size();
        for (int i = 0; i < arraySize; i++) {
            videoRes[i] = videoList.get(i).getAsJsonObject()
                    .get("encodingOption").getAsJsonObject()
                    .get("name").getAsString();
            cdnUrl[i] = videoList.get(i).getAsJsonObject()
                    .get("source").getAsString();
            fileSize[i] = videoList.get(i).getAsJsonObject()
                    .get("size").getAsLong();
        }

        System.out.println("[tvcast] 영상 내려받는 중...");
        System.out.println("영상 타이틀: " + title);
        System.out.println("영상 해상도: " + videoRes[arraySize - 1]);
        System.out.println("파일 사이즈: " + (float) (fileSize[arraySize - 1] / (1024 * 1024)) + "MB");

        hutil.setClientConnection(cdnUrl[arraySize - 1]);
        return hutil.requestByGet().writeFile(title + ".mp4");
    }
}
