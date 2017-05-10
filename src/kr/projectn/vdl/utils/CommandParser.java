package kr.projectn.vdl.utils;

import org.apache.commons.cli.*;
import kr.projectn.vdl.application.VDLMain;

/**
 * Created by Kim.K on 2017-05-03.
 */
public class CommandParser {

    private CommandLine com;
    private Options opt;

    public CommandParser() {
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
    }

    public boolean Parse(String[] arg) {
        CommandLineParser parser = new DefaultParser();
        VDLMain m = new VDLMain();
        try {
            com = parser.parse(opt, arg);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            System.err.println("[CommandParse] Error parsing arguments.");
            System.err.println("Message" + e.getMessage());
            e.printStackTrace();
        }

        if(com.hasOption("h") || com.hasOption("help")) {
            HelpFormatter format = new HelpFormatter();
            format.printHelp("vdl", opt);
            return false;
        }
        else if(com.hasOption("v") || com.hasOption("version")) {
            m.printVersion();
            return false;
        }
        else if(com.hasOption("d") || com.hasOption("download")) {
            if(com.hasOption("d")) {
                m.setUrl(com.getOptionValue("d"));
            }
            else {
                m.setUrl(com.getOptionValue("download"));
            }
            return true;
        }

        return true;
    }
}
