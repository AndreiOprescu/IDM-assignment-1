import tudelft.wis.idm_tasks.basicJDBC.classes.Retriever;

import java.text.CollationElementIterator;
import java.util.Collection;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Retriever retriever = new Retriever();

        Collection<String> query1 = retriever.getTitlesPerYear(2019);
        System.out.println("Query 1:");
        System.out.println(query1.stream()
                .limit(20)
                .toList());

        Collection<String> query2 = retriever.getJobCategoriesFromTitles("batman");
        System.out.println("Query 2:");
        System.out.println(query2.stream()
                .limit(20)
                .toList());
        
        double query3 = retriever.getAverageRuntimeOfGenre("Comedy");
        System.out.println("Query 3:");
        System.out.println(query3);
        
        Collection<String> query4 = retriever.getPlayedCharacters("Robert Downey Jr.");
        System.out.println("Query 4:");
        System.out.println(query4.stream()
                .limit(20)
                .toList());
    }
}
