package com.example.passmanager;

import javafx.application.Application;
import org.h2.tools.Server;

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
        Server.createWebServer("-web", "-webPort", "8082").start();

        Connection conn = DriverManager.getConnection(DBQueries.URL, "sa", "");
        Statement stmt = conn.createStatement();
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS Accounts (" +
            "ID INT AUTO_INCREMENT PRIMARY KEY, " +
            "Platform VARCHAR(255), " +
            "Username VARCHAR(255), " +
            "Password VARCHAR(512), " +
            "RANDOM_SALT VARCHAR(255))"
        );
        conn.close();
        Application.launch(HelloApplication.class, args);
    }
}
// http://localhost:8082
// jdbc:h2:~/PassManager/data
//DELETE FROM Accounts;
//ALTER TABLE Accounts ALTER COLUMN id RESTART WITH 1
//To update any code make sure we have nothing in our database, and we want to
// be in the directory /Users/schmay/IdeaProjects/PassManager and do ./build.sh