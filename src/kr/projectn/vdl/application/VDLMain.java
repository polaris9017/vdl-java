package kr.projectn.vdl.application;

import kr.projectn.vdl.module.ModuleLoader;
import kr.projectn.vdl.module.ModuleMetaInfo;
import kr.projectn.vdl.utils.*;
import org.apache.commons.cli.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * vdl Main class
 * Created by qscx9512 on 2017-05-03.
 */
public class VDLMain implements ModuleMetaInfo {

    private static final int main = 2;
    private static final int min = 0;
    private static final int rev = 0;
    private static final String code = "Alwayz";
    private static final String verString = "020000";
    private static String url;
    private int magic;
    public static HttpUtil util;
    public static RegexUtil regex;

    /**
     * Default constructor
     */
    public VDLMain() {
        util = HttpUtil.getInstance();
        regex = RegexUtil.getInstance();
    }

    /**
     * Prints module version string
     */
    @Override
    public void getVersionString() {
        System.out.println("VDL Main Program ver. " + main + "." + min + "." + rev + " (build 260)");
    }

    /**
     * Prints version for all modules
     */
    public void printVersion() {
        this.getVersionString();
        ModuleLoader.printModuleVersion();
    }

    /**
     * Print initial text
     */
    private void printInit() {
        PrintUtil.seperateLine(80);
        System.out.println("V App 영상 다운로더 ver. " + main + "." +
                + min+ "." + rev + " by Moonrise° (DCInside 러블리즈 갤러리)");
        System.out.println("사용법은 게시글을 참조해주세요.");
        System.out.println("Codename " + code);
        PrintUtil.seperateLine(80);
        System.out.print("\n\n");
    }

    /**
     * Print notice get from server
     */
    private void printNotice() {
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        param.add(new BasicNameValuePair("ver", verString));
        param.add(new BasicNameValuePair("codename", code));
        util.setClientConnection("https://projectn.kr/notice.php");
        util.setConnectionParameter(param);

        System.out.println("<공지사항>");
        PrintUtil.seperateLine(80);

        PrintUtil.printWarning(util.requestByGet().getAsString() + "\n");

        PrintUtil.seperateLine(80);
    }

    /**
     * Get user URL input
     */
    private void setUrl() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("URL: ");
            setUrl(in.readLine());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            ExceptionReportUtil.reportExceptionToFile(e);
        }
    }

    /**
     * Set URL to instance variable
     *
     * @param url URL which user given
     */
    public void setUrl(String url) {
        this.url = url;
    }

    private void setModuleId() {
        UrlUtil util = new UrlUtil();
        magic = util.setModuleId(url);
    }

    /**
     * Parse command line arguments
     *
     * @param args command line argument
     * @return <b>true</b> if successfully parsed
     */
    private boolean parseCommand(String[] args) {
        CommandLine com;
        Options opt;
        CommandLineParser parser;

        opt = new Options();
        opt.addOption("h", "help", false, "Show Options");
        opt.addOption("v", "version", false, "Show version");
        opt.addOption(Option.builder("d")
                .required(false)
                .longOpt("download")
                .desc("Directly download from URL")
                .valueSeparator('=')
                .hasArg(true)
                .numberOfArgs(1)
                .argName("url")
                .build());

        parser = new DefaultParser();
        com = null;

        try {
            com = parser.parse(opt, args);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            ExceptionReportUtil.reportExceptionToFile(e);
        }

        if (com.hasOption("h") || com.hasOption("help")) {
            HelpFormatter format = new HelpFormatter();
            format.printHelp("vdl", opt);
            return false;
        } else if (com.hasOption("v") || com.hasOption("version")) {
            printVersion();
            return false;
        } else if (com.hasOption("d") || com.hasOption("download")) {
            if (com.hasOption("d")) {
                setUrl(com.getOptionValue("d"));
            } else {
                setUrl(com.getOptionValue("download"));
            }
            return true;
        }
        return true;
    }

    public static void main(String[] args) {

        VDLMain m = new VDLMain();

        if (!m.parseCommand(args)) {
            System.exit(0);
        }

        m.printInit();
        m.printNotice();

        if (m.parseCommand(args) && url == null) {
            m.setUrl();
        }

        m.setModuleId();

        ModuleLoader loader = new ModuleLoader(m.url, m.magic);
        loader.loadModule();
    }
}
