package com.tugalsan.api.email.server;

import module com.tugalsan.api.function;
import module com.tugalsan.api.union;
import module org.simplejavamail;
import java.nio.file.*;

public class TS_EMailConvertUtils {

    private TS_EMailConvertUtils() {

    }

    public static TGS_UnionExcuseVoid convertMSG2EML(Path pathInputMsg, Path pathOutputEml) {
        return TGS_FuncMTCUtils.call(() -> {
            String emlString = EmailConverter.outlookMsgToEML(pathInputMsg.toFile());
            try (var writer = Files.newBufferedWriter(pathOutputEml)) {
                writer.write(emlString);
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

}
