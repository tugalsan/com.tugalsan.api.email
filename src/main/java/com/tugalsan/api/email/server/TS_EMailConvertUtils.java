package com.tugalsan.api.email.server;

import module org.apache.poi.scratchpad;
import module org.simplejavamail;
import module com.tugalsan.api.function;
import module com.tugalsan.api.union;
import java.nio.file.Files;
import java.nio.file.Path;

public class TS_EMailConvertUtils {

    private TS_EMailConvertUtils() {

    }

    private static void addRecipients(EmailPopulatingBuilder builder, MAPIMessage msg) throws ChunkNotFoundException {
        // TO
        var toRaw = msg.getRecipientEmailAddress();
        if (toRaw != null && !toRaw.isBlank()) {
            for (String addr : toRaw.split(";")) {
                builder.to(addr.trim());
            }
        }
        String ccRaw = msg.getDisplayCC();
        if (ccRaw != null && !ccRaw.isBlank()) {
            for (String addr : ccRaw.split(";")) {
                builder.cc(addr.trim());
            }
        }
        String bccRaw = msg.getDisplayBCC();
        if (bccRaw != null && !bccRaw.isBlank()) {
            for (String addr : bccRaw.split(";")) {
                builder.bcc(addr.trim());
            }
        }
    }

    public static TGS_UnionExcuseVoid convertMSG2EML(Path pathInputMsg, Path pathOutputEml) {
        return TGS_FuncMTCUtils.call(() -> {
            var input = new MAPIMessage(pathInputMsg.toFile());
            var builder = EmailBuilder.startingBlank().withSubject(input.getSubject());
            // Body: prefer HTML if available, fallback to plain text
            var htmlBody = input.getHtmlBody();
            var textBody = input.getTextBody();
            if (htmlBody != null && !htmlBody.isBlank()) {
                builder.withHTMLText(htmlBody);
                if (textBody != null && !textBody.isBlank()) {
                    builder.withPlainText(textBody);
                }
            } else {
                builder.withPlainText(textBody != null ? textBody : "");
            }
            // Sender
            var from = input.getDisplayFrom();
            if (from != null && !from.isBlank()) {
                builder.from(from);
            }
            // Recipients
            addRecipients(builder, input);
            // Attachments
            for (var a : input.getAttachmentFiles()) {
                var fn = a.getAttachLongFileName() != null
                        ? a.getAttachLongFileName().toString()
                        : (a.getAttachFileName() != null ? a.getAttachFileName().toString() : "attachment");
                var fd = a.getAttachData() != null ? a.getAttachData().getValue() : null;
                if (fd != null) {
                    builder.withAttachment(fn, fd, "application/octet-stream");
                }
            }
            // Save as EML
            try (var fos = Files.newOutputStream(pathOutputEml)) {
                EmailConverter.emailToMimeMessage(builder.buildEmail()).writeTo(fos);
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> TGS_UnionExcuseVoid.ofExcuse(e));
    }

}
