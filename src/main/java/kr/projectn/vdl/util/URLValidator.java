package kr.projectn.vdl.util;

import com.google.common.base.Objects;
import kr.projectn.vdl.core.frame.ServiceType;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public final class URLValidator {
    public static String validate(String url) {
        if (!url.contains("http") || !url.contains("https")) {
            return "http://" + url;
        }
        return url;
    }

    public static boolean hasList(String url) {
        return Objects.equal(ServiceType.findServiceByURL(url), ServiceType.VLIVE_CHANNEL);
    }

    //Taken from http://www.rgagnon.com/javadetails/java-check-if-a-filename-is-valid.html
    public static boolean validateFilePath(String path) {
        File file = new File(path);
        try {
            file.getCanonicalPath();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String getSocialPostCode(String url) {
        String code = "";

        try {
            String path = new URI(url).getPath();

            switch (ServiceType.findServiceByURL(url)) {
                case INSTAGRAM:
                    code = path.substring(path.lastIndexOf('/')+1);
            }
        } catch (URISyntaxException e) {
            ExceptionCollector.collect(e);
        }finally {
            return code;
        }
    }
}
