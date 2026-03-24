package com.example.passmanager;

import javafx.application.Application;

import java.sql.*;

public class Launcher {
    public static void main(String[] args) throws SQLException {

        DBQueries query = new DBQueries();
        Connection conn = DriverManager.getConnection("jdbc:h2:/Users/schmay/test;AUTO_SERVER=TRUE", "sa", "");
        Statement stmt = conn.createStatement();
        org.h2.tools.Server.createWebServer().start();
        //System.out.println(query.resultSetToString(stmt.executeQuery(query.viewAccountInfo("GitHub"))));
        Application.launch(HelloApplication.class, args);
    }
}
// http://localhost:8082