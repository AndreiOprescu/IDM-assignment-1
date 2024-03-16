package tudelft.wis.idm_tasks.boardGameTracker.classes;

import org.hibernate.boot.model.process.internal.UserTypeMutabilityPlanAdapter;
import tudelft.wis.idm_tasks.basicJDBC.classes.Manager;
import tudelft.wis.idm_tasks.boardGameTracker.BgtException;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.BgtDataManager;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.BoardGame;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.PlaySession;
import tudelft.wis.idm_tasks.boardGameTracker.interfaces.Player;

import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class DataManager implements BgtDataManager {

    private Connection connection;
    public DataManager() {
        try {
            connection = new Manager().getConnection();
        } catch (SQLException | ClassNotFoundException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to fetch a Player by ID
    private Player fetchPlayerById(long playerId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM Player WHERE player_id = ?");
        statement.setLong(1, playerId);
        ResultSet resultSet = statement.executeQuery();
        PersistentPlayer player = null;
        if (resultSet.next()) {
            String name = resultSet.getString("name");
            String nickname = resultSet.getString("nickname");
            player = new PersistentPlayer(name, nickname);
            player.setId(playerId);
        }
        statement.close();
        return player;
    }

    // Method to fetch a BoardGame by ID
    private BoardGame fetchGameById(long gameId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM BoardGame WHERE game_id = ?");
        statement.setLong(1, gameId);
        ResultSet resultSet = statement.executeQuery();
        PersistentBoardGame game = null;
        if (resultSet.next()) {
            String name = resultSet.getString("name");
            String bggUrl = resultSet.getString("bgg_url");
            game = new PersistentBoardGame(name, bggUrl);
            game.setId(gameId);
        }
        statement.close();
        return game;
    }

    private PlaySession fetchPlaySessionById(long sessionId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM PlaySession WHERE session_id = ?");
        statement.setLong(1, sessionId);
        ResultSet resultSet = statement.executeQuery();
        PersistentPlaySession playSession = null;
        if (resultSet.next()) {
            Date date = resultSet.getDate("date");
            int hostId = resultSet.getInt("host_id");
            int gameId = resultSet.getInt("game_id");
            int playtime = resultSet.getInt("playtime");
            int winnerId = resultSet.getInt("winner_id");

            // Fetch host player
            Player host = fetchPlayerById(hostId);

            // Fetch game
            BoardGame game = fetchGameById(gameId);

            // Fetch all players
            Collection<Player> allPlayers = fetchAllPlayersForSession(sessionId);

            // Fetch winner player
            Player winner = fetchPlayerById(winnerId);

            // Create a new PlaySession object
            playSession = new PersistentPlaySession(new java.sql.Date(date.getTime()), host, game, allPlayers, winner, playtime);
            playSession.setId(sessionId);
        }
        statement.close();
        return playSession;
    }


    // Method to fetch all players for a given session ID
    private Collection<Player> fetchAllPlayersForSession(long sessionId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM playsession_players WHERE playsession_id = ?");
        statement.setLong(1, sessionId);
        ResultSet resultSet = statement.executeQuery();
        Collection<Player> players = new ArrayList<>();
        while (resultSet.next()) {
            int playerId = resultSet.getInt("player_id");
            Player player = fetchPlayerById(playerId);
            if (player != null) {
                players.add(player);
            }
        }
        statement.close();
        return players;
    }


    /**
     * Creates a new player and stores it in the DB.
     *
     * @param name     the player name
     * @param nickname the player nickname
     *
     * @return the new player
     *
     * @throws SQLException DB trouble
     */
    @Override
    public Player createNewPlayer(String name, String nickname) throws BgtException, SQLException {
        PersistentPlayer player = new PersistentPlayer(name, nickname);
        // Disable auto-commit to start the transaction
        connection.setAutoCommit(false);

        // Create a Statement object
        PreparedStatement statement = connection.prepareStatement("INSERT INTO player (name, nickname) VALUES (?, ?) RETURNING player_id");
        statement.setString(1, player.getPlayerName());
        statement.setString(2, player.getPlayerNickName());

        ResultSet results = statement.executeQuery();
        // Commit the transaction
        connection.commit();

        results.next();
        player.setId(results.getInt("player_id"));
        statement.close();
//        connection.close();

        return player;
    }

    /**
     * Searches for player in the database by a substring of their name.
     *
     * @param name the name substring to use, e.g., searching for "hris" will find "Christoph"
     *
     * @return collection of all players containing the param substring in their names
     *
     * @throws BgtException the bgt exception
     */
    @Override
    public Collection<Player> findPlayersByName(String name) throws BgtException, SQLException {
        connection.setAutoCommit(false);
        PreparedStatement query = connection.prepareStatement(
                "SELECT *\n" +
                "FROM player p\n" +
                "WHERE p.name LIKE ?");
        String searchString = "%" + name + "%";
        query.setString(1, searchString);
        ResultSet result = query.executeQuery();
        List<Player> resultList = new ArrayList<>();
        while(result.next()) {
            long playerId = result.getLong("player_id");;
            String playerName = result.getString("name");
            String playerNickname = result.getString("nickname");
            PersistentPlayer player = new PersistentPlayer(playerName, playerNickname);
            player.setId(playerId);
            resultList.add(player);
        }

        query.close();
//        connection.close();
        return resultList;
    }

    /**
     * Creates a new board game and stores it in the DB.
     * <p>
     * Note: These "create" methods are somewhat unnecessary. However, I put
     * them here to make the task test a bit easier. You can call an appropriate
     * persist method for this.
     *
     * @param name   the name of the game
     * @param bggURL the URL of the game at BoardGameGeek.com
     *
     * @return the new game
     *
     * @throws SQLException DB trouble
     */
    @Override
    public BoardGame createNewBoardgame(String name, String bggURL) throws BgtException, SQLException {
        PersistentBoardGame boardGame = new PersistentBoardGame(name, bggURL);

        // Disable auto-commit to start the transaction
        connection.setAutoCommit(false);

        // Create a PreparedStatement to insert the new BoardGame into the database
        PreparedStatement statement = connection.prepareStatement("INSERT INTO BoardGame (name, bgg_url) VALUES (?, ?) RETURNING game_id");
        statement.setString(1, boardGame.getName());
        statement.setString(2, boardGame.getBGG_URL());

        // Execute the query to insert the BoardGame into the database
        ResultSet results = statement.executeQuery();

        // Commit the transaction
        connection.commit();
        results.next();
        boardGame.setId(results.getInt("game_id"));

        // Close the PreparedStatement and database connection
        statement.close();
//        connection.close();

        return boardGame;
    }

    /**
     * Searches for game in the database by a substring of their name.
     *
     * @param name the name substring to use, e.g., searching for "clips" will
     *             find "Eclipse: Second Dawn of the Galaxy""
     *
     * @return collection of all boardgames containing the param substring in their names
     */
    @Override
    public Collection<BoardGame> findGamesByName(String name) throws BgtException, SQLException {
        connection.setAutoCommit(false);
        PreparedStatement query = connection.prepareStatement(
                "SELECT *\n" +
                "FROM boardgame b\n" +
                "WHERE b.name LIKE ?");
        String searchString = "%" + name + "%";
        query.setString(1, searchString);
        ResultSet result = query.executeQuery();
        List<BoardGame> resultList = new ArrayList<>();
        while(result.next()) {
            long boardgameId = result.getLong("game_id");
            String boardgameName = result.getString("name");
            String boardgameUrl = result.getString("bgg_url");
            PersistentBoardGame boardgame = new PersistentBoardGame(boardgameName, boardgameUrl);
            boardgame.setId(boardgameId);
            resultList.add(boardgame);
        }
        query.close();
//        connection.close();

        return resultList;


    }

    /**
     * Creates a new play session and stores it in the DB.
     *
     * @param date     the date of the session
     * @param host     the session host
     * @param game     the game which was played
     * @param playtime the approximate playtime in minutes
     * @param players  all players
     * @param winner   the one player who won (NULL in case of no winner; multiple
     *                 winners not supported)
     *
     * @return the new play session
     */
    @Override
    public PlaySession createNewPlaySession(
            Date date,
            Player host,
            BoardGame game,
            int playtime,
            Collection<Player> players,
            Player winner
    ) throws BgtException, SQLException {
        PersistentPlaySession playSession = new PersistentPlaySession(new java.sql.Date(date.getTime()), host, game, players, winner, playtime);
        connection.setAutoCommit(false);

        // Create a Statement object
        PreparedStatement statement = connection.prepareStatement("INSERT INTO playsession " +
                                                                  "(date, host_id, game_id, playtime, winner_id)" +
                                                                  " VALUES (?, ?, ?, ?, ?) RETURNING session_id");
        statement.setDate(1, new java.sql.Date(date.getTime()));
        statement.setLong(2, ((PersistentPlayer) host).getId());
        statement.setLong(3, ((PersistentBoardGame) game).getId());
        statement.setInt(4, playtime);
        statement.setLong(5, ((PersistentPlayer) winner).getId());

        ResultSet results = statement.executeQuery();
        connection.commit();

        results.next();
        playSession.setId(results.getInt("session_id"));
        for (Player player: players) {
            statement = connection.prepareStatement("INSERT INTO playsession_players (playsession_id, player_id) VALUES (?, ?)");
            statement.setLong(1, playSession.getId());
            statement.setLong(2, ((PersistentPlayer) player).getId());
        }
        // Commit the transaction
        connection.commit();

        statement.close();
//        connection.close();
        return playSession;
    }

    /**
     * Finds all play sessions from a specific date
     *
     * @param date the date to search from
     *
     * @return collection of all play sessions from the param date
     *
     * @throws BgtException the bgt exception
     */
    @Override
    public List<PlaySession> findSessionByDate(Date date) throws SQLException {
        // Disable auto-commit to start the transaction
        connection.setAutoCommit(false);

        // Create a PreparedStatement to select play sessions by date
        PreparedStatement query = connection.prepareStatement(
                "SELECT * FROM PlaySession WHERE date = ?");
        query.setDate(1, new java.sql.Date(date.getTime()));

        // Execute the query to select play sessions by date
        ResultSet result = query.executeQuery();

        // Initialize a list to store the result
        List<PlaySession> resultList = new ArrayList<>();

        // Iterate over the result set and add play sessions to the list
        while (result.next()) {
            long sessionId = result.getLong("session_id");
            Date sessionDate = result.getDate("date");
            int hostId = result.getInt("host_id");
            int gameId = result.getInt("game_id");
            int playtime = result.getInt("playtime");
            int winnerId = result.getInt("winner_id");

            // Fetch host player
            Player host = fetchPlayerById(hostId);

            // Fetch game
            BoardGame game = fetchGameById(gameId);

            // Fetch all players
            Collection<Player> allPlayers = fetchAllPlayersForSession(sessionId);

            // Fetch winner player
            Player winner = fetchPlayerById(winnerId);

            // Create a new PersistentPlaySession object
            PersistentPlaySession playSession = new PersistentPlaySession(new java.sql.Date(sessionDate.getTime()), host, game, allPlayers, winner, playtime);
            playSession.setId(sessionId);

            // Add the play session to the result list
            resultList.add(playSession);
        }

        // Close the PreparedStatement and database connection
        query.close();
//        connection.close();

        return resultList;
    }



    /**
     * Persists a given player to the DB. Note that this player might already exist and only needs an update :-)
     *
     * @param player the player
     */
    @Override
    public void persistPlayer(Player player) throws SQLException, BgtException {
        Player p = fetchPlayerById(((PersistentPlayer) player).getId());
        if(p == null) {
            createNewPlayer(player.getPlayerName(), player.getPlayerNickName());
        } else {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE player SET name = ?, nickname = ? WHERE player_id = ?");
            preparedStatement.setString(1, player.getPlayerName());
            preparedStatement.setString(2, player.getPlayerNickName());
            preparedStatement.setLong(3, ((PersistentPlayer) player).getId());
            int rs = preparedStatement.executeUpdate();
            preparedStatement.close();
        }
    }

    /**
     * Persists a given session to the DB. Note that this session might already exist and only needs an update :-)
     *
     * @param session the session
     */
    @Override
    public void persistPlaySession(PlaySession session) throws SQLException, BgtException {
        PlaySession playSession = fetchPlaySessionById(((PersistentPlaySession) session).getId());
        if(playSession == null) {
            createNewPlaySession(session.getDate(), session.getHost(), session.getGame(), session.getPlaytime(), session.getAllPlayers(),
                                 session.getWinner());
        } else {
            PreparedStatement statement = connection.prepareStatement("UPDATE PlaySession SET date = ?, host_id = ?, game_id = ?, playtime = ?, winner_id = ? WHERE session_id = ?");
            statement.setDate(1, (java.sql.Date) playSession.getDate());
            statement.setLong(2, ((PersistentPlayer) playSession.getHost()).getId());
            statement.setLong(3, ((PersistentBoardGame) playSession.getGame()).getId());
            statement.setInt(4, playSession.getPlaytime());
            statement.setLong(5, ((PersistentPlayer) playSession.getWinner()).getId());
            statement.setLong(6, ((PersistentPlaySession) playSession).getId());
            int rs = statement.executeUpdate();
            statement.close();
        }
    }

    /**
     * Persists a given game to the DB. Note that this game might already exist and only needs an update :-)
     *
     * @param game the game
     */
    @Override
    public void persistBoardGame(BoardGame game) throws SQLException, BgtException {
        BoardGame b = fetchGameById(((PersistentBoardGame) game).getId());
        if(b == null) {
            createNewBoardgame(game.getName(), game.getBGG_URL());
        } else {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE game SET name = ?, bgg_url = ? WHERE game_id = ?");
            preparedStatement.setString(1, game.getName());
            preparedStatement.setString(2, game.getBGG_URL());
            preparedStatement.setLong(3, ((PersistentBoardGame) game).getId());
            int rs = preparedStatement.executeUpdate();
            preparedStatement.close();
        }
    }
}
