package tudelft.wis.idm_tasks.basicJDBC.classes;

import tudelft.wis.idm_tasks.basicJDBC.interfaces.JDBCTask2Interface;

import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Retriever implements JDBCTask2Interface {
    @Override
    public Connection getConnection() {
        try {
            return new Manager().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<String> getTitlesPerYear(int year) {
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);
            PreparedStatement titlesQuery = conn.prepareStatement(
                    "SELECT t.primary_title " +
                    "FROM titles t " +
                    "WHERE t.start_year = ? " +
                    "LIMIT 20"
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
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);
            PreparedStatement titlesQuery = conn.prepareStatement(
                    "SELECT DISTINCT ci.job_category " +
                    "FROM titles t " +
                    "JOIN cast_info ci ON ci.title_id = t.title_id " +
                    "WHERE t.primary_title LIKE ? " +
                    "LIMIT 20"
            );
            searchString = "%" + searchString + "%";
            titlesQuery.setString(1, searchString);
            ResultSet result = titlesQuery.executeQuery();
            List<String> resultList = new ArrayList<>();
            while(result.next()) {
                resultList.add(result.getString("job_category"));
            }
            return resultList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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