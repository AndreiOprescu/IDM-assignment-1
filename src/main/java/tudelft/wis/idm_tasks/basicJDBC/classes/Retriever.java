class Retriever implements JDBCTask2Interface {
    public Collection<String> getTitlesPerYear(int year) {
        Connection conn = JDBCManager.getConnection();
        conn.setAutoCommit(false);
        PreparedStatement changeNameStmt = conn.prepareStatement(
                "UPDATE hero SET name=? WHERE name=?"
        );
        changeNameStmt.setString(1, "Jean Grey-Summers");
        changeNameStmt.setString(2, "Jean Grey");
        changeNameStmt.executeUpdate();

        changeNameStmt.setString(1, "Scott Grey-Summers");
        changeNameStmt.setString (2, "Scott Summers");
        changeNameStmt.executeUpdate();

        conn.commit();
    }
}