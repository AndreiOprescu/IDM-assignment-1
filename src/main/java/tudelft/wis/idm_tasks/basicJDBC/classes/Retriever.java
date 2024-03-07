package tudelft.wis.idm_tasks.basicJDBC.classes;

import tudelft.wis.idm_tasks.basicJDBC.interfaces.JDBCTask2Interface;

import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class Retriever implements JDBCTask2Interface {
    @Override
    public Connection getConnection() {
        return new Retriever().getConnection();
    }

    public Collection<String> getTitlesPerYear(int year) {
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);
            PreparedStatement titlesQuery = conn.prepareStatement(
                    "SELECT t.primary_title FROM titles t WHERE t.start_year = ?"
            );
            titlesQuery.setInt(1, year);
            ResultSet result = titlesQuery.executeQuery();
            List<String> resultList = new ArrayList<>();
            while(result.next()) {
                resultList.add(result.getString("primary_title"));
            }
            return resultList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<String> getJobCategoriesFromTitles(String searchString) {
        return null;
    }

    @Override
    public double getAverageRuntimeOfGenre(String genre) {
        return 0;
    }

    @Override
    public Collection<String> getPlayedCharacters(String actorFullname) {
        return null;
    }
}