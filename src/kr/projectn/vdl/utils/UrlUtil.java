package kr.projectn.vdl.utils;

import java.util.ArrayList;
import java.util.List;

import kr.projectn.vdl.application.VDLMain;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.*;

import kr.projectn.vdl.utils.HttpUtil;

/**
 * Created by Kim.K on 2017-05-03.
 */
public class UrlUtil {
    private String requestVCode(String code) {
        HttpUtil util = VDLMain.util;
        List<NameValuePair> param = new ArrayList<NameValuePair>();

        param.add(new BasicNameValuePair("code", code));
        util.setClientConnection("https://projectn.tk/api/v1/request");
        util.setConnectionParameter(param);

        String JsonString = util.requestByGet().getAsString();
        //Json parse로 처리할것
        JsonObject jsonObj = new JsonParser().parse(JsonString).getAsJsonObject();

        if (!jsonObj.get("status").getAsString().equals("err")) {
            return jsonObj.get("url").getAsString();
        }

        return null;
    }

    public int setModuleId(String url) {
        if (url.contains(ModuleMagic.URL_VLIVE)) {
            return ModuleMagic.MODULE_VLIVE;
        } else if (url.contains(ModuleMagic.URL_VLIVE_CHANNEL)) {
            return ModuleMagic.MODULE_VLIVE_CHANNEL;
        } else if (url.contains(ModuleMagic.URL_NAVER)) {
            return ModuleMagic.MODULE_NAVER;
        } else if (url.contains(ModuleMagic.URL_DAUM)) {
            return ModuleMagic.MODULE_DAUM;
        } else if (url.contains(ModuleMagic.URL_KAKAO)) {
            return ModuleMagic.MODULE_KAKAO;
        } else if (url.contains(ModuleMagic.URL_FACEBOOK)) {
            return ModuleMagic.MODULE_FACEBOOK;
        } else if (url.contains(ModuleMagic.URL_INSTAGRAM)) {
            return ModuleMagic.MODULE_INSTAGRAM;
        } else {
            UrlUtil util = new UrlUtil();
            String codeUrl = util.requestVCode(url);
            if (!(codeUrl != null && codeUrl.isEmpty())) {
                return util.setModuleId(codeUrl);
            }
        }

        return 0xff;
    }

    /**
     * Created by Kim.K on 2017-05-03.
     */

    //http://www.nextree.co.kr/p11686
    public interface ModuleMagic {
        int MODULE_VLIVE = 0x10;
        int MODULE_VLIVE_CHANNEL = 0x11;
        int MODULE_NAVER = 0x12;
        int MODULE_DAUM = 0x13;
        int MODULE_KAKAO = 0x14;
        int MODULE_FACEBOOK = 0x15;
        int MODULE_INSTAGRAM = 0x16;

        String URL_VLIVE = "vlive.tv/video";
        String URL_VLIVE_CHANNEL = "channels.vlive.tv";
        String URL_NAVER = "tv.naver.com";
        String URL_DAUM = "tvpot.daum.net";
        String URL_KAKAO = "tv.kakao.com";
        String URL_FACEBOOK = "facebook.com";
        String URL_INSTAGRAM = "instagram.com";
    }
}
