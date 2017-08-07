package kr.projectn.vdl.module.Facebook;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.projectn.vdl.application.VDLMain;
import kr.projectn.vdl.module.ModuleInterface;
import kr.projectn.vdl.utils.ExceptionReportUtil;
import kr.projectn.vdl.utils.HttpUtil;
import kr.projectn.vdl.utils.RegexUtil;
import kr.projectn.vdl.utils.UrlUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Facebook extractor & downloader class
 *
 * Created by qscx9512 on 2017-05-03.
 */

public class Facebook implements ModuleInterface {
    private static final int main = 2;
    private static final int min = 0;
    private static final int rev = 0;
    private HttpUtil hutil;
    private RegexUtil regex;

    public Facebook() {
        regex = VDLMain.regex;
        hutil = VDLMain.util;
    }

    @Override
    public void getVersionString() {
        System.out.println("Facebook download module ver. " + main + "." + min + "." + rev);
    }

    @Override
    public boolean Run(String url) {
        List<NameValuePair> param = new ArrayList<NameValuePair>();

        //Retrieve access token
        param.add(new BasicNameValuePair("client_id", "841023869361760"));
        param.add(new BasicNameValuePair("client_secret", "e742f0e162c7f0cf412c80434f07a95c"));
        param.add(new BasicNameValuePair("grant_type", "client_credentials"));
        hutil.setClientConnection("https://graph.facebook.com/oauth/access_token");
        hutil.setConnectionParameter(param);
        String token = new JsonParser().parse(hutil.requestByGet().getAsString()).getAsJsonObject()
                .get("access_token").getAsString();

        System.out.println("[facebook] 페이지 불러오는 중...");
        hutil.setClientConnection(url);

        //parse video id
        String vid = regex.setRegexString("(?<=facebook\\.com\\/).+\\/videos\\/([\\d]+)")
                .Parse(hutil.requestByGet().getAsString()).group(1);

        //Get video information
        System.out.println("[facebook] 영상 정보 불러오는 중...");
        param.clear();
        param.add(new BasicNameValuePair("fields", "source,title"));
        param.add(new BasicNameValuePair("access_token", token));

        hutil.setClientConnection("https://graph.facebook.com/v2.10/" + vid);
        hutil.setConnectionParameter(param);

        System.out.println("[facebook] loading Facebook API...");

        JsonObject jsonObj = new JsonParser().parse(hutil.requestByGet().getAsString()).getAsJsonObject();
        String cdnUrl = jsonObj.get("source").getAsString();

        String title = "";
        try {
            title = UrlUtil.validateFilename(jsonObj.get("title").getAsString());
        } catch (Exception e) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("저장할 파일 명: ");
                title = UrlUtil.validateFilename(in.readLine());
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                ExceptionReportUtil.reportExceptionToFile(e1);
            }
        }


        System.out.println("[facebook] 영상 내려받는 중...");
        System.out.println("영상 타이틀: " + title);

        hutil.setClientConnection(cdnUrl);

        return hutil.requestByGet().writeFile("[facebook]" + UrlUtil.validateFilename(title) + ".mp4");
    }
}