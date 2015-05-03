package gohs.scyoly.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Crew {
	
	private static List<Crew> allCrews = new ArrayList<>();
	
	public static List<Crew> getAllCrews() {
		return allCrews;
	}

	public int rank;
	public Map<Event, gohs.scyoly.core.Entry> roster;
	
	public Crew(int rank) {
		this.rank = rank;
		roster = new HashMap<>();
		allCrews.add(this);
	}
	
	public int getRank() {
		return rank;
	}

	public Map<Event, gohs.scyoly.core.Entry> getRoster() {
		return roster;
	}
	
	public void add(Event event, gohs.scyoly.core.Entry entry) {
		roster.put(event, entry);
	}
	
	public Set<Entry<Event,gohs.scyoly.core.Entry>> entrySet() {
		 return roster.entrySet();
	}
	
	public List<TimeRange> getSortedTimeRanges() {
		
		List<TimeRange> allTimeRanges = new ArrayList<>(roster.size());
		
		// iterate over roster and get time ranges
		for (Event event : roster.keySet()) {
			allTimeRanges.add(event.getTimeRange());
		}
		
		Collections.sort(allTimeRanges); // sort
		return allTimeRanges;
	}
	
	
}
