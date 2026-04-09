package com.example.passmanager;

import javafx.application.Application;

import java.sql.*;

/**
 * The point where the application is loaded from
 */
public class Launcher {

    /**
     * Main method — sets up the H2 database server and launches the JavaFX application.
     * Calls Hello Application to instantiate JavaFX and first stage
     *
     * @param args command-line arguments passed through to JavaFX
     * @throws SQLException if the database connection or H2 web server cannot be started
     */
    public static void main(String[] args) throws SQLException
    {
        Connection conn = DriverManager.getConnection("jdbc:h2:/Users/schmay/test;AUTO_SERVER=TRUE", "sa", "");
        org.h2.tools.Server.createWebServer().start();
        //System.out.println(query.resultSetToString(stmt.executeQuery(query.viewAccountInfo("GitHub"))));
        Application.launch(HelloApplication.class, args);
    }
}
// http://localhost:8082