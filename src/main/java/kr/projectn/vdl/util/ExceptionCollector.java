package kr.projectn.vdl.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public final class ExceptionCollector {
    private static Logger logger = LogManager.getRootLogger();
    public static void collect(Exception e) {
        Writer writer = new StringWriter();

        e.printStackTrace(new PrintWriter(writer));

        System.err.println("Exception occured! Message: " + e.getMessage());
        System.err.println("Check log for detail.");
        logger.throwing(e);
    }

    public static void collect(String msg) {
        System.err.println("Exception occured! Message: " + msg);
    }
}
