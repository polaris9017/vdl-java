package kr.projectn.vdl.module.daum;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.projectn.vdl.application.VDLMain;
import kr.projectn.vdl.module.ModuleInterface;
import kr.projectn.vdl.utils.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Daum TvPot extractor & downloader class
 *
 * Created by qscx9512 on 2017-05-03.
 */

public class tvpot implements ModuleInterface {
    private static final int main = 1;
    private static final int min = 1;
    private static final int rev = 0;
    private HttpUtil hutil;
    private RegexUtil regex;
    private String title;

    public tvpot() {
        hutil = VDLMain.util;
        regex = VDLMain.regex;
    }

    public tvpot(String title) {
        this();
        this.title = title;
    }

    @Override
    public void getVersionString() {
        System.out.println("Daum Tvpot download module ver. " + main + "." + min + "." + rev);
    }

    @Override
    public boolean Run(String url) {
        JsonObject jsonObject;
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        PrintUtil p = new PrintUtil();

        String vid = regex.setRegexString("tvpot\\.daum\\.net.+v\\/(.+)").Parse(url)
                .group(1);  //video id

        System.out.println("[tvpot] 영상 정보 불러오는 중...");
        hutil.setClientConnection("http://videofarm.daum.net/controller/api/closed/v1_2/IntegratedMovieData.json");
        param.add(new BasicNameValuePair("vid", vid));
        param.add(new BasicNameValuePair("dte_type", "WEB"));
        hutil.setConnectionParameter(param);
        param.clear();

        jsonObject = new JsonParser().parse(hutil.requestByGet().getAsString()).getAsJsonObject();
        JsonArray metaData = jsonObject.get("output_list").getAsJsonObject()
                .get("output_list").getAsJsonArray();  //parse video metadata Json

        String[] videoRes = new String[metaData.size()];
        String[] profile = new String[metaData.size()];
        String[] cdnUrl = new String[metaData.size()];
        long[] vid_fsz = new long[metaData.size()];

        for (int i = 0; i < metaData.size(); i++) {
            vid_fsz[i] = metaData.get(i).getAsJsonObject()
                    .get("filesize").getAsLong();
            videoRes[i] = metaData.get(i).getAsJsonObject()
                    .get("label").getAsString();
            profile[i] = metaData.get(i).getAsJsonObject()
                    .get("profile").getAsString();

            param.add(new BasicNameValuePair("vid", vid));
            param.add(new BasicNameValuePair("profile", profile[i]));
            XmlHandler handler = new XmlHandler("http://videofarm.daum.net/controller/api/open/v1_2/MovieLocation.apixml",
                    param);
            handler.parse();
            cdnUrl[i] = handler.getUrl();
            param.clear();
        }

        if (title.isEmpty()) {
            param.add(new BasicNameValuePair("vid", vid));
            XmlHandler handler = new XmlHandler("http://tvpot.daum.net/clip/ClipInfoXml.do", param);
            handler.parse();
            title = handler.getTitle();
        }

        System.out.println("[tvpot] 영상 내려받는 중...");
        System.out.println("영상 타이틀: " + UrlUtil.validateFilename(title));
        System.out.println("영상 해상도: " + videoRes[metaData.size() - 1]);
        System.out.println("파일 사이즈: " + (float) (vid_fsz[metaData.size() - 1] / (1024 * 1024)) + "MB");


        hutil.setClientConnection(cdnUrl[metaData.size() - 1]);

        return hutil.requestByGet().writeFile("[tvpot]" + UrlUtil.validateFilename(title) + ".mp4");
    }

    private class XmlHandler extends DefaultHandler {
        private SAXParserFactory factory;  //Parser factory
        private SAXParser parser; //XML Parser
        private String url;
        List<NameValuePair> param;
        private String startTagName, endTagName;

        private StringBuffer buf = new StringBuffer();

        private String retUrl;
        private String retTitle;

        public XmlHandler() {

        }

        public XmlHandler(String url) {
            super();
            this.url = url;
            try {
                factory = SAXParserFactory.newInstance();
                parser = factory.newSAXParser();
            } catch (Exception e) {
                ExceptionReportUtil.reportExceptionToFile(e);
            }
        }

        public XmlHandler(String url, List<NameValuePair> param) {
            this(url);
            this.param = param;
        }

        public void startDocument() {

        }

        public void endDocument() {

        }

        public void startElement(String url, String name, String elementName, Attributes attr) {
            startTagName = elementName;
            buf.setLength(0);
        }

        public void characters(char[] str, int start, int len) {
            buf.append(str, start, len);
            if (this.startTagName.equals("url")) {
                retUrl = buf.toString().trim();
            } else if (this.startTagName.equals("TITLE")) {
                retTitle = buf.toString().trim();
            }
        }

        public void endElement(String url, String localName, String name) {
            endTagName = name;
        }

        public void parse() {
            try {
                hutil.setClientConnection(url);
                if (!param.isEmpty()) {
                    hutil.setConnectionParameter(param);
                }
                parser.parse(hutil.requestByGet().getAsInputStream(), this);
            } catch (Exception e) {
                ExceptionReportUtil.reportExceptionToFile(e);
            }
        }

        public String getUrl() {
            return retUrl;
        }

        public String getTitle() {
            return retTitle;
        }
    }
}
