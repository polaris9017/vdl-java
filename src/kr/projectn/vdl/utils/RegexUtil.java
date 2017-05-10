package kr.projectn.vdl.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kim.K on 2017-05-03.
 */
public class RegexUtil {
    private String regex;

    public RegexUtil(String regex) {
        this.regex = regex;
    }

    public Matcher Parse(String str) {
        Pattern regPattern = Pattern.compile(regex);
        Matcher m = regPattern.matcher(str);
        m.find();
        return m;
    }
}
