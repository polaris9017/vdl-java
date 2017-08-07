package kr.projectn.vdl.module.vlive;

import com.comcast.viper.hlsparserj.IPlaylist;
import com.comcast.viper.hlsparserj.MediaPlaylist;
import com.comcast.viper.hlsparserj.PlaylistFactory;
import com.comcast.viper.hlsparserj.PlaylistVersion;
import com.comcast.viper.hlsparserj.tags.media.ExtInf;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import kr.projectn.vdl.application.VDLMain;
import kr.projectn.vdl.module.ModuleInterface;
import kr.projectn.vdl.utils.HttpUtil;
import kr.projectn.vdl.utils.RegexUtil;
import kr.projectn.vdl.utils.UrlUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VLive live broadcast extractor & downloader class
 *
 * Created by qscx9512 on 2017-05-03.
 */

public class Realtime implements ModuleInterface {

    private static final int main = 1;
    private static final int min = 1;
    private static final int rev = 0;
    private HttpUtil hutil;
    private RegexUtil regex;


    public Realtime() {
        regex = VDLMain.regex;
        hutil = VDLMain.util;
    }

    @Override
    public void getVersionString() {
        System.out.println("VLive download module ver. " + main + "." + min + "." + rev);
    }

    @Override
    public boolean Run(String url) {
        List<NameValuePair> reqParam = new ArrayList<NameValuePair>();
        Map<String, String> header = new HashMap<String, String>(); //header
        int seq_prev = 0; //Playlist previous sequence
        int seq;

        hutil.setClientConnection(url);

        String vid = regex.setRegexString(UrlUtil.ModuleMagic.URL_VLIVE + "\\/([\\d]+)")
                .Parse(url).group(1);  //Parse video id from URL
        String[] param = regex.setRegexString("\\bvlive\\.video\\.init\\(([^)]+)")
                .Parse(hutil.requestByGet().getAsString()).group(1).split("[\\s\\W]*,[\\s\\W]*"); //Get live status

        reqParam.add(new BasicNameValuePair("videoSeq", vid));

        System.out.println("[vlive-live] 영상 정보 불러오는 중...");
        String title = regex.setRegexString("\"og[^=]*+=\"(\\[[^\"]*+)\"")
                .Parse(hutil.requestByGet().getAsString()).group(1);

        header.put("Referer", UrlUtil.ModuleMagic.URL_VLIVE + "/video/" + vid);
        header.put("Content-Type", "application/x-www-form-urlencoded");

        System.out.println("[vlive-live] 영상 녹화 중...");
        while (!param[2].equals("LIVE_END")) {
            hutil.setClientConnection("http://www.vlive.tv/video/init/view");
            hutil.setConnectionParameter(reqParam);
            String jsonParse = hutil.requestByPost(header).getAsString();

            JsonArray jsonLiveParam = new JsonParser().parse(regex.setRegexString("\"liveStreamInfo\"\\s*:\\s*\"(.*)\",")
                    .Parse(jsonParse).group(1).replace("\\\"", "\"")).getAsJsonObject()
                    .get("resolutions").getAsJsonArray();
            String cdnUrl = jsonLiveParam.get(jsonLiveParam.size() - 1).getAsJsonObject()
                    .get("cdnUrl").getAsString();

            //System.out.println(cdnUrl);

            //com.comcast.viper.hlsparserj
            hutil.setClientConnection(cdnUrl.replace("playlist.m3u8?__gda", "chunklist.m3u8?__agda"));
            IPlaylist playlist = PlaylistFactory.parsePlaylist(PlaylistVersion.TWELVE,
                    hutil.requestByGet().getAsString().replace(",\n", ",")); //replace ',\n' to ',' because of M3U8 format error

            MediaPlaylist mediaPlaylist = (MediaPlaylist) playlist;

            seq = mediaPlaylist.getMediaSequence().getSequenceNumber();


            if (seq < seq_prev + 3) {
                continue;
            } else if (seq >= seq_prev + 3) {
                for (ExtInf stream : mediaPlaylist.getSegments()) {
                    hutil.setClientConnection(
                            regex.setRegexString("\\w*+:\\/\\/vlive(?:[^\\/:]*+\\/)*+").Parse(cdnUrl).group(0)
                                    + stream.getTitle()
                    );
                    System.out.println("Seq: " + seq + "; media: " + stream.getTitle() + "; dur: " + stream.getDuration());
                    hutil.requestByGet().writeStream(UrlUtil.validateFilename(title) + ".mp4");
                }
                seq_prev = seq;
            }
            System.out.println("\n\n\n");
            param = regex.setRegexString("\\bvlive\\.video\\.init\\(([^)]+)")
                    .Parse(hutil.requestByGet().getAsString()).group(1).split("[\\s\\W]*,[\\s\\W]*");
        }
        return false;
    }
}
