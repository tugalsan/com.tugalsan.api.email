package com.tugalsan.api.email.server;

import module jakarta.mail;
import module org.apache.poi.scratchpad;
import module com.tugalsan.api.union;
import module com.tugalsan.api.function;
import java.nio.file.Files;
import java.util.*;
import java.nio.file.*;

public class TS_EMailConvertUtils {

    private TS_EMailConvertUtils() {

    }

    public static TGS_UnionExcuseVoid convertMSG2EML(Path inputMsg, Path outputEml) {
        return TGS_FuncMTCUtils.call(() -> {
            var msg = new MAPIMessage(inputMsg.toAbsolutePath().toString());
            var eml = new MimeMessage(Session.getDefaultInstance(new Properties()));
            eml.setSubject(msg.getSubject());
            if (msg.getRecipientEmailAddress() != null) {
                eml.setFrom(new InternetAddress(msg.getRecipientEmailAddress()));
            }
            if (msg.getRecipientEmailAddressList() != null) {
                for (var s : msg.getRecipientEmailAddressList()) {
                    eml.addRecipient(Message.RecipientType.TO, new InternetAddress(s));
                }
            }
            var multipart = new MimeMultipart();
            var textPart = new MimeBodyPart();
            textPart.setText(msg.getTextBody());
            multipart.addBodyPart(textPart);
            for (var a : msg.getAttachmentFiles()) {
                var attachPart = new MimeBodyPart();
                attachPart.setFileName(a.getAttachLongFileName().toString());
                attachPart.setContent(a.getAttachData().getValue(), "application/octet-stream");
                multipart.addBodyPart(attachPart);
            }
            eml.setContent(multipart);
            try (var fos = Files.newOutputStream(outputEml)) {
                eml.writeTo(fos);
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }
}
