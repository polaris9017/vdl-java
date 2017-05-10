package kr.projectn.vdl.utils;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;

/**
 * Created by Kim.K on 2017-05-03.
 */
public class PrintUtil {
    private ColoredPrinter p;

    public PrintUtil() {
        p = new ColoredPrinter.Builder(1, false).build();
    }

    public void printError(String str) {
        p.debugPrint(str,
                2,
                Ansi.Attribute.NONE,
                Ansi.FColor.RED,
                Ansi.BColor.NONE);
        p.clear();
    }

    public void printWarning(String str) {
        p.println(str, Ansi.Attribute.NONE,
                Ansi.FColor.YELLOW,
                Ansi.BColor.NONE);
        p.clear();
    }

    public void seperateLine(int len) {
        for(int i = 0; i < len;i++) {
            System.out.print("=");
        }
        System.out.println("");
    }
}
