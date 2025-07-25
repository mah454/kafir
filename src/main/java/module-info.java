module kafir {
    requires java.net.http;
    requires com.fasterxml.jackson.module.paramnames;
    requires com.fasterxml.jackson.databind;
    exports ir.moke.kafir.annotation;
    exports ir.moke.kafir.http;
}