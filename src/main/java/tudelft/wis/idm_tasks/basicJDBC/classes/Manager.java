package tudelft.wis.idm_tasks.basicJDBC.classes;

import tudelft.wis.idm_tasks.basicJDBC.interfaces.*;
import java.io.*;
import java.sql.*;
import java.util.Scanner;


public class Manager implements JDBCManager {
    public static void main(String[] args) throws SQLException, FileNotFoundException, ClassNotFoundException {
        new Manager().getConnection();
    }

    public Connection getConnection() throws SQLException, ClassNotFoundException, FileNotFoundException {

        String jdbcUrl = "jdbc:postgresql://localhost:5432/idm_imdb";
        String username = "postgres";
        String password = new Scanner(new FileReader("src/main/java/tudelft/wis/idm_tasks/basicJDBC/classes/config.txt")).next();

        // Register the PostgreSQL driver

        Class.forName("org.postgresql.Driver");

        // Connect to the database

        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        return connection;
    }
}