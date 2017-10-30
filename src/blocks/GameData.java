package blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class GameData {
	Map<Integer,String> selectedChanger = new HashMap<Integer, String>();
	Map<String, VoterData> voters = new HashMap<String, VoterData>();
	Map<Integer, RoundData> roundsData = new HashMap<Integer, RoundData>();

	private Integer id;
	private int votersNum;
	private Integer candsNum;
	private int rounds;
	private int timePerRound;
	private String prefSet;
	private Integer convTime;
	private int actualVoters;
	private int winner;
	private AtomicInteger voteChanges=new AtomicInteger();
	private Integer poa;
	private Map<Integer, Integer> truthfulResults;
	private boolean finished;
	private boolean isIntro;
	
	private int mad1All;
	private int mad2All;
	private int mad12All;
	private int mad1Selected;
	private int mad2Selected;
	private int mad12Selected;

	public void addSelectionRow(int gameId, String voterId,int round,String prefSet,Integer selection,Integer changedFlag) {

		if (!voters.containsKey(voterId)) {
			VoterData voter = new VoterData();
			int[] orderedCand = fromString(prefSet);
			if(candsNum==null)
			{
				candsNum=orderedCand.length;
			}
			voter.setUid(voterId);
			voter.setGameId(gameId);
			voter.setOrderedCands(orderedCand);
			voter.setSelectedCand(orderedCand[0]);		
			voters.put(voterId, voter);
			
		}
		if(changedFlag==1)
		{
			selectedChanger.put(round, voterId);	
		}
		
		RoundData roundData;
		if(roundsData.containsKey(round))
		{
			roundData = roundsData.get(round);
		} else {
			roundData = new RoundData();
			roundData.setGame(gameId);
			roundData.setRound(round);
		}
		
		roundData.setSelection(voterId, selection);
		roundsData.put(round, roundData);
//		if(round>firstRound)
//		{
//			firstRound=round;
//		}
//		if(round<lastRound)
//		{
//			lastRound=round;
//		}		
	}
//	public int getFirstRound()
//	{
//		return firstRound;
//	}
//	public int getLastRound()
//	{
//		return lastRound;
//	}
	
	public String getSelectedChanger(int round)
	{
		return selectedChanger.get(round);
	}
	public Map<String, VoterData> getVoters()
	{		
		return voters;
	}
	
	public Map<Integer, RoundData> getRoundsData()
	{		
		return roundsData;
	}
	
	private static int[] fromString(String prefSet) {
		String[] prefs = prefSet.replace("[", "").replace("]", "").split(", ");
		int prefsArr[] = new int[prefs.length];
		for (int i = 0; i < prefsArr.length; i++) {
			prefsArr[i] = Integer.parseInt(prefs[i]);
		}
		return prefsArr;
	}


	public int getVotersNum() {
		return votersNum;
	}
	public void setVotersNum(int votersNum){
		this.votersNum = votersNum;
	}
	public int getRounds() {
		return rounds;
	}
	public void setRounds(int rounds) {
		this.rounds = rounds;
	}
	public String getPrefSet() {
		return prefSet;
	}
	public void setPrefSet(String prefSet) {
		this.prefSet = prefSet;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getTimePerRound() {
		return timePerRound;
	}
	public void setTimePerRound(int timePerRound) {
		this.timePerRound = timePerRound;
	}
	public Integer getConvTime() {
		return convTime;
	}
	public void setConvTime(Integer convTime) {
		this.convTime = convTime;
	}
	public void addVoteChange() {
		voteChanges.incrementAndGet();
	}
	public int getVoteChanges() {
		return voteChanges.get();
	}

	public Integer getPoa() {
		return poa;
	}
	public void setPoa(Integer poa) {
		this.poa = poa;
	}
	public Map<Integer, Integer> getTruthfulResults() {
		return truthfulResults;
	}
	public void setTruthfulResults(Map<Integer, Integer> truthfulResults) {
		this.truthfulResults = truthfulResults;
	}
	public int getActualVoters() {
		return actualVoters;
	}
	public void setActualVoters(int actualVoters) {
		this.actualVoters = actualVoters;
	}
	public int getWinner() {
		return winner;
	}
	public void setWinner(int winner) {
		this.winner = winner;
	}

	public int getCandsNum() {
		return candsNum;
	}

	public void setCandsNum(int candsNum) {
		this.candsNum = candsNum;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public boolean isIntro() {
		return isIntro;
	}

	public void setIntro(boolean isIntro) {
		this.isIntro = isIntro;
	}

	public int getMad1All() {
		return mad1All;
	}

	public void addMad1All() {
		this.mad1All++;
	}

	public int getMad2All() {
		return mad2All;
	}

	public void addMad2All() {
		this.mad2All++;
	}

	public int getMad12All() {
		return mad12All;
	}

	public void addMad12All() {
		this.mad12All++;
	}

	public int getMad1Selected() {
		return mad1Selected;
	}

	public void addMad1Selected() {
		this.mad1Selected++;
	}

	public int getMad2Selected() {
		return mad2Selected;
	}

	public void addMad2Selected() {
		this.mad2Selected++;
	}

	public int getMad12Selected() {
		return mad12Selected;
	}

	public void addMad12Selected() {
		this.mad12Selected++;
	}


}
