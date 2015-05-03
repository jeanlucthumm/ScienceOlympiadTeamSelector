package gohs.scyoly.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

public class Event {
	
	private static List<Event> allEvents = new ArrayList<>();
	
	public static List<Event> getAllEvents() {
		return allEvents;
	}
	
	public static boolean removeEvent(String name) {
		
		for (int i = 0; i < allEvents.size(); i++) {
			if (allEvents.get(i).name.equalsIgnoreCase(name)) {
				allEvents.remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean removeEvent(Event e) {
		return allEvents.remove(e);
	}
	
	public static Event getEventByName(String name) {
		
		String currentName;
		for (int i = 0; i < allEvents.size(); i++) {
			currentName = allEvents.get(i).getName();
			if (name.equalsIgnoreCase(currentName))
					return allEvents.get(i);
		}
		
		return null;
	}
	
	private String name;
	private TimeRange timeRange;
	private List<Entry> entries;
	
	public Event(String name, TimeRange timeRange) {
		this.name = WordUtils.capitalizeFully(name.trim());
		this.timeRange = timeRange;
		allEvents.add(this);
		entries = new ArrayList<Entry>();
	}
	
	public Event(String name, Time start, Time stop) {
		this(name, new TimeRange(start, stop));
	}
	
	public Event(String name, String start, String stop) {
		this (name, new Time(start), new Time(stop));
	}

	public String getName() {
		return name;
	}

	public TimeRange getTimeRange() {
		return timeRange;
	}

	public List<Entry> getEntries() {
		return entries;
	}
	
	public void addEntry(Team team, int rank) {
		entries.add(new Entry(team, rank));
	}
	
	public boolean removeEntry(Team team) {
		Iterator<Entry> itr = entries.iterator();
		Entry e;
		
		while(itr.hasNext()) {
			e = itr.next();
			if (e.getTeam() == team) {
				itr.remove();
				return true;
			}
		}
		
		return false;
	}
	
	public boolean removeEntry(String team) {
		Team t = Team.getTeamByName(team);
		return removeEntry(t);
	}
	
	public List<Entry> getSortedEntries() {
		Collections.sort(entries);
		return entries;
	}
	
	public boolean overlapsWith(Event o) {
		return timeRange.overlapsWith(o.timeRange);
	}
	
	
	@Override
	public String toString() {
		return name;
	}
}