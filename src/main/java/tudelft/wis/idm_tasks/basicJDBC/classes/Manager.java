package tudelft.wis.idm_tasks.basicJDBC.classes;

import tudelft.wis.idm_tasks.basicJDBC.interfaces.*;
import java.io.*;
import java.sql.*;
import java.util.Scanner;


public class Manager implements JDBCManager {
    public Connection getConnection() throws SQLException, ClassNotFoundException, FileNotFoundException {
        Scanner configs = new Scanner(new FileReader("src/main/java/tudelft/wis/idm_tasks/basicJDBC/classes/config.txt"));
        String jdbcUrl = "jdbc:postgresql://localhost:5432/" + configs.nextLine();
        String username = "postgres";
        String password = configs.nextLine();

        // Register the PostgreSQL driver

        Class.forName("org.postgresql.Driver");

        // Connect to the database

        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        return connection;
    }
}