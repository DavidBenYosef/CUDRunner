package runner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Util {
	public static List<Integer> generatePWF(Map<Integer, Integer> results, int roundsLeft, int voters) {
		List<Integer> pwf = new ArrayList<Integer>();
		for (Entry<Integer, Integer> entry : results.entrySet()) {
			int cand = entry.getKey();
			if (isPW(results.get(cand), roundsLeft, voters)) {
				pwf.add(cand);
			}
		}
		return pwf;
	}

	public static boolean isPW(int score, int roundsLeft, int voters) {
		return score + roundsLeft >= voters;
	}

	public static Integer generatePoa(Map<Integer, Integer> truthfulResults, int winner) {
		Map.Entry<Integer, Integer> maxEntry = null;

		for (Map.Entry<Integer, Integer> entry : truthfulResults.entrySet()) {
			if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
				maxEntry = entry;
			}
		}
		return maxEntry.getValue() - truthfulResults.get(winner);
	}

	public static int getWinner(Map<Integer, Integer> results) {

		int winner = 0;
		for (Map.Entry<Integer, Integer> result : results.entrySet()) {
			if (result.getValue() > 0) {
				if (winner > 0) {
					return 0;
				} else {
					winner = result.getKey();
				}
			}
		}

		return winner;
	}
	
	public static int calculateScore(int candsNum, int candIndex) {
		return (int) Math.round(100 / (double) candsNum * (candsNum - candIndex));
	}
	
	public static void initCountMap(Map<Integer,Integer> map,int first,int last)
	{
		for (int i = first; i <= last; i++) {
			map.put(i, 0);
		}
	}
}
