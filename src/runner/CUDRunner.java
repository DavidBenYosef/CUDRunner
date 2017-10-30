package runner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import blocks.GameData;
import blocks.RoundData;
import blocks.VoterData;
import connectors.Connector;
import connectors.Connector.ConnectorType;

public class CUDRunner {

	public SortedSet<Integer> suspected = new TreeSet<>();
	public Map<Integer, List<String>> suspectedMap = new HashMap<>();

	public Map<Integer, Integer> popularDistribution = new HashMap<>();
	public Map<Integer, Integer> voteDistribution = new HashMap<>();
	public Map<Integer, Integer> convDistribution = new HashMap<>();
	public Map<Integer, Integer> mad1Distribution = new HashMap<>();
	public Map<Integer, Integer> mad2Distribution = new HashMap<>();
	public Map<Integer, Integer> mad1_2Distribution = new HashMap<>();

	public Map<String, Map<Integer, List<Integer>>> madVoteMap = new TreeMap<>();
	public Map<String, Map<Integer, List<Integer>>> voterActionsMap = new TreeMap<>();

	public Map<String, List<Integer>> disconnectionVoterDist = new TreeMap<>();
	public Map<Integer, List<String>> disconnectionGameDist = new TreeMap<>();
	public List<Integer> gameList = new ArrayList<>();
	List<Integer> allRunGames = new ArrayList<>();
	
	private Connector connector;

	public void initMaps() {
		
			Util.initCountMap(popularDistribution, 1, 10);
			Util.initCountMap(voteDistribution, 1, 10);
			Util.initCountMap(convDistribution, 1, 10);
			Util.initCountMap(mad1Distribution, 1, 10);
			Util.initCountMap(mad2Distribution, 1, 10);
			Util.initCountMap(mad1_2Distribution, 1, 10);	
		convDistribution.put(0, 0);
	}

	public static void main(String args[]) throws SQLException {
		System.out.println("STARTED");
		String filePath = "C:/experimental results/first/results.accdb";
		
		Connector connector = new Connector(ConnectorType.SQL,"first");
		
		CUDRunner runner = new CUDRunner();
		runner.run(connector);
		
		System.out.println("FINISHED");
	}
	
	
	public void run(Connector connector){
		this.connector = connector;
			
		List<Integer> games = connector.getGames();
		//connector.createDataTables();

		CUDRunner runner = new CUDRunner();
		runner.initMaps();
		for (int game : games) {
			//if (game>1000){
			GameData gameData = connector.getGameDetails(game);
			//if (gameData.isFinished()){
			// if (gameData.isFinished() && gameData.getActualVoters() < 7&&
			// gameData.getActualVoters() > 0) {
			//if (gameData.isFinished()  && gameData.getWinner()==0) {

				System.out.println("Running game " + game);
				runner.allRunGames.add(gameData.getId());
				runner.runGame(gameData);
				System.out.println("Finished game " + game);
			//}//}
		}

		// runner.printSuspected();
		runner.printResults();


	}

	public void printResults() {
		 System.out.println("Games :" + allRunGames.size());
		 System.out.println("vote: " + voteDistribution);
		 System.out.println("popular: " + popularDistribution);
		 System.out.println("conv: " + convDistribution);
		 System.out.println("mad1: " + mad1Distribution);
		 System.out.println("mad2: " + mad2Distribution);
		 System.out.println("mad12: " + mad1_2Distribution);
		 System.out.println("disconnect: " + disconnectionVoterDist);

		for (Entry<String, Map<Integer, List<Integer>>> MadEntry : madVoteMap.entrySet()) {
			String voterId = MadEntry.getKey();
			Map<Integer, List<Integer>> voterMadActions = MadEntry.getValue();
			Map<Integer, List<Integer>> voterGames = voterActionsMap.get(voterId);
			// Collections.sort(voterGames);
			System.out.println(voterId + ":");
			
			System.out.println("Mad Actions: " + voterMadActions);
			System.out.println("All Voter Actions: " + voterGames);
			for(Entry<Integer, List<Integer>> voterGameEntry:voterGames.entrySet())
			{
				Integer gameId = voterGameEntry.getKey();
				int numOfMadActionsPerGame =0;
				if(voterMadActions.containsKey(gameId))
				{
					numOfMadActionsPerGame = voterMadActions.get(gameId).size();
				}
				
				System.out.println("Game: "+gameId+", percent of mad actions: "+(int)((double)numOfMadActionsPerGame/voterGameEntry.getValue().size()*100)+"%");
				
			}		
			
			System.out.println();
		}

		// System.out.println(disconnectionVoterDist.size());

		List<Integer> sortedKeys = new ArrayList<Integer>(disconnectionGameDist.keySet());
		Collections.sort(sortedKeys);
		for (Integer game : sortedKeys) {
			System.out.println(game + ":" + disconnectionGameDist.get(game));
		}

		System.out.println(disconnectionGameDist.size());
		// System.out.println("Avg:");
		// printAvg(voteDistribution);
		// System.out.println("Total: "+runner.allRunGames.size());
		// runner.allRunGames.removeAll(runner.gameList);
		//
		// Collections.sort(runner.allRunGames);
		// System.out.println(runner.allRunGames);
		// System.out.println(runner.allRunGames.size());

		// System.out.println(runner.madVoteMap);
	}

