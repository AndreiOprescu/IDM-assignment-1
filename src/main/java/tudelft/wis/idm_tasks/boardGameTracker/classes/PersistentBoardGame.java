package tudelft.wis.idm_tasks.boardGameTracker.classes;

import jakarta.persistence.*;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.BoardGame;

@Entity
@Table(name = "Boardgame")
public class PersistentBoardGame implements BoardGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name")
    private String name;

    @Column(name = "bgg_url")
    private String bgg_url = "https://boardgamesgeek.com/";

    public PersistentBoardGame(String name, String bgg_url) {
        this.name = name;
        this.bgg_url = bgg_url;
    }

    public PersistentBoardGame() {

    }

    /**
     * Returns the game name.
     *
     * @return game name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the game's BoardGamesGeek.com URL.
     *
     * @return the URL as a string
     */
    @Override
    public String getBGG_URL() {
        return bgg_url;
    }

    /**
     * Creates a human-readable String representation of this object.
     *
     * @return the string representation of the object
     */
    @Override
    public String toVerboseString() {
        return "PersistentBoardGame{" +
               "name='" + name + '\'' +
               ", BGG_URL='" + bgg_url + '\'' +
               '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
