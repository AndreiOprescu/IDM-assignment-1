import tudelft.wis.idm_tasks.basicJDBC.classes.Retriever;

import java.text.CollationElementIterator;
import java.util.Collection;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Retriever retriever = new Retriever();

        Collection<String> query1 = retriever.getTitlesPerYear(2019);
        System.out.println(query1);

        Collection<String> query2 = retriever.getJobCategoriesFromTitles("batman");
        System.out.println(query2);
    }
}
