package kr.projectn.vdl.module.daum;

import com.google.gson.JsonParser;
import kr.projectn.vdl.application.VDLMain;
import kr.projectn.vdl.module.ModuleInterface;
import kr.projectn.vdl.utils.HttpUtil;
import kr.projectn.vdl.utils.PrintUtil;
import kr.projectn.vdl.utils.RegexUtil;
import kr.projectn.vdl.utils.UrlUtil;

/**
 * DaumKakao Kakao tv extractor & downloader class
 *
 * Created by qscx9512 on 2017-05-03.
 */

public class kakao implements ModuleInterface {
    private static final int main = 1;
    private static final int min = 1;
    private static final int rev = 0;
    private HttpUtil hutil;
    private RegexUtil regex;


    @Override
    public void getVersionString() {
        System.out.println("Daum Tvpot download module ver. " + main + "." + min + "." + rev);
    }

    public kakao() {
        hutil = VDLMain.util;
        regex = VDLMain.regex;
    }
    @Override
    public boolean Run(String url) {

        System.out.println("[kakao] loading kakao tv API json...");
        String clipid = regex.setRegexString("tv\\.kakao\\.com.+cliplink\\/(.*)").Parse(url)
                .group(1);


        //Get Daum TVPot video id from Kakao TV clipLink API(in s1.daumcdn.net/svc/original/U03/cssjs/klimt/kakaotv-eb201f18f4.min.js)
        hutil.setClientConnection("http://tv.kakao.com/api/v1/ft/cliplinks/" + clipid);
        String vid = new JsonParser().parse(hutil.requestByGet().getAsString()).getAsJsonObject()
                .get("clip").getAsJsonObject().get("vid").getAsString();

        //set title
        String title = new JsonParser().parse(hutil.requestByGet().getAsString()).getAsJsonObject()
                .get("clip").getAsJsonObject().get("title").getAsString();

        PrintUtil.printWarning("[kakao] Redirecting to Daum TVPot module... ");
        tvpot m = new tvpot(title);
        return m.Run(UrlUtil.ModuleMagic.URL_DAUM + "/v/" + vid);
    }
}