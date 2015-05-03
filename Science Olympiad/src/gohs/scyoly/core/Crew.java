package gohs.scyoly.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Crew {

	private static List<Crew> allCrews = new ArrayList<>();

	public static List<Crew> getAllCrews() {
		return allCrews;
	}

	public int rank;
	public Map<Event, gohs.scyoly.core.Entry> roster;
	public List<Student> students;

	public Crew(int rank) {
		this.rank = rank;
		roster = new HashMap<>();
		students = new LinkedList<>();
		allCrews.add(this);
	}

	public int getRank() {
		return rank;
	}

	public Map<Event, gohs.scyoly.core.Entry> getRoster() {
		return roster;
	}

	public void add(Event event, gohs.scyoly.core.Entry entry) {
		// capture original students if they are there
		List<Student> prev = null;
		if (roster.containsKey(event)) {
			prev = new ArrayList<>();
			prev.addAll(roster.get(event).getTeam().getMembers());
		}

		roster.put(event, entry);
		List<Student> members = entry.getTeam().getMembers();
		for (Student s : members) {
			if (containsStudent(s))
				continue;
			else
				students.add(s);
		}

		// if there were original students, check if they need to be discarded
		if (prev != null) {
			boolean contains = false;
			for (Student s : prev) {
				if (!containsStudent(s))
					students.remove(s);

				small: for (Map.Entry<Event, gohs.scyoly.core.Entry> rosterEntry : roster
						.entrySet()) {
					if (rosterEntry.getValue().getTeam().containsStudent(s)) {
						contains = true;
						break small;
					}
				}
				
				if (!contains)
					students.remove(s);
			}
		}
	}

	public Set<Entry<Event, gohs.scyoly.core.Entry>> entrySet() {
		return roster.entrySet();
	}
	
	public gohs.scyoly.core.Entry getEntry(Event e) {
		return roster.get(e);
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

	public double getAverageRank() {
		double total = 0;
		for (Map.Entry<Event, gohs.scyoly.core.Entry> rosterEntry : roster
				.entrySet()) {
			total += rosterEntry.getValue().getRank();
		}
		return total / roster.size();
	}

	public boolean containsStudent(Student s) {
		for (Student student : students)
			if (student.equals(s))
				return true;
		return false;
	}

	public boolean containsStudent(String name) {
		for (Student student : students)
			if (student.getName().equals(name))
				return true;
		return false;
	}

	public int getSize() {
		return students.size();
	}

}
