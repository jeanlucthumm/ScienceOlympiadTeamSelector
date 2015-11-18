package gohs.scyoly.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

	public Map<Event, Entry> getRoster() {
		return roster;
	}

	public Entry add(Event event, Entry entry) {
		// capture original students if they are there
		List<Student> prev = null; // students to be replaced
		Entry res = null; // entry of event to be replaced
		
		if (roster.containsKey(event)) { // entry already present
			prev = new ArrayList<>();
			res = roster.get(event);
			
			List<Student> temp = res.getTeam().getMembers();
			prev.addAll(temp);
			
			// tell each student they are not part of event anymore
			for (Student s : temp) {
				s.removeEvent(event);
			}
		}

		// add the students to the roster and notify them
		roster.put(event, entry);
		List<Student> members = entry.getTeam().getMembers();
		for (Student s : members) {
			if (containsStudent(s)) {
				s.addEvent(event); // notify student
			}
			else {
				students.add(s); // add to crew
				s.addEvent(event); // notify student
			}
		}

		// if there were original students, check if they need to be discarded
		// (they are not enrolled in any other events for this crew)
		if (prev != null) {
			boolean contains = false;
			for (Student s : prev) {

				for (Map.Entry<Event, Entry> rosterEntry : roster
						.entrySet()) {
					if (rosterEntry.getValue().getTeam().containsStudent(s)) {
						contains = true;
						break;
					}
				}
				
				if (!contains) // student is no longer part of crew
					students.remove(s);
			}
		} // end of big if
		return res;
	}

	public Set<Map.Entry<Event, Entry>> entrySet() {
		return roster.entrySet();
	}
	
	public Entry getEntry(Event e) {
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
		for (Map.Entry<Event, Entry> rosterEntry : roster
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
