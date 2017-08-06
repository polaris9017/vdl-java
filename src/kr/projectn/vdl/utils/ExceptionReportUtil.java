package kr.projectn.vdl.utils;

import java.io.*;
import java.sql.Timestamp;
import java.util.Arrays;

public class ExceptionReportUtil {
    public static void reportExceptionToFile(Exception e) {
        String logTimestamp = "error-" + new Timestamp(System.currentTimeMillis() / 1000L).getTime() + ".log";
        String errorMessage = e.getMessage() + "\n\n" + Arrays.toString(e.getStackTrace());
        try {
            InputStream ret = new ByteArrayInputStream(errorMessage.getBytes());
            BufferedInputStream in = new BufferedInputStream(ret);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(logTimestamp)));
            int inByte;
            while ((inByte = in.read()) != -1) out.write(inByte);
            in.close();
            out.close();
        } catch (UnsupportedOperationException | IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        System.out.println("Exception reported to " + logTimestamp);
    }
}
