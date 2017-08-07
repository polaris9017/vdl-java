package kr.projectn.vdl.module.vlive;

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

/**
 * VLive channel extractor & downloader class
 *
 * Created by qscx9512 on 2017-05-03.
 */

public class Channel implements ModuleInterface {
    private static final int main = 1;
    private static final int min = 1;
    private static final int rev = 0;
    private HttpUtil hutil;
    private RegexUtil regex;
    private String appID;
    private String channelCode;
    private VOD vlive;

    public Channel() {
        hutil = VDLMain.util;
        regex = VDLMain.regex;
        vlive = new VOD();
    }

    @Override
    public void getVersionString() {

    }

    @Override
    public boolean Run(String url) {
        System.out.println("[vlive-channel] 페이지 불러오는 중...");

        channelCode = regex.setRegexString("http:\\/\\/channels\\.vlive\\.tv\\/([\\w\\D]+)\\/video")
                .Parse(url)
                .group(1);

        hutil.setClientConnection(url);
        String appJsUrl = regex.setRegexString("<script[^>]+src=[\\\\\"\\'](http.+?\\/app\\.js)")
                .Parse(hutil.requestByGet().getAsString()).group(1);

        hutil.setClientConnection(appJsUrl);
        appID = regex.setRegexString("Global\\.VFAN_APP_ID\\s*=\\s*[\\'\\\"]([^\\'\\\"]+)[\\'\\\"]")
                .Parse(hutil.requestByGet().getAsString()).group(1);

        String[] vodUrl = FetchVideoList();

        for (String vUrl : vodUrl) {
            vlive.Run(vUrl);
        }
        return true;
    }

    private String[] FetchVideoList() {
        int channelSequence;
        List<NameValuePair> param = new ArrayList<NameValuePair>();

        param.add(new BasicNameValuePair("app_id", appID));
        param.add(new BasicNameValuePair("channelCode", channelCode));

        hutil.setClientConnection("http://api.vfan.vlive.tv/vproxy/channelplus/decodeChannelCode");
        hutil.setConnectionParameter(param);
        channelSequence = new JsonParser().parse(hutil.requestByGet().getAsString()).getAsJsonObject()
                .get("result").getAsJsonObject().get("channelSeq").getAsInt();

        param = new ArrayList<NameValuePair>();

        System.out.println("[vlive-channel] 영상 리스트 불러오는 중...");

        param.add(new BasicNameValuePair("app_id", appID));
        param.add(new BasicNameValuePair("channelSeq", String.valueOf(channelSequence)));
        param.add(new BasicNameValuePair("maxNumOfRows", String.valueOf(10000)));
        hutil.setClientConnection("http://api.vfan.vlive.tv/vproxy/channelplus/getChannelVideoList");
        hutil.setConnectionParameter(param);

        JsonObject videoListJson = new JsonParser().parse(hutil.requestByGet().getAsString()).getAsJsonObject()
                .get("result").getAsJsonObject();
        String[] videoList = new String[videoListJson.get("totalVideoCount").getAsInt()];

        for (int i = 0; i < videoListJson.get("totalVideoCount").getAsInt(); i++) {
            videoList[i] = "http://vlive.tv/video/" + videoListJson.getAsJsonArray("videoList")
                    .get(i).getAsJsonObject().get("videoSeq")
                    .getAsString();
        }

        return videoList;
    }


}
