package tudelft.wis.idm_tasks.boardGameTracker.classes;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.BoardGame;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.Player;
import org.hibernate.annotations.*;

import java.util.Collection;
import java.util.HashSet;

@Entity
@Table(name = "Player")
public class PersistentPlayer implements Player {
    /**
     * Returns the name of the player.
     *
     * @return name of the player
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name",
            nullable = false)
    private String name;
    @Column(name = "nickname")
    private String nickname;
    @ManyToMany(targetEntity = PersistentBoardGame.class)
    @JoinTable(
            name = "player_owns_game",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "boardgame_id")
    )
    @Cascade(CascadeType.ALL)
    private Collection<BoardGame> boardgames = new HashSet<>();

    public PersistentPlayer(String name, String nickname) {
        this.name = name;
        this.nickname = nickname;
    }

    public PersistentPlayer() {

    }

    @Override
    public String getPlayerName() {
        return this.name;
    }

    /**
     * Returns the nickname of the player.
     *
     * @return nickname of the player
     */
    @Override
    public String getPlayerNickName() {
        return this.nickname;
    }

    /**
     * Returns all the boardgames this player owns (if any).
     *
     * @return collection of boardgames this player owns
     */
    @Override
    public Collection<BoardGame> getGameCollection() {
        return boardgames;
    }

    /**
     * Creates a human-readable String representation of this object.
     *
     * @return the string representation of the object
     */
    @Override
    public String toVerboseString() {
        return String.format("<Player {name: %s, nickname: %s, boardgames: %s}>",
                             this.name,
                             this.nickname,
                             this.boardgames.toString());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void addGame(BoardGame game) {
        boardgames.add(game);
    }
}
