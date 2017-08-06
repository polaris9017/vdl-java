package kr.projectn.vdl.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regular expression utility class
 *
 * Created by qscx9512 on 2017-05-03.
 */

public class RegexUtil {
    private String regex;

    private RegexUtil() {
    }

    private static class Singleton {
        private static final RegexUtil instance = new RegexUtil();
    }

    public static RegexUtil getInstance() {
        return Singleton.instance;
    }

    public RegexUtil setRegexString(String str) {
        regex = str;
        return this;
    }

    public Matcher Parse(String str) {
        Pattern regPattern = Pattern.compile(regex);
        Matcher m = regPattern.matcher(str);
        m.find();
        return m;
    }
}
