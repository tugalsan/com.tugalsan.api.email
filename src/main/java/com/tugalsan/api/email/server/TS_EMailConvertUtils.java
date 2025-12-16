package com.tugalsan.api.email.server;

import com.tugalsan.api.log.server.TS_Log;

import module com.tugalsan.api.function;
import module com.tugalsan.api.union;
import module org.simplejavamail;
import java.nio.file.*;

public class TS_EMailConvertUtils {

    final private static TS_Log d = TS_Log.of(TS_EMailConvertUtils.class);

    private TS_EMailConvertUtils() {

    }

//    @Deprecated //NOT WORKING
//    public static TGS_UnionExcuseVoid convertMSG2EML(Path pathInputMsg, Path pathOutputEml) {
//        return TGS_FuncMTCUtils.call(() -> {
//            d.cr("convertMSG2EML", "#0");
//            System.setProperty("org.simplejavamail.debug", "true");
//            var emlString = EmailConverter.outlookMsgToEML(pathInputMsg.toFile());
//            d.cr("convertMSG2EML", "#1");
//            try (var writer = Files.newBufferedWriter(pathOutputEml)) {
//                d.cr("convertMSG2EML", "#2");
//                writer.write(emlString);
//                d.cr("convertMSG2EML", "emlString", emlString);
//            }
//            d.cr("convertMSG2EML", "#4");
//            return TGS_UnionExcuseVoid.ofVoid();
//        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
//    }

}
