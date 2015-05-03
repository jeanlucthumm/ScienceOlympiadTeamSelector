package gohs.scyoly.core;

public class Entry implements Comparable<Entry> {
	
	private Team team;
	private int rank;
	
	public Entry(Team team, int rank) {
		this.team = team;
		this.rank = rank;
	}
	
	public Team getTeam() {
		return team;
	}
	
	public int getRank() {
		return rank;
	}
	
	@Override
	public int compareTo(Entry o) {
		if (this.rank < o.rank)
			return -1;
		if (this.rank > o.rank)
			return 1;
		else
			return 0;
	}
	
	@Override
	public String toString() {
		return "[" + team + " : " + rank + "]";
	}
}
