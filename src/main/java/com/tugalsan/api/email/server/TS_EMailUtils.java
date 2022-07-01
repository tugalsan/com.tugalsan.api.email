package com.tugalsan.api.email.server;

import java.util.*;
import java.nio.file.*;
import jakarta.activation.*;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import com.tugalsan.api.file.server.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.unsafe.client.*;

public class TS_EMailUtils {

    final private static TS_Log d = TS_Log.of(TS_EMailUtils.class.getSimpleName());

    public static boolean sendEmailTextTLS(
            String smtpServer, String fromEmail, String fromPassword,
            String toEmails, String subjectText, String bodyText) {
        return sendEmailText(TS_EMailUtils.createPropertiesTLS(smtpServer),
                fromEmail, fromEmail, fromPassword,
                toEmails, subjectText, bodyText
        );
    }

    public static boolean sendEmailText(Properties properties,
            CharSequence fromEmail, CharSequence fromText, CharSequence password,
            CharSequence toEmails, CharSequence subjectText, CharSequence bodyText) {
        return TGS_UnSafe.compile(() -> {
            var auth = createAuthenticator(fromEmail, password);
            var session = Session.getInstance(properties, auth);
            var msg = createMimeMessage(session, fromEmail, fromText, toEmails, subjectText);
            msg.setText(bodyText.toString(), "UTF-8");
            Transport.send(msg);
            return true;
        }, e -> {
            e.printStackTrace();
            return false;
        });
    }

    public static boolean sendEmailContentTLS(
            String smtpServer, String fromEmail, String fromPassword,
            String toEmails, String subjectText, String bodyText) {
        return sendEmailContent(TS_EMailUtils.createPropertiesTLS(smtpServer),
                fromEmail, fromEmail, fromPassword,
                toEmails, subjectText, TS_EMailUtils.createMultipart(bodyText)
        );
    }

    public static boolean sendEmailContent(Properties properties,
            CharSequence fromEmail, CharSequence fromText, CharSequence password,
            CharSequence toEmails, CharSequence subjectText, Multipart bodyContent) {

        return TGS_UnSafe.compile(() -> {
            var auth = createAuthenticator(fromEmail, password);
            var session = Session.getInstance(properties, auth);
            var msg = createMimeMessage(session, fromEmail, fromText, toEmails, subjectText);
            msg.setContent(bodyContent);
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

    public static Multipart createMultipart(CharSequence bodyText) {
        return createMultipart_File(bodyText, null, null);
    }

    public static Multipart createMultipart_File(CharSequence bodyText, Path filePath, CharSequence optionalFileName) {
        return TGS_UnSafe.compile(() -> {
            var mp = new MimeMultipart();
            var mbp1 = new MimeBodyPart();
            mbp1.setText(bodyText.toString());
            mp.addBodyPart(mbp1);
            if (filePath == null) {
                return mp;
            }
            var fileName = optionalFileName == null ? TS_FileUtils.getNameFull(filePath) : optionalFileName;
            var mbp2 = new MimeBodyPart();
            var source = new FileDataSource(filePath.toString());
            mbp2.setDataHandler(new DataHandler(source));
            mbp2.setFileName(fileName.toString());
            mp.addBodyPart(mbp2);
            return mp;
        });
    }

    public static Multipart createMultipart_Image(CharSequence bodyText, Path filePath, CharSequence optionalFileName) {
        return TGS_UnSafe.compile(() -> {
            var mp = new MimeMultipart();
            var mbp1 = new MimeBodyPart();
            mbp1.setText(bodyText.toString());
            mp.addBodyPart(mbp1);
            if (filePath == null) {
                return mp;
            }
            var fileName = optionalFileName == null ? TS_FileUtils.getNameFull(filePath) : optionalFileName;
            var mbp2 = new MimeBodyPart();
            var source = new FileDataSource(filePath.toString());
            mbp2.setDataHandler(new DataHandler(source));
            mbp2.setFileName(fileName.toString());
            mbp2.setHeader("Content-ID", "image_id");//Trick is to add the content-id header here
            mp.addBodyPart(mbp2);
            var mbp3 = new MimeBodyPart();//third part for displaying image in the email body
            mbp3.setContent("<h1>Attached Image</h1><img src='cid:image_id'>", "text/html");
            mp.addBodyPart(mbp3);
            return mp;
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
