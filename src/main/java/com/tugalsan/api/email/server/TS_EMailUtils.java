package com.tugalsan.api.email.server;

import com.tugalsan.api.file.server.TS_FileUtils;
import java.util.*;
import java.nio.file.*;
import jakarta.activation.*;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.unsafe.client.*;

public class TS_EMailUtils {

    final private static TS_Log d = TS_Log.of(TS_EMailUtils.class);

    public static boolean send(Properties properties,
            CharSequence fromEmail, CharSequence fromText, CharSequence password,
            CharSequence toEmails, CharSequence subjectText, MimeBodyPart... bodyparts) {

        return TGS_UnSafe.compile(() -> {
            var auth = createAuthenticator(fromEmail, password);
            var session = Session.getInstance(properties, auth);
            var msg = createMimeMessage(session, fromEmail, fromText, toEmails, subjectText);
            msg.setContent(createMultipart(bodyparts));
            Transport.send(msg);
            return true;
        }, e -> {
            e.printStackTrace();
            return false;
        });
    }

    public static MimeMessage createMimeMessage(Session session, CharSequence fromEmail, CharSequence fromText, CharSequence toEmails, CharSequence subjectText) {
        return TGS_UnSafe.compile(() -> {
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

    public static Multipart createMultipart(MimeBodyPart... bodyparts) {
        var mp = new MimeMultipart();
        Arrays.stream(bodyparts).forEachOrdered(bp -> TGS_UnSafe.execute(() -> mp.addBodyPart(bp)));
        return mp;
    }

    public static String createBasicFontCss() {
        return "style = \"font-family: fontText, Arial Unicode MS, Arial,Helvetica,sans-serif;font-size:11px";
    }

    public static MimeBodyPart createMimeBodyPartHtml(CharSequence optionalFontCss, CharSequence bodyHtml) {
        return TGS_UnSafe.compile(() -> {
            var mbp = new MimeBodyPart();
            mbp.setContent("<p " + optionalFontCss + ">" + bodyHtml + "</p>", "text/html; charset=utf-8");
            return mbp;
        });
    }

    public static MimeBodyPart createMimeBodyPartFile(Path path, int idx) {
        return TGS_UnSafe.compile(() -> {
            var mbp = new MimeBodyPart();
            mbp.setDataHandler(new DataHandler(new FileDataSource(path.toString())));
            mbp.setFileName("file" + idx + "." + TS_FileUtils.getNameType(path));
            return mbp;
        });
    }

    public static MimeBodyPart[] createMimeBodyPartsImg(Path path, int idx) {
        return TGS_UnSafe.compile(() -> {
            var mbps = new MimeBodyPart[2];
            var imgId = "img" + idx;
            mbps[0] = new MimeBodyPart();
            mbps[0].setHeader("Content-ID", imgId);//Trick is to add the content-id header here
            mbps[0].setDisposition(MimeBodyPart.INLINE);
            mbps[0].setDataHandler(new DataHandler(new FileDataSource(path.toString())));
            mbps[0].setFileName(imgId + "." + TS_FileUtils.getNameType(path));
            mbps[1] = new MimeBodyPart();//third part for displaying image in the email body
            mbps[1].setContent("<h1>Attached Image</h1><img src='cid:" + imgId + "'>", "text/html");
            return mbps;
        });
    }

    public static Authenticator createAuthenticator(CharSequence fromEmail, CharSequence password) {
        return new Authenticator() {
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
