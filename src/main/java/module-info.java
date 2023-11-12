module com.tugalsan.api.email {
    requires jakarta.activation;
    requires jakarta.mail;
    requires com.tugalsan.api.string;
    requires com.tugalsan.api.tuple;
    requires com.tugalsan.api.unsafe;
    requires com.tugalsan.api.callable;
    requires com.tugalsan.api.file;
    requires com.tugalsan.api.log;
    exports com.tugalsan.api.email.client;
    exports com.tugalsan.api.email.server;
}
