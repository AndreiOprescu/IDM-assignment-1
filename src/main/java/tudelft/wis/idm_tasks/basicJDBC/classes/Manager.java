
public static class JDBCManager implements JDBCManager {

    public static Connection getConnection() throws SQLException, ClassNotFoundException {

        String jdbcUrl = "jdbc:postgresql://localhost:5432/idm_imdb";
        String username = "postgres";
        String password;

        Object o = new JSONParser().parse(new FileReader(File.json));
        JSONObject j = (JSONObject) o;
        password = (String) j.get("password");

        // Register the PostgreSQL driver

        Class.forName("org.postgresql.Driver");

        // Connect to the database

        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        return connection;
    }
}