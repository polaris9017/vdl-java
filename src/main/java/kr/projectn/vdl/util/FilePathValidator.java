package kr.projectn.vdl.util;

import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.io.IOException;

public final class FilePathValidator {
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

    public static String validateFilename(String path) {
        String path_decoded;

        path_decoded = EmojiParser.parseToHtmlDecimal(path);

        return path_decoded.replaceAll("[:\\\\/*\"?|<>]", "_");
    }
}
