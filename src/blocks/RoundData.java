package blocks;

import java.util.HashMap;
import java.util.Map;

public class RoundData {	
	
	private Map<String, Integer> selectionsMap = new HashMap<String, Integer>();
	
	private int voteChanges;
	
	private int voteChangesForPopular;
	
	private int round;
	
	private int game;
	
	private Map<Integer, Integer> baseResults;
	
	private String changerId;
	
	private Integer changedFrom;
	
	private Integer changedTo;

	public void setSelection(String voterId, Integer selection) {
		if(!selectionsMap.containsKey(voterId))
		{
			selectionsMap.put(voterId, selection);
		} else {
			//throw new RuntimeException("Game: "+game+" Round: "+round+" The voter selection already exist for voter:"+voterId+" SelectionMap:"+selectionsMap);
		}		
	}
	
	public Map<String, Integer> getsSelectionsMap() {
		return selectionsMap;
	}
	
	public int getVotersNum()
	{
		return selectionsMap.size();
	}

	public int getVoteChanges() {
		return voteChanges;
	}

	public void addVoteChange() {
		this.voteChanges++;
	}

	public int getVoteChangesForPopular() {
		return voteChangesForPopular;
	}

	public void addVoteChangesForPopular() {
		this.voteChangesForPopular++;
	}

	public int getGame() {
		return game;
	}

	public void setGame(int game) {
		this.game = game;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public Map<Integer, Integer> getBaseResults() {
		return baseResults;
	}

	public void setBaseResults(Map<Integer, Integer> baseResults) {
		this.baseResults = baseResults;
	}

	public String getChangerId() {
		return changerId;
	}

	public void setChangerId(String changerId) {
		this.changerId = changerId;
	}

	public Integer getChangedFrom() {
		return changedFrom;
	}

	public void setChangedFrom(Integer changedFrom) {
		this.changedFrom = changedFrom;
	}

	public Integer getChangedTo() {
		return changedTo;
	}

	public void setChangedTo(Integer changedTo) {
		this.changedTo = changedTo;
	}	
	
}
