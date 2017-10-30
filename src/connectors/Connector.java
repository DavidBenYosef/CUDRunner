package connectors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import blocks.GameData;
import blocks.RoundData;
import blocks.VoterData;

public class Connector  {
	
	public enum ConnectorType{SQL,ACCESS}		
	
	ConnectorType type;
	
	String driver = null;
	String url = null;
	
	public Connector(ConnectorType type,String resource){
		this.type = type;
		switch (type) {
		case ACCESS:
			 driver =  "net.ucanaccess.jdbc.UcanaccessDriver";
			 url = "jdbc:ucanaccess://"+resource;
			break;
		case SQL:
			driver = "com.mysql.jdbc.Driver";
			url = "jdbc:mysql://localhost:3306/"+resource+"?" +
                                   "user=cudgame&password=Daby5302!";
			break;
		default:
			break;
		}
	}
	
	
	public Connection getConnection() {
		Connection conn = null;		

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	
	public List<Integer> getGames() {
		List<Integer> games = new ArrayList<>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();

			stmt = conn.createStatement();
			String excelQuery = "select id from games ";
			rs = stmt.executeQuery(excelQuery);
			while (rs.next()) {
				games.add(rs.getInt("id"));
			
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				conn.close();

			} catch (SQLException e) {
			}
		}
		return games;
	}
	
	
	/* (non-Javadoc)
	 * @see connectors.IConnector#getGameDetails(int)
	 */
	
