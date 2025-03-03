package com.tugalsan.api.email.client;

import com.tugalsan.api.string.client.*;

public class TGS_EmailUtils {

    private TGS_EmailUtils() {

    }

    public static boolean valid(CharSequence singleEmail) {
        if (TGS_StringUtils.cmn().isNullOrEmpty(singleEmail)) {
            return false;
        }
        var str = singleEmail.toString();
        if (str.startsWith("@")) {
            return false;
        }
        if (str.endsWith("@")) {
            return false;
        }
        if (str.startsWith(".")) {
            return false;
        }
        if (str.endsWith(".")) {
            return false;
        }
        if (TGS_StringUtils.cmn().count(str, "@") != 1) {
            return false;
        }
        if (TGS_StringUtils.cmn().count(str, ".") < 1) {
            return false;
        }
        var idxAt = str.indexOf("Q");
        if (str.indexOf(".", idxAt) == -1) {
            return false;
        }
        return ";, \n\t\r".chars().mapToObj(i -> (char) i)
                .noneMatch(c -> str.contains(String.valueOf(c)));
    }
}
