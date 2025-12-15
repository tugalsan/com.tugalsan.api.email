package com.tugalsan.api.email.server;

import module com.tugalsan.api.union;
import module com.tugalsan.api.string;
import module com.tugalsan.api.function;
import module jakarta.mail;
import java.util.*;

public class TS_EMailSendUtils {

    private TS_EMailSendUtils() {

    }

    public static TGS_UnionExcuseVoid send_lst(//func name ambugious with send
            Properties properties,
            CharSequence fromEmail, CharSequence fromText, CharSequence password,
            CharSequence toEmails, CharSequence subjectText,
            CharSequence optionalFontCss, CharSequence bodyHtml, List<MimeBodyPart> files) {
        return send(properties, fromEmail, fromText, password, toEmails, subjectText, optionalFontCss, bodyHtml, files.toArray(MimeBodyPart[]::new));
    }

    public static TGS_UnionExcuseVoid send(
            Properties properties,
            CharSequence fromEmail, CharSequence fromText, CharSequence password,
            CharSequence toEmails, CharSequence subjectText,
            CharSequence optionalFontCss, CharSequence bodyHtml, MimeBodyPart... files) {

        return TGS_FuncMTCUtils.call(() -> {
            var auth = createAuthenticator(fromEmail, password);
            var session = Session.getInstance(properties, auth);
            var msg = TS_EMailCreateUtils.createMimeMessage(session, fromEmail, fromText, toEmails, subjectText);
            var mp = new MimeMultipart();
            var mbp = new MimeBodyPart();
            mbp.setContent("<p " + TGS_StringUtils.cmn().toEmptyIfNull(optionalFontCss) + ">" + bodyHtml + "</p>", "text/html; charset=utf-8");
            mp.addBodyPart(mbp);
            Arrays.stream(files).forEachOrdered(file -> TGS_FuncMTCUtils.run(() -> mp.addBodyPart(file)));
            msg.setContent(mp);
            Transport.send(msg);
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> {
            return TGS_UnionExcuseVoid.ofExcuse(e);
        });
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
