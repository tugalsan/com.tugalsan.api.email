package com.tugalsan.api.email.server;

import com.tugalsan.api.file.server.TS_FileUtils;
import java.util.*;
import java.nio.file.*;
import jakarta.activation.*;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import com.tugalsan.api.string.client.TGS_StringUtils;
import com.tugalsan.api.union.client.TGS_Union;
import java.io.UnsupportedEncodingException;

public class TS_EMailUtils {

//    final private static TS_Log d = TS_Log.of(TS_EMailUtils.class);
    public static TGS_Union<Boolean> send(Properties properties,
            CharSequence fromEmail, CharSequence fromText, CharSequence password,
            CharSequence toEmails, CharSequence subjectText,
            CharSequence optionalFontCss, CharSequence bodyHtml, MimeBodyPart... files) {
        try {
            var auth = createAuthenticator(fromEmail, password);
            var session = Session.getInstance(properties, auth);
            var u_msg = createMimeMessage(session, fromEmail, fromText, toEmails, subjectText);
            if (u_msg.isExcuse()) {
                return TGS_Union.ofExcuse(u_msg.excuse());
            }
            var msg = u_msg.value();
            var mp = new MimeMultipart();
            var mbp = new MimeBodyPart();
            mbp.setContent("<p " + TGS_StringUtils.toEmptyIfNull(optionalFontCss) + ">" + bodyHtml + "</p>", "text/html; charset=utf-8");
            mp.addBodyPart(mbp);
            for (var file : files) {
                mp.addBodyPart(file);
            }
            msg.setContent(mp);
            Transport.send(msg);
            return TGS_Union.of(true);
        } catch (MessagingException ex) {
            return TGS_Union.ofExcuse(ex);
        }
    }

    public static TGS_Union<MimeMessage> createMimeMessage(Session session, CharSequence fromEmail, CharSequence fromText, CharSequence toEmails, CharSequence subjectText) {
        try {
            var msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setSentDate(new Date());

            msg.setFrom(new InternetAddress(fromEmail.toString(), fromText.toString()));
            msg.setReplyTo(InternetAddress.parse(fromEmail.toString(), false));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmails.toString(), false));

            msg.setSubject(subjectText.toString(), "UTF-8");
            return TGS_Union.of(msg);
        } catch (MessagingException | UnsupportedEncodingException ex) {
            return TGS_Union.ofExcuse(ex);
        }
    }

    public static String createBasicFontCss() {
        return "style = \"font-family: fontText, Arial Unicode MS, Arial,Helvetica,sans-serif;font-size:11px;\"";
    }

    public static TGS_Union<MimeBodyPart> createMimeBodyPartFile(Path path, int idx) {
        try {
            var mbp = new MimeBodyPart();
            mbp.setDataHandler(new DataHandler(new FileDataSource(path.toAbsolutePath().toString())));
            mbp.setFileName("file" + idx + "." + TS_FileUtils.getNameType(path));
            return TGS_Union.of(mbp);
        } catch (MessagingException ex) {
            return TGS_Union.ofExcuse(ex);
        }
    }

    public static TGS_Union<MimeBodyPartsImg> createMimeBodyPartsImg(Path path, int idx) {
        try {
            var imgId = "img" + idx;
            var mbps = new MimeBodyPartsImg("<img src='cid:" + imgId + "'>", new MimeBodyPart());
            mbps.bp.setHeader("Content-ID", imgId);
            mbps.bp.setDisposition(MimeBodyPart.INLINE);
            mbps.bp.setDataHandler(new DataHandler(new FileDataSource(path.toAbsolutePath().toString())));
            mbps.bp.setFileName(imgId + "." + TS_FileUtils.getNameType(path));
            return TGS_Union.of(mbps);
        } catch (MessagingException ex) {
            return TGS_Union.ofExcuse(ex);
        }
    }

    public static record MimeBodyPartsImg(String htmlcode_id, MimeBodyPart bp) {

    }

    public static Authenticator createAuthenticator(CharSequence fromEmail, CharSequence password) {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail.toString(), password.toString());
            }
        };
    }

    public static Properties createPropertiesSSL(CharSequence smtpServer) {
        var props = new Properties();
        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
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
