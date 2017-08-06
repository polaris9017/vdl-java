package kr.projectn.vdl.utils;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;

/**
 * Line printing utility class
 *
 * Created by qscx9512 on 2017-05-03.
 */

public class PrintUtil {

    public static void printError(String str) {
        ColoredPrinter p = new ColoredPrinter.Builder(1, false).build();
        p.debugPrint(str,
                2,
                Ansi.Attribute.NONE,
                Ansi.FColor.RED,
                Ansi.BColor.NONE);
        p.clear();
    }

    public static void printWarning(String str) {
        ColoredPrinter p = new ColoredPrinter.Builder(1, false).build();
        p.println(str, Ansi.Attribute.NONE,
                Ansi.FColor.YELLOW,
                Ansi.BColor.NONE);
        p.clear();
    }

    public static void seperateLine(int len) {
        for(int i = 0; i < len;i++) {
            System.out.print("=");
        }
        System.out.println("");
    }
}
