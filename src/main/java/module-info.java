module org.example.practica5 {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires java.desktop;

    opens org.example.practica5 ;
    opens org.example.practica5.Model ;
    opens org.example.practica5.Router ;
    opens org.example.practica5.Repository ;
    opens org.example.practica5.DB ;


    exports org.example.practica5;
    exports org.example.practica5.Model;
    exports org.example.practica5.Filter;
    opens org.example.practica5.Filter;
    exports org.example.practica5.Controller;
    opens org.example.practica5.Controller;
    exports org.example.practica5.Handler;
    opens org.example.practica5.Handler;
}