package com.tugalsan.api.email.server;

import module com.tugalsan.api.file;
import module com.tugalsan.api.function;
import module jakarta.mail;
import module jakarta.activation;
import java.util.*;
import java.nio.file.*;

public class TS_EMailCreateUtils {

    private TS_EMailCreateUtils() {

    }

    public static MimeMessage createMimeMessage(Session session, CharSequence fromEmail, CharSequence fromText, CharSequence toEmails, CharSequence subjectText) {
        return TGS_FuncMTCUtils.call(() -> {
            var msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setSentDate(new Date());

            msg.setFrom(new InternetAddress(fromEmail.toString(), fromText.toString()));
            msg.setReplyTo(InternetAddress.parse(fromEmail.toString(), false));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmails.toString(), false));

            msg.setSubject(subjectText.toString(), "UTF-8");
            return msg;
        });
    }

    public static String createBasicFontCss() {
        return "style = \"font-family: fontText, Arial Unicode MS, Arial,Helvetica,sans-serif;font-size:11px;\"";
    }

    public static MimeBodyPart createMimeBodyPartFile(Path path, int idx) {
        return createMimeBodyPartFile(path, idx, "file" + idx + "." + TS_FileUtils.getNameType(path));
    }

    public static MimeBodyPart createMimeBodyPartFile(Path path, int idx, String filename) {
        return TGS_FuncMTCUtils.call(() -> {
            var mbp = new MimeBodyPart();
            mbp.setDataHandler(new DataHandler(new FileDataSource(path.toAbsolutePath().toString())));
            mbp.setFileName(filename);
            return mbp;
        });
    }

    public static MimeBodyPartsImg createMimeBodyPartsImg(Path path, int idx) {
        return TGS_FuncMTCUtils.call(() -> {
            var imgId = "img" + idx;
            var mbp = new MimeBodyPart();
            mbp.setHeader("Content-ID", imgId);
            mbp.setDisposition(MimeBodyPart.INLINE);
            mbp.setDataHandler(new DataHandler(new FileDataSource(path.toAbsolutePath().toString())));
            mbp.setFileName(imgId + "." + TS_FileUtils.getNameType(path));
            return new MimeBodyPartsImg("<img src='cid:" + imgId + "'>", mbp);
        });
    }

    public static record MimeBodyPartsImg(String html, MimeBodyPart mbp) {

    }
}
