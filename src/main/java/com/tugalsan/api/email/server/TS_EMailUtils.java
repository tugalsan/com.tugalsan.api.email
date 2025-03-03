package com.tugalsan.api.email.server;

import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.function.client.maythrow.checkedexceptions.TGS_FuncMTCEUtils;
import java.util.*;
import java.nio.file.*;
import jakarta.activation.*;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import com.tugalsan.api.string.client.TGS_StringUtils;
import com.tugalsan.api.union.client.TGS_UnionExcuseVoid;

public class TS_EMailUtils {
    
    private TS_EMailUtils(){
        
    }

//    final private static TS_Log d = TS_Log.of(TS_EMailUtils.class);
    public static TGS_UnionExcuseVoid send(Properties properties,
            CharSequence fromEmail, CharSequence fromText, CharSequence password,
            CharSequence toEmails, CharSequence subjectText,
            CharSequence optionalFontCss, CharSequence bodyHtml, List<MimeBodyPart> files) {
        return send(properties, fromEmail, fromText, password, toEmails, subjectText, optionalFontCss, bodyHtml, files.toArray(MimeBodyPart[]::new));
    }

    public static TGS_UnionExcuseVoid send(Properties properties,
            CharSequence fromEmail, CharSequence fromText, CharSequence password,
            CharSequence toEmails, CharSequence subjectText,
            CharSequence optionalFontCss, CharSequence bodyHtml, MimeBodyPart... files) {

        return TGS_FuncMTCEUtils.call(() -> {
            var auth = createAuthenticator(fromEmail, password);
            var session = Session.getInstance(properties, auth);
            var msg = createMimeMessage(session, fromEmail, fromText, toEmails, subjectText);
            var mp = new MimeMultipart();
            var mbp = new MimeBodyPart();
            mbp.setContent("<p " + TGS_StringUtils.cmn().toEmptyIfNull(optionalFontCss) + ">" + bodyHtml + "</p>", "text/html; charset=utf-8");
            mp.addBodyPart(mbp);
            Arrays.stream(files).forEachOrdered(file -> TGS_FuncMTCEUtils.run(() -> mp.addBodyPart(file)));
            msg.setContent(mp);
            Transport.send(msg);
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> {
            return TGS_UnionExcuseVoid.ofExcuse(e);
        });
    }

    public static MimeMessage createMimeMessage(Session session, CharSequence fromEmail, CharSequence fromText, CharSequence toEmails, CharSequence subjectText) {
        return TGS_FuncMTCEUtils.call(() -> {
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
        return TGS_FuncMTCEUtils.call(() -> {
            var mbp = new MimeBodyPart();
            mbp.setDataHandler(new DataHandler(new FileDataSource(path.toAbsolutePath().toString())));
            mbp.setFileName(filename);
            return mbp;
        });
    }

    public static MimeBodyPartsImg createMimeBodyPartsImg(Path path, int idx) {
        return TGS_FuncMTCEUtils.call(() -> {
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

    public static Authenticator createAuthenticator(CharSequence fromEmail, CharSequence password) {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail.toString(), password.toString());
            }
        };
    }

    public static Properties createPropertiesSSL(CharSequence smtpServer, boolean checkServerIdentity) {
        var props = new Properties();
        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        if (checkServerIdentity) {
            props.put("mail.smtp.ssl.checkserveridentity", true);
        }
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        return props;
    }

    public static Properties createPropertiesTLS(CharSequence smtpServer) {
        var props = new Properties();
        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        return props;
    }

    public static Properties createPropertiesSimple(CharSequence smtpServer) {
        var props = System.getProperties();
        props.put("mail.smtp.host", smtpServer);
        return props;
    }
}