	public void printSuspected() {
		Map<String, Map<Integer, Integer>> totalMap = new HashMap<>();
		for (Entry<Integer, List<String>> entry : suspectedMap.entrySet()) {
			Integer game = entry.getKey();
			List<String> voters = entry.getValue();
			for (String voter : voters) {
				Integer gameScore = connector.getUserGameScore(voter, game);
				if (totalMap.containsKey(voter)) {
					totalMap.get(voter).put(game, gameScore);
				} else {
					Map<Integer, Integer> map = new HashMap<>();
					map.put(game, gameScore);
					totalMap.put(voter, map);
				}
			}
		}
		for (Entry<String, Map<Integer, Integer>> entry : totalMap.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
	}

	public void runGame(GameData gameData) {

		Map<String, VoterData> voters = gameData.getVoters();

		// checkSuspected(gameData, voters);

		Map<Integer, RoundData> roundsData = gameData.getRoundsData();

		Map<Integer, Integer> results = new HashMap<Integer, Integer>();
		Util.initCountMap(results, 1, gameData.getCandsNum());

		for (Entry<String, VoterData> entry : voters.entrySet()) {
			VoterData voter = entry.getValue();
			int selectedCand = voter.getSelectedCand();
			results.put(selectedCand, results.get(selectedCand) + 1);

		}

		gameData.setTruthfulResults(new HashMap<Integer, Integer>(results));

		List<Integer> rounds = new ArrayList<Integer>(roundsData.keySet());
		Collections.sort(rounds, Collections.reverseOrder());

		List<Integer> pwf;

		Set<String> prevVoters = null;
		for (int round : rounds) {

			 System.out.println("Round " + round + " started, current results:" + results);
			RoundData roundData = roundsData.get(round);
			roundData.setBaseResults(new HashMap<Integer, Integer>(results));
			Map<String, Integer> selectionMap = roundData.getsSelectionsMap();

			Set<String> currentVoters = selectionMap.keySet();
			checkDisconnections(gameData.getId(), prevVoters, round, currentVoters);
			prevVoters = currentVoters;

			pwf = Util.generatePWF(results, rounds.get(0), voters.size());

			Integer popularVote = getPopularVote(results);

			for (Entry<String, Integer> selection : selectionMap.entrySet()) {
				VoterData voter = voters.get(selection.getKey());
				Integer selectedCand = voter.getSelectedCand();
				Integer nextCand = selection.getValue();
				voter.setNextCand(nextCand);

				if (nextCand != null) {
					System.out.println("Voter " + voter.getUid() + " want to change from " + selectedCand + " to " + nextCand);
					voter.addVoteChange();
					addVoterAction(voterActionsMap, voter.getUid(), gameData.getId(), round);
					// roundData.addVoteChange();
					voteDistribution.put(round, voteDistribution.get(round) + 1);
					if (nextCand.equals(popularVote)) {
						popularDistribution.put(round, popularDistribution.get(round) + 1);

						// roundData.addVoteChangesForPopular();
					}
					if (pwf.contains(selectedCand)) {
						voter.addVoteChangeForPW();
					} else {
						voter.addVoteChangeForNPW();
					}

					//checkForMad(gameData, results, round, popularVote, voter, selectedCand, nextCand);
					
					boolean isMad1 = isMad1Action(results, voter.getOrderedCandsList(), selectedCand, nextCand);
					boolean isMad2 = isMad2Action(results, voter.getOrderedCandsList(), selectedCand, nextCand);
					
					if (isMad1)
					{
						gameData.addMad1All();
						mad1Distribution.put(round, mad1Distribution.get(round) + 1);
					}
					
					if (isMad2)
					{
						gameData.addMad2All();
						mad1_2Distribution.put(round, mad1_2Distribution.get(round) + 1);
					}
					 if (isMad1&& isMad2)
					 {
						 gameData.addMad12All();
						mad1_2Distribution.put(round, mad1_2Distribution.get(round) + 1); 
					 }
					 
					 if(isMad1||isMad2)
					 {
						 voter.addIrr();
					 }
				}
			}

			String changer = gameData.getSelectedChanger(round);
			if (changer != null) {
				
				VoterData changerVoter = voters.get(changer);
				int oldCand = changerVoter.getSelectedCand();
				int newCand = changerVoter.getNextCand();
				
				roundData.setChangerId(changer);
				roundData.setChangedFrom(oldCand);
				roundData.setChangedTo(newCand);

				results.put(oldCand, results.get(oldCand) - 1);
				changerVoter.setSelectedCand(newCand);
				results.put(newCand, results.get(newCand) + 1);
				gameData.addVoteChange();
				
				
				boolean isMad1 = isMad1Action(results, changerVoter.getOrderedCandsList(), oldCand, newCand);
				boolean isMad2 = isMad2Action(results, changerVoter.getOrderedCandsList(), oldCand, newCand);
				
				if (isMad1)
				{
					gameData.addMad1Selected();
					
				}
				
				if (isMad2)
				{
					gameData.addMad2Selected();
					
				}
				 if (isMad1&& isMad2)
				 {
					 gameData.addMad12Selected();
					
				 }
				

				 System.out.println("Voter " + changer + " chosen to changed from " + oldCand + " to " + newCand);
			} else {
				 System.out.println("No one wanted to change his selection");
			}
			 System.out.println("Round " + round + " finished , current results:" + results);
			int winner = Util.getWinner(results);
			if (winner > 0) {
				convDistribution.put(round, convDistribution.get(round) + 1);
				// gameList.add(gameData.getId());
				gameData.setPoa(Util.generatePoa(gameData.getTruthfulResults(), winner));
				gameData.setConvTime(gameData.getRounds() - round + 1);
				gameData.setWinner(winner);
				gameData.setActualVoters(roundsData.get(round).getVotersNum());
				for (Map.Entry<String, VoterData> entry : voters.entrySet()) {

					VoterData voter = entry.getValue();
					voter.setScore(Util.calculateScore(voter.getOrderedCands().length,
							voter.getOrderedCandsList().indexOf(voter.getSelectedCand())));
				}
				 System.out.println("Agreement on cand " + winner);
			} else if (round == 1) {
				convDistribution.put(0, convDistribution.get(0) + 1);
				// gameList.add(gameData.getId());
				gameData.setActualVoters(roundsData.get(round).getVotersNum());
				for (Map.Entry<String, VoterData> entry : voters.entrySet()) {

					VoterData voter = entry.getValue();
					voter.setScore(0);
				}
				 System.out.println("No agreement by the deadline");
			}
//			try {
//				connector.saveRoundData(roundData);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
		}

	try {
			connector.saveGameData(gameData);
			for (Map.Entry<String, VoterData> entry : voters.entrySet()) {
				VoterData voter = entry.getValue();
				connector.saveVoterData(voter);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void checkForMad(GameData gameData, Map<Integer, Integer> results, int round, Integer popularVote,
			VoterData voter, Integer selectedCand, Integer nextCand) {
		List<Integer> voterOrderedCand = voter.getOrderedCandsList();
		int currentRank = voterOrderedCand.indexOf(selectedCand);
		int nextRank = voterOrderedCand.indexOf(nextCand);

		int currentScore = results.get(selectedCand);
		int nextScore = results.get(nextCand);

		boolean mad1 = false;
		if (popularVote != null) {
			int pupularRank = voterOrderedCand.indexOf(popularVote);
			// lihi - isNextCandLowerThanPopular

			if (pupularRank < nextRank) {
				gameData.addMad1All();
				mad1Distribution.put(round, mad1Distribution.get(round) + 1);
				mad1 = true;

			}
		}

		// david
		boolean isNextCandLowerRank = currentRank < nextRank;
		// david
		boolean isNextCandLowerScore = currentScore > nextScore;

		if (isNextCandLowerRank && isNextCandLowerScore) {
			gameData.addMad2All();
			mad2Distribution.put(round, mad2Distribution.get(round) + 1);
			if (mad1) {
				// both
				// System.out.println("Both: voterOrderedCand:" +
				// voterOrderedCand + " ,results:" + results
				// + " , selectedCand:" + selectedCand + "
				// ,nextCand" + nextCand);
				gameData.addMad12All();
				mad1_2Distribution.put(round, mad1_2Distribution.get(round) + 1);

			} else {
				addVoterAction(madVoteMap, voter.getUid(), gameData.getId(), round);
				// only mad2

			}
		} else if (mad1) {
			// only mad1
			// System.out.println("only1: voterOrderedCand:" +
			// voterOrderedCand + " ,results:" + results
			// + " , selectedCand:" + selectedCand + " ,nextCand" +
			// nextCand);

		}

		// System.out.println("Voter " + selection.getKey() + " with
		// prefset " + voterOrderedCand
		// + " want to change from " + selectedCand + " to " +
		// nextCand);
	}
	
	
	
	private boolean isMad1Action(Map<Integer, Integer> results, List<Integer> voterOrderedCand, Integer selectedCand,
			Integer nextCand) {
		Integer popularVote = getPopularVote(results);
		int nextRank = voterOrderedCand.indexOf(nextCand);
		if (popularVote != null) {
			int pupularRank = voterOrderedCand.indexOf(popularVote);
			// lihi - isNextCandLowerThanPopular

			if (pupularRank < nextRank) {
				// gameData.addMad1All();
				// mad1Distribution.put(round, mad1Distribution.get(round) + 1);
				// mad1 = true;
				return true;
			}
		}
		return false;
	}

	private boolean isMad2Action(Map<Integer, Integer> results, List<Integer> voterOrderedCand, Integer selectedCand,
			Integer nextCand) {

		int currentScore = results.get(selectedCand);
		int nextScore = results.get(nextCand);
		int currentRank = voterOrderedCand.indexOf(selectedCand);
		int nextRank = voterOrderedCand.indexOf(nextCand);

		// david
		boolean isNextCandLowerRank = currentRank < nextRank;
		// david
		boolean isNextCandLowerScore = currentScore > nextScore;

		if (isNextCandLowerRank && isNextCandLowerScore) {
			return true;
		}
		return false;
	}
	
	private void checkDisconnections(Integer gameId, Set<String> prevVoters, int round, Set<String> currentVoters) {
		if (prevVoters != null) {
			prevVoters.removeAll(currentVoters);
			if (!prevVoters.isEmpty()) {
				if (!gameList.contains(gameId)) {
					gameList.add(gameId);
				}
				for (String disconnected : prevVoters) {
					if (disconnectionVoterDist.containsKey(disconnected)) {
						disconnectionVoterDist.get(disconnected).add(gameId);
					} else {
						List<Integer> gamesList = new ArrayList<>();
						gamesList.add(gameId);
						disconnectionVoterDist.put(disconnected, gamesList);
					}

					if (disconnectionGameDist.containsKey(gameId)) {
						disconnectionGameDist.get(gameId).add(disconnected);
					} else {
						List<String> votersList = new ArrayList<>();
						votersList.add(disconnected);
						disconnectionGameDist.put(gameId, votersList);
					}
				}

				System.out.println("Game: " + gameId + " , round: " + round + " , disconnected users: " + prevVoters);
			}
		} else {
			if (currentVoters.size() < 7) {
				System.out.println("Game started with disconnection: " + gameId);
				// gameList.add(gameData.getId());
			}
		}
	}

	private void addVoterAction(Map<String, Map<Integer, List<Integer>>> map, String voterId, Integer gameId,
			Integer round) {
		Map<Integer, List<Integer>> gameMap;
		if (map.containsKey(voterId)) {
			gameMap = map.get(voterId);
		} else {
			gameMap = new TreeMap<>();
			gameMap.put(gameId, new ArrayList<>());

		}

		if (gameMap.containsKey(gameId)) {
			gameMap.get(gameId).add(round);
		} else {
			List<Integer> rounds = new ArrayList<>();
			rounds.add(round);
			gameMap.put(gameId, rounds);
		}
		map.put(voterId, gameMap);
	}

	private Integer getPopularVote(Map<Integer, Integer> results) {
		int popularVote = 0;

		int maxValue = Collections.max(results.values());

		for (Map.Entry<Integer, Integer> result : results.entrySet()) {
			if (result.getValue().equals(maxValue)) {
				// 2 values are same
				if (popularVote > 0) {
					return null;
				} else {
					popularVote = result.getKey();
				}
			}
		}
		return popularVote;
	}

	public void checkSuspected(GameData gameData, Map<String, VoterData> voters) {
		int above = 0;
		int below = 0;
		List<String> susList = new ArrayList<>();
		// Map<Integer,String> susMap = new HashMap<>();
		for (Entry<String, VoterData> entry : voters.entrySet()) {
			VoterData voter = entry.getValue();
			int score = connector.getUserScore(voter.getUid(), gameData.getId());
			if (score >= 1000) {

				above++;
			} else {
				susList.add(voter.getUid());
				below++;
			}
		}
		if (below < 3) {
			suspectedMap.put(gameData.getId(), susList);

			System.out
					.println("SUSPECTED game: " + gameData.getId() + " below 1000: " + below + " above 1000: " + above);
			System.out.println(susList);
		}
	}

}