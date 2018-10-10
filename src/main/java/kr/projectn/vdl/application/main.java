package kr.projectn.vdl.application;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import kr.projectn.vdl.core.*;
import kr.projectn.vdl.core.util.WebClient;
import kr.projectn.vdl.event.EventListener;
import kr.projectn.vdl.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class main {
    private static final String version = "v2.1.0";
    private static final String version_core = "v1.0.2";
    private static final String code = "Muse";

    private static void printVersion() {
        System.out.println("vdl "+ version + " / " + code);
        System.out.println("vdl core API library " + version_core);
    }

    private static void printInit() {
        System.out.println(String.format("%80s", " ").replace(' ', '*'));
        System.out.println("VApp 영상 다운로더 " + version + " by Moonrise° (DCInside 러블리즈 갤러리)");
        System.out.println("사용법은 게시글 참조");
        System.out.println("Codename " + code);
        System.out.println(String.format("%80s", " ").replace(' ', '*'));
        System.out.println("\n");
    }

    public static void main(String[] args) {
        boolean hasSubtitle = false;
        Logger logger = LogManager.getLogger(kr.projectn.vdl.application.main.class);

        try {
            SubmoduleLoader loader;
            WebClient client = new WebClient();
            RequestBuilder builder = new RequestBuilder();
            LinkedList<Response> responseList = new LinkedList<>();

            EventListener listener = new EventListener();
            Queue<Request> requestList = new LinkedList<>();
            Queue<String> downloadList = new LinkedList<>();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));


            logger.debug("Parsing command. arg: " + Arrays.toString(args));

            if (CommandParser.isHelp(args)) {
                logger.debug("Printing help. End of log");
                CommandParser.printHelp(args);
                return;
            } else if (CommandParser.isVersion(args)) {
                logger.debug("Printing version. End of log");
                printVersion();
                return;
            } else if (CommandParser.hasSubtitle(args)) {
                hasSubtitle = true;
            }

            printInit();

            logger.debug("Adding URL from user");
            if (CommandParser.getDownloadList(args).isEmpty()) {
                while (true){
                    String url;
                    System.out.print("URL: ");

                    if (Objects.equal((url = in.readLine()), ";")) {
                        break;
                    } else {
                        downloadList.offer(URLValidator.validate(url));
                    }
                }
            } else {
                downloadList = CommandParser.getDownloadList(args);
            }

            logger.info("Listing all added URL");
            for (String url : downloadList) {
                int start, end;
                List<String> indexList;

                if (URLValidator.hasList(url)) {
                    logger.debug("Get list user index of " + url);

                    System.out.println(url + " contains list.");
                    System.out.print("Enter start and end index. (Latest index is 0) (i.e. 3, 7): ");

                    indexList = Splitter.on(',')
                            .trimResults()
                            .omitEmptyStrings()
                            .limit(2)
                            .splitToList(in.readLine());

                    start = Integer.parseInt(indexList.get(0));
                    end = Integer.parseInt(indexList.get(1));

                    logger.debug("Adding list Request instance. url: {}, index: {} / {}", url, start, end);
                    requestList.offer(new RequestBuilder()
                            .setUrl(url)
                            .setListener(listener)
                            .build(start, end));
                } else {
                    builder.setUrl(url);
                }
            }

            logger.debug("Adding ordinary Request instance.");
            requestList.offer(builder.setListener(new EventListener())
                    .build());

            logger.info("Running submodules");
            for (Request request : requestList) {
                loader = new SubmoduleLoader(request);
                loader.run();

                logger.debug("Appending Response instance to internal list");
                for (Response response : loader.getResponseList()) {
                    responseList.offer(response);
                }
            }

            logger.debug("Check whether HLS response is contained");
            for (Response el : responseList) {
                String op = "";
                if (Objects.equal(el.getClass(), HLSResponse.class)) {
                    System.out.println("URL you requested contains HLS streaming media.");
                    System.out.println("Put 'y' to record streaming video, 'n' to ignore this: ");

                    try {
                        op = in.readLine();
                        while ((!Objects.equal(op, "y")) ||
                                (!Objects.equal(op, "n"))) {
                            System.err.println("Illegal option (y/n): ");
                            op = in.readLine();
                        }
                    } catch (IOException e) {
                        ExceptionCollector.collect(e);
                    }

                    if (op.equals("y")) {
                        HLSResponse response = (HLSResponse) el;
                        responseList.clear();
                        responseList.offer(response);
                    } else {
                        responseList.removeFirstOccurrence(el);
                    }

                    break;
                }
            }

            int idx = 1;

            for (Response el : responseList) {
                if (el.getTitleList().isEmpty()) {
                    for (int i = 1; i <= el.getUrlList().size(); i++) {
                        el.setTitle(URLValidator.getSocialPostCode(downloadList.peek()) + "-" + i);
                    }
                }

                if (Objects.equal(el.getClass(), HLSResponse.class)) {
                    while (((HLSResponse)el).getHLSSegment()) {
                        for (String url : ((HLSResponse)el).getVideoUrlQueue()) {
                            client.setClientConnection(url);
                            client.request().writeFile(el.getTitleList().peek());
                        }
                    }
                }

                for (String url : el.getUrlList()) {
                    int idx_sub = 1;

                    String title = el.getTitleList().poll();
                    logger.info("Downloading video (" + idx++ + "/" + responseList.size() + ")");
                    client.setClientConnection(url);
                    if (URLValidator.getSocialPostCode(url).isEmpty()) {
                        client.request().writeFile(FilePathValidator.validateFilename(title) + ".mp4");
                    } else{
                        client.request().writeFile(URLValidator.getSocialPostCode(url));
                    }

                    if (hasSubtitle) {
                        for (Response.Subtitle sub : el.getSubtitleList(downloadList.peek())) {

                            String locale = sub.getLocale();

                            logger.info("   - Downloading subtitle of video ("
                                    + idx_sub++ + "/" + el.getSubtitleList(downloadList.peek()).size() + ")");
                            logger.debug("Downloading subtitle ==> idx: " + idx_sub + " URL: " + url + " / locale: " + locale);


                            client.setClientConnection(sub.getSource()).request().writeFile(title + "_" + locale + ".vtt");
                        }
                    }
                }

                downloadList.poll();
            }
        } catch (Exception e) {
            ExceptionCollector.collect(e);
        }
    }
}