package tudelft.wis.idm_tasks.boardGameTracker.classes;

import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.BoardGame;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.PlaySession;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.Player;

import java.sql.Date;
import java.util.Collection;

@Entity
public class PersistentPlaySession implements PlaySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToOne(targetEntity = PersistentPlayer.class)
    @JoinColumn(name = "host_id", referencedColumnName = "id")
    private Player host;

    @ManyToOne(targetEntity = PersistentBoardGame.class)
    @JoinColumn(name = "game_id", referencedColumnName = "id")
    private BoardGame game;

    @ManyToMany(targetEntity = PersistentPlayer.class)
    @JoinTable(
            name = "playsession_players",
            joinColumns = @JoinColumn(name = "playsession_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    @Cascade(CascadeType.ALL)
    private Collection<Player> allPlayers;

    @OneToOne(targetEntity = PersistentPlayer.class)
    @JoinColumn(name = "winner_id")
    private Player winner;

    private int playtime;

    public PersistentPlaySession(
            Date date,
            Player host,
            BoardGame game,
            Collection<Player> allPlayers,
            Player winner,
            int playtime
    ) {
        this.date = date;
        this.host = host;
        this.game = game;
        this.allPlayers = allPlayers;
        this.winner = winner;
        this.playtime = playtime;
    }

    public PersistentPlaySession() {

    }

    /**
     * Returns the date of the play session.
     *
     * @return date of the play session
     */
    @Override
    public Date getDate() {
        return date;
    }

    /**
     * Returns the player who hosted or organized this game session.
     *
     * @return player who hosted/organized this game session
     */
    @Override
    public Player getHost() {
        return host;
    }

    /**
     * Returns the game which was played.
     *
     * @return game which was played
     */
    @Override
    public BoardGame getGame() {
        return game;
    }

    /**
     * Returns all the players who joined the session.
     *
     * @return collection of players who joined the session
     */
    @Override
    public Collection<Player> getAllPlayers() {
        return allPlayers;
    }

    /**
     * Returns the winner of the game. This is somewhat naively assuming that
     * thee is only one player, but yeah, simplicity. Can be Null if nobody won.
     *
     * @return the player who is the winner, or Null if there is no winner
     */
    @Override
    public Player getWinner() {
        return winner;
    }

    /**
     * Returns the approximate playtime, in minutes, for the session.
     *
     * @return an integer representing the approximate playtime in minutes for this session
     */
    @Override
    public int getPlaytime() {
        return playtime;
    }

    @Override
    public String toVerboseString() {
        return "PersistentPlaySession{" +
               "id=" + id +
               ", date=" + date +
               ", host=" + host +
               ", game=" + game +
               ", allPlayers=" + allPlayers +
               ", winner=" + winner +
               ", playtime=" + playtime +
               '}';
    }

    /**
     * Creates a human-readable String representation of this object.
     *
     * @return the string representation of the object
     */


    public Long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
}
