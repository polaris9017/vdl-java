package kr.projectn.vdl.module.Instagram;

import kr.projectn.vdl.application.VDLMain;
import kr.projectn.vdl.module.ModuleInterface;
import kr.projectn.vdl.utils.HttpUtil;
import kr.projectn.vdl.utils.RegexUtil;

/**
 * Instagram extractor & downloader class
 *
 * Created by qscx9512 on 2017-05-03.
 */

public class Instagram implements ModuleInterface {
    private static final int main = 1;
    private static final int min = 0;
    private static final int rev = 0;
    private HttpUtil hutil;
    private RegexUtil regex;

    public Instagram() {
        regex = VDLMain.regex;
        hutil = VDLMain.util;
    }

    @Override
    public void getVersionString() {
        System.out.println("Instagram download module ver. " + main + "." + min + "." + rev + "-java");
    }

    @Override
    public boolean Run(String url) {
        System.out.println("[instagram] 페이지 불러오는 중...");
        hutil.setClientConnection(url);

        String cdnUrl, videoRes;

        cdnUrl = regex.setRegexString("\\Wmeta.+secure_url.+content=\\\"(.+)\\\"")
                .Parse(hutil.requestByGet().getAsString()).group(1);
        videoRes = regex.setRegexString("\\Wmeta.+video:height.+content=\\\"(.+)\\\"")
                .Parse(hutil.requestByGet().getAsString()).group(1);

        String title = regex.setRegexString("(?<=instagram\\.com)\\/p\\/(.+)\\/")
                .Parse(url).group(1);

        System.out.println("[instagram] 영상 내려받는 중...");
        System.out.println("영상 타이틀: " + title);
        System.out.println("영상 해상도: " + videoRes);

        hutil.setClientConnection(cdnUrl);
        return hutil.requestByGet().writeFile("[instagram] " + title + ".mp4");
    }
}
