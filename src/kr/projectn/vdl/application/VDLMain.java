package kr.projectn.vdl.application;

import kr.projectn.vdl.module.ModuleMetaInfo;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import kr.projectn.vdl.utils.*;
import kr.projectn.vdl.module.ModuleLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kim.K on 2017-05-03.
 */
public class VDLMain implements ModuleMetaInfo {

    private static final int main = 2;
    private static final int min = 0;
    private static final int rev = 0;
    private static final String code = "WoW";
    private static final String verString = "020000";
    private String url;
    private int magic;
    public static HttpUtil util;

    public VDLMain() {
        util = HttpUtil.getInstance();
    }

    @Override
    public void getVersionString() {
        System.out.println("VDL Main Program ver. " + main + "." + min + "." + rev + " (build 240)");
    }

    public void printVersion() {
        this.getVersionString();
    }

    private void printInit() {
        System.out.println("V App 영상 다운로더 ver. " + main + "." +
                + min+ "." + rev + " by Moonrise° (DCInside 러블리즈 갤러리)");
        System.out.println("사용법은 게시글을 참조해주세요.");
        System.out.println("Codename " + code);
    }

    private void printNotice() {
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        PrintUtil p = new PrintUtil();
        param.add(new BasicNameValuePair("ver", verString));
        param.add(new BasicNameValuePair("codename", code));
        util.setClientConnection("https://projectn.kr/notice.php");
        util.setConnectionParameter(param);

        System.out.println("<공지사항>");
        p.seperateLine(80);

        p.printWarning(util.requestByGet().getAsString() + "\n");

        p.seperateLine(80);
    }

    private void setUrl() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("URL: ");
            setUrl(in.readLine());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            PrintUtil p = new PrintUtil();
            p.printError("[main@" + new VDLMain().toString() + "]Error message: " + e.getMessage());
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private void setModuleId() {
        UrlUtil util = new UrlUtil();
        magic = util.setModuleId(url);
    }

    public static void main(String[] args) {
        CommandParser parser = new CommandParser();
        VDLMain m = new VDLMain();

        if(!parser.Parse(args)) {
            System.exit(0);
        }

        m.printInit();
        m.printNotice();

        if(parser.Parse(args)) {
            m.setUrl();
        }

        m.setModuleId();

        ModuleLoader loader = new ModuleLoader(m.url, m.magic);
        loader.loadModule();
    }
}
