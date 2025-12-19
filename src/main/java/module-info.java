module com.tugalsan.api.email {
    requires jakarta.activation;
    requires jakarta.mail;
    //requires outlook-message-parser;
    requires com.tugalsan.api.string;
    requires com.tugalsan.api.union;    
    requires com.tugalsan.api.function;
    requires com.tugalsan.api.file;
    requires com.tugalsan.api.log;
    exports com.tugalsan.api.email.client;
    exports com.tugalsan.api.email.server;
}