	public GameData getGameDetails(int id) {
		GameData gameData = new GameData();
		gameData.setId(id);
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();

			stmt = conn.createStatement();
			String excelQuery = "select * from games where id =" + id;
			rs = stmt.executeQuery(excelQuery);
			if (rs.next()) {
				gameData.setVotersNum(rs.getInt("voters"));
				gameData.setRounds(rs.getInt("rounds"));
				gameData.setPrefSet(rs.getString("prefset"));
				gameData.setCandsNum(rs.getInt("cands_num"));
				gameData.setWinner(rs.getInt("winner"));
				gameData.setActualVoters(rs.getInt("actual_voters"));
				gameData.setFinished("FINISHED".equals(rs.getString("status")));
				gameData.setIntro("1".equals(rs.getString("is_intro")));
			}

			rs.close();
			stmt.close();

			stmt = conn.createStatement();
			excelQuery = "select * from voter_actions where game_id =" + id;
			rs = stmt.executeQuery(excelQuery);

			while (rs.next()) {
				Integer nextCand = rs.getInt("next_selected_cand");
				if (rs.wasNull()) {
					nextCand = null;
				}
				gameData.addSelectionRow(id,rs.getString("voter_id"), rs.getInt("round"), rs.getString("prefset"),
						nextCand, rs.getInt("changed_flag"));
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				conn.close();

			} catch (SQLException e) {
			}
		}
		return gameData;

	}
	

	/* (non-Javadoc)
	 * @see connectors.IConnector#createDataTables()
	 */
	
	public  void createDataTables() throws SQLException {
		String sql = "DROP TABLE games_data;";
		try {
			executeUpdateSQL(sql, null);
		} catch (SQLException e) {

		}
		
		sql = "DROP TABLE voters_data;";
		try {
			executeUpdateSQL(sql, null);
		} catch (SQLException e) {

		}
		
		sql = "DROP TABLE rounds_data;";
		try {
			executeUpdateSQL(sql, null);
		} catch (SQLException e) {

		}

//		CREATE TABLE `games` (
//				  `id` int(11) NOT NULL AUTO_INCREMENT,
//				  `voters` int(11) DEFAULT NULL,
//				  `rounds` int(11) DEFAULT NULL,
//				  `prefset` varchar(45) DEFAULT NULL,
//				  `run_date` datetime DEFAULT NULL,
//				  `status` varchar(45) DEFAULT NULL,
//				  `timePerRound` int(11) DEFAULT NULL,
//				  `actual_voters` int(11) DEFAULT NULL,
//				  `winner` int(11) DEFAULT NULL,
//				  `conv_time` int(11) DEFAULT NULL,
//				  `vote_changes` int(11) DEFAULT NULL,
//				  `poa` int(11) DEFAULT NULL,
//				  PRIMARY KEY (`id`)
//				) ENGINE=InnoDB AUTO_INCREMENT=342 DEFAULT CHARSET=utf8;
//		
		
		StringBuilder builder = new StringBuilder();
		builder.append("CREATE TABLE games_data(");
		builder.append("ID INTEGER NOT NULL, ");
		builder.append("VOTERS TEXT  DEFAULT NULL, ");
		builder.append("rounds INTEGER   DEFAULT NULL, ");
		builder.append("prefset TEXT  DEFAULT NULL, ");
		builder.append("time_per_round INTEGER   DEFAULT NULL, ");
		builder.append("conv_time INTEGER  DEFAULT NULL , ");
		builder.append("actual_voters INTEGER  DEFAULT NULL , ");		
		builder.append("winner INTEGER  DEFAULT NULL , ");
		builder.append("vote_changes INTEGER   DEFAULT NULL, ");
		builder.append("poa INTEGER  DEFAULT NULL ,");
		builder.append("PRIMARY KEY (id) ) ");
		executeUpdateSQL(builder.toString(), null);

		
		builder = new StringBuilder();
		builder.append("CREATE TABLE voters_data(");
		builder.append("uid TEXT NOT NULL, ");
		builder.append("score INTEGER, ");
		builder.append("game_id INTEGER , ");		
		builder.append("vote_change_requests INTEGER , ");
		builder.append("vote_change_for_pw INTEGER , ");
		builder.append("vote_change_for_npw INTEGER )");


		executeUpdateSQL(builder.toString(), null);
		
		builder = new StringBuilder();
		builder.append("CREATE TABLE rounds_data(");
		builder.append("game_id NUMBER(11) , ");
		builder.append("round NUMBER(11), ");
		builder.append("base_results VARCHAR(50) , ");		
		builder.append("changer_id VARCHAR(20) , ");
		builder.append("changed_from NUMBER(11) , ");
		builder.append("changed_to NUMBER(11) )");


		executeUpdateSQL(builder.toString(), null);
	}

	/* (non-Javadoc)
	 * @see connectors.IConnector#saveGameData(blocks.GameData)
	 */
	
	public  void saveGameData(GameData game) throws SQLException {

		//String sql = "INSERT INTO games_data (id,voters,rounds,prefset,time_per_round,conv_time,actual_voters,winner,vote_changes,poa) VALUES (?,?,?,?,?,?,?,?,?,?)";

		//Object[] objects = new Object[] { game.getId(), game.getVotersNum(),game.getRounds(),game.getPrefSet(),game.getTimePerRound(), game.getConvTime(), game.getActualVoters(),
		//		game.getWinner(), game.getVoteChanges(),game.getPoa() };
		String sql = "UPDATE games set mad1_all = ? , mad2_all= ? ,mad12_all= ? ,mad1_selected= ? , mad2_selected= ? ,mad12_selected= ? where id = ? ";
		
		//"INSERT INTO voters_data (uid,score,game_id,vote_change_requests,vote_change_for_pw,vote_change_for_npw) VALUES (? ,? ,?,?,?,?)";
Object[] objects = new Object[] { game.getMad1All(),game.getMad2All(),game.getMad12All(),game.getMad1Selected(),game.getMad2Selected(),game.getMad12Selected(), game.getId()};
		//new Object[] {voter.getUid(), voter.getScore(), voter.getGameId(),
		//voter.getVoteChanges(), voter.getVoteChangeForPW(), voter.getVoteChangeForNPW() };
		
		
		executeUpdateSQL(sql, objects);

	}
	
	/* (non-Javadoc)
	 * @see connectors.IConnector#saveRoundData(blocks.RoundData)
	 */
	
	public  void saveRoundData(RoundData round) throws SQLException {

		String sql = "INSERT INTO rounds_data (game_id,round,base_results,changer_id,changed_from,changed_to) VALUES (?,?,?,?,?,?)";

		Object[] objects = new Object[] { round.getGame(), round.getRound(),round.getBaseResults().toString(),round.getChangerId(),round.getChangedFrom(), round.getChangedTo() };

		executeUpdateSQL(sql, objects);

	}

	/* (non-Javadoc)
	 * @see connectors.IConnector#saveVoterData(blocks.VoterData)
	 */
	
	public  void saveVoterData(VoterData voter) throws SQLException {

		String sql = "UPDATE voters set irr = ? where uid = ? and game_id = ?";
				
				//"INSERT INTO voters_data (uid,score,game_id,vote_change_requests,vote_change_for_pw,vote_change_for_npw) VALUES (? ,? ,?,?,?,?)";
		Object[] objects = new Object[] { voter.getIrr(),voter.getUid(), voter.getGameId()};
				//new Object[] {voter.getUid(), voter.getScore(), voter.getGameId(),
				//voter.getVoteChanges(), voter.getVoteChangeForPW(), voter.getVoteChangeForNPW() };
		executeUpdateSQL(sql, objects);
	}

	private  void setObjects(PreparedStatement stmt, Object[] objects) throws SQLException {
		int i = 1;
		if (objects != null) {
			for (Object object : objects) {
				stmt.setObject(i++, object);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see connectors.IConnector#getUserScore(java.lang.String, int)
	 */
	
	public  int getUserScore(String uid,int gameId)
	{
		int score=0;
		String sql = "SELECT count(*) as games,sum(score) as totalavg from voters where uid = '"+uid+"' and game_id < "+gameId;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();

			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				score = rs.getInt("totalavg");				
			}

			rs.close();
			stmt.close();
		
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				conn.close();

			} catch (SQLException e) {
			}
		}
		return score;
		
	}
	
	/* (non-Javadoc)
	 * @see connectors.IConnector#getUserGameScore(java.lang.String, int)
	 */
	
	public  int getUserGameScore(String uid,int gameId)
	{
		int score=0;
		String sql = "SELECT score from voters where uid = '"+uid+"' and game_id = "+gameId;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();

			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				score = rs.getInt("score");				
			}

			rs.close();
			stmt.close();
		
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				conn.close();

			} catch (SQLException e) {
			}
		}
		return score;
		
	}
	

	/* (non-Javadoc)
	 * @see connectors.IConnector#executeUpdateSQL(java.lang.String, java.lang.Object[])
	 */
	
	public  void executeUpdateSQL(String sql, Object[] objects) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			setObjects(stmt, objects);
			stmt.executeUpdate();
		}

		finally {
			if (conn != null)
				try {
					conn.close();
				} catch (Exception ignore) {
				}
		}
	}
}
