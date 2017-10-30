package blocks;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class VoterData {
	
	private int gameId;
	private String name;
	private String uid;
	private int[] orderedCands;
	private Integer selectedCand;
	private Integer nextCand;
	private Integer score;
	private int voteChanges;
	private int voteChangeForPW;
	private int voteChangeForNPW;
	private int irr;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int[] getOrderedCands() {
		return orderedCands;
	}

	public void setOrderedCands(int[] orderedCands) {
		this.orderedCands = orderedCands;
	}

	public Integer getSelectedCand() {
		return selectedCand;
	}

	public void setSelectedCand(Integer selectedCand) {
		this.selectedCand = selectedCand;
	}

	public Integer getNextCand() {
		return nextCand;
	}

	public void setNextCand(Integer nextCand) {
		this.nextCand = nextCand;
	}


	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public int getVoteChanges() {
		return voteChanges;
	}

	public void addVoteChange() {
		this.voteChanges++;
	}

	public int getVoteChangeForPW() {
		return voteChangeForPW;
	}

	public void addVoteChangeForPW() {
		this.voteChangeForPW++;
	}

	public int getVoteChangeForNPW() {
		return voteChangeForNPW;
	}

	public void addVoteChangeForNPW() {
		this.voteChangeForNPW++;
	}
	public List<Integer> getOrderedCandsList()
	{
		return IntStream.of(orderedCands).boxed().collect(Collectors.toList());
	}

	public int getIrr() {
		return irr;
	}

	public void addIrr() {
		this.irr++;
	}

}
