package com.tugalsan.api.email.server;

import module com.tugalsan.api.function;
import module com.tugalsan.api.union;
import java.nio.file.Path;

public class TS_EMailConvertUtils {

    private TS_EMailConvertUtils() {

    }

    public static TGS_UnionExcuseVoid convertMSG2EML(Path pathInputMsg, Path pathOutputEml) {
        return TGS_FuncMTCUtils.call(() -> {

            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

}
