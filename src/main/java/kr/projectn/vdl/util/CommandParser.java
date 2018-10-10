package kr.projectn.vdl.util;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;

public final class CommandParser {
    private static  Logger logger = LogManager.getLogger(kr.projectn.vdl.util.CommandParser.class);
    private static Options options = new Options()
            .addOption("h", "help", false, "prints options. "
                    + "Other options will be ignored.")
            .addOption("v", "version", false, "prints version. "
                    + "Other options will be ignored.")
            .addOption("s", "subtitle", false, "Download subtitle if possible")
            .addOption(Option.builder("l")
                    .longOpt("list")
                    .hasArgs()
                    .numberOfArgs(Option.UNLIMITED_VALUES)
                    .valueSeparator(' ')
                    .desc("Directly download from list."
                            + " Values are separated to ' '(Blank)")
                    .build());

    public static boolean parse(String[] args) {
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("l") || line.hasOption("list")) {
                return true;
            }
        } catch (ParseException e) {
            ExceptionCollector.collect(e);
        }
        return false;
    }

    public static boolean isHelp(String[] args) {
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("h") || line.hasOption("help")) {
                return true;
            }
        } catch (ParseException e) {
            ExceptionCollector.collect(e);
        }
        return false;
    }

    public static boolean isVersion(String[] args) {
        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("v") || line.hasOption("version")) {
                return true;
            }
        } catch (ParseException e) {
            ExceptionCollector.collect(e);
        }
        return false;
    }

    public static boolean hasSubtitle(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("s") || line.hasOption("subtitle")) {
                return true;
            }
        } catch (ParseException e) {
            ExceptionCollector.collect(e);
        }
        return false;
    }

    public static Queue<String> getDownloadList(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Queue<String> downloadList = new LinkedList<>();
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("l") || line.hasOption("list")) {
                if (line.hasOption("l")) {
                    for (String url : line.getOptionValues('l')) {
                        downloadList.offer(url);
                    }

                    return downloadList;
                }
                for (String url : line.getOptionValues("list")) {
                    downloadList.offer(url);
                }
            }
        } catch (ParseException e) {
            ExceptionCollector.collect(e);
        }

        return downloadList;
    }

    public static void printHelp(String[] args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("h") || line.hasOption("help")) {
                formatter.printHelp("vdl", options);
            }
        } catch (ParseException e) {
            ExceptionCollector.collect(e);
        }
    }
}
