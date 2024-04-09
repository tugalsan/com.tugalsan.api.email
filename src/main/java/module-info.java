module com.tugalsan.api.email {
    requires jakarta.activation;
    requires jakarta.mail;
    requires com.tugalsan.api.string;
    requires com.tugalsan.api.callable;
    requires com.tugalsan.api.file;
    requires com.tugalsan.api.log;
    requires com.tugalsan.api.union;
    exports com.tugalsan.api.email.client;
    exports com.tugalsan.api.email.server;
}
