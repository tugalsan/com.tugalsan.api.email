package com.tugalsan.api.email.server;

import module com.tugalsan.api.log;
import module com.tugalsan.api.function;
import module com.tugalsan.api.union;
import java.nio.file.*;

public class TS_EMailConvertUtils {

    final private static TS_Log d = TS_Log.of(TS_EMailConvertUtils.class);

    private TS_EMailConvertUtils() {

    }

    @Deprecated //NOT WORKING
    public static TGS_UnionExcuseVoid convertMSG2EML(Path pathInputMsg, Path pathOutputEml) {
        return TGS_FuncMTCUtils.call(() -> {
            //TODO
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

}
