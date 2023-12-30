module com.ake.ckey {
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.controls;
    requires lombok;
    requires org.jsoup;
    requires cn.hutool;
    requires java.sql;

    opens com.ake.ckey;
    opens com.ake.ckey.model;
}