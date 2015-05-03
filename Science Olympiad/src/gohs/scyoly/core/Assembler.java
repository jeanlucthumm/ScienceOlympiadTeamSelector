package gohs.scyoly.core;

import gohs.scyoly.io.DataReader;
import gohs.scyoly.io.DataWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

@SuppressWarnings("unused")
public class Assembler {

	private Map<Event, Stack<Entry>> feeder;
	private boolean populatedFeeder;
	private List<Crew> crews;

	private Map<Event, Event> simultEvents;
	private boolean populatedSimultEvents;

	public Assembler() {
		feeder = new HashMap<>();
		crews = new ArrayList<>(3);
		simultEvents = new HashMap<>();

		populatedFeeder = false;
		populatedSimultEvents = false;
	}

	private void populateDataStructures() {

		// POPULATE FEEDER

		for (Event e : Event.getAllEvents()) {
			Stack<Entry> sorted = new Stack<>();
			List<Entry> sortedEntries = e.getSortedEntries();
			Collections.reverse(sortedEntries);
			sorted.addAll(sortedEntries);
			feeder.put(e, sorted);
		}

		populatedFeeder = true;

		// POPULATE SIMULT EVENTS

		// identify events that occur at the same time
		List<Event> remainingEvents = new LinkedList<>(Event.getAllEvents());

		for (Event event1 : Event.getAllEvents()) {
			for (Event event2 : remainingEvents) {

				// check if it is the same event
				if (event1 == event2)
					continue;

				// check whether they overlap
				if (event1.overlapsWith(event2)) {
					simultEvents.put(event1, event2);
				}
			}

			// event1 is now exhausted
			remainingEvents.remove(event1);
		}

		populatedSimultEvents = true;
	}

	private void removeFeederTimeConflicts()
			throws MissingDataStructureException {

		// check if we have populated required data structures
		if (!populatedSimultEvents)
			throw new MissingDataStructureException();

		Event event1, event2;
		Team team1, team2;

		// iterate over simultaneous events and find conflicting entries
		for (Map.Entry<Event, Event> entry : simultEvents.entrySet()) {
			event1 = entry.getKey();
			event2 = entry.getValue();

			for (Entry entry1 : event1.getEntries()) {
				for (Entry entry2 : event2.getEntries()) {

					// check if the same student is in both teams
					if (entry1.getTeam().sharesStudents(entry2.getTeam())) {

						System.out.println("Found Conflict: " + event1
								+ " and " + event2); // DEBUG

						if (entry1.getRank() > entry2.getRank()) {
							// entry one has the higher rank and must be removed
							feeder.get(event1).remove(entry1);
						} else if (entry2.getRank() > entry1.getRank()) {
							// entry two has the higher rank and must be removed
							feeder.get(event2).remove(entry2);
							System.out.println(entry2);
						} else {
							// ranks are equal. do nothing and deal with it
							// later
						}
					}
				} // end of small for
			} // end of middle for
		} // end of big for
	}

	private void generateCrews() {

		Crew crew = new Crew(0);
		Event event;
		Entry entry;

		// Get the top team for each Event
		// iterate through the feeder and get the top of each stack
		for (Map.Entry<Event, Stack<Entry>> feederEntry : feeder.entrySet()) {
			event = feederEntry.getKey();
			entry = feederEntry.getValue().pop();
			crew.add(event, entry);
		}

		// Reduce team size if necessary
		if (crew.getSize() > 15) {
			System.out.println("Crew reduction neccessary"); // DEBUG

			// get list of the next step down in feeder
			LinkedHashMap<Event, gohs.scyoly.core.Entry> sorted = new LinkedHashMap<>(feeder.size());

			for (Map.Entry<Event, Stack<Entry>> feederEntry : feeder.entrySet()) {
				sorted.put(feederEntry.getKey(), feederEntry.getValue().peek());
			}
			
			BubbleSort.mapSort(sorted, new EventComparator(crew));

			System.out.println(sorted.entrySet()); // DEBUG
		}

	}

	class MissingDataStructureException extends Exception {

		private static final long serialVersionUID = 1L;
	}

	private class EventComparator implements Comparator<Map.Entry<Event, gohs.scyoly.core.Entry>> {

		Crew crew;

		public EventComparator(Crew crew) {
			this.crew = crew;
		}

		@Override
		public int compare(java.util.Map.Entry<Event, Entry> o1,
				java.util.Map.Entry<Event, Entry> o2) {
			
			int membCount1 = 0;
			int membCount2 = 0;

			// FIXME students that have fewer events on the crew should be
			// preferred
			for (Student s : o1.getValue().getTeam().getMembers())
				if (crew.containsStudent(s)) {
					membCount1++;
					continue;
				}

			for (Student s : o2.getValue().getTeam().getMembers())
				if (crew.containsStudent(s)) {
					membCount2++;
					continue;
				}

			return Integer.compare(membCount1, membCount2);
		}

	}

	private static class BubbleSort {

		public static <K, V> void mapSort(LinkedHashMap<K, V> map,
				Comparator<Map.Entry<K, V>> comparator) {
			
			// array list is more efficient
			ArrayList<Map.Entry<K, V>> entries = new ArrayList<>(map.entrySet());
			int i; // var for iterating
			boolean swap = true; // false if no swaps occurred (list is sorted)
			Map.Entry<K, V> temp;
			
			while(swap) {
				swap = false; // assume no swap will occur
				for (i = 0; i < entries.size() -1; i++)
					if (comparator.compare(entries.get(i), entries.get(i + 1)) < 0) {
						temp = entries.get(i);
						entries.set(i, entries.get(i + 1));
						entries.set(i + 1, temp);
						swap = true;
					}
			}
			
			// re-factor the original map
			map.clear();
			for (Map.Entry<K, V> entry : entries)
				map.put(entry.getKey(), entry.getValue());
		}

	}

	public static void main(String[] args) throws BiffException, IOException,
			RowsExceededException, WriteException,
			MissingDataStructureException {
		// DataWriter.generateTemplate("out.xls", 8);

		DataReader dr = new DataReader("in.xls");
		DataWriter dw = new DataWriter("temp.xls");

		System.out.println("Data structure dump:");

		dr.populateEvents();
		System.out.println(Event.getAllEvents());
		System.out.println(Team.getAllTeams());
		System.out.println(Student.getAllStudents());
		System.out.println();

		System.out.println("Sorted entries of astronomy:");

		Event e = Event.getEventByName("astronomy");
		System.out.println(e.getSortedEntries());
		System.out.println();

		System.out.println("Concurrent Events:");

		Assembler a = new Assembler();
		a.populateDataStructures();
		System.out.println(a.simultEvents.entrySet());
		System.out.println(a.feeder.get(e));
		a.removeFeederTimeConflicts();
		System.out.println("Astronomy entries after resolution:");
		System.out.println(a.feeder.get(e));
		System.out.println("Cell Biology entries after resolution");
		System.out.println(a.feeder.get(Event.getEventByName("cell biology")));
		System.out.println();

		System.out.println("Entry set of first crew");
		a.generateCrews();
		System.out.println(Crew.getAllCrews().get(0).entrySet());

		dw.writeCrew(Crew.getAllCrews().get(0));
		dw.writeCrew(Crew.getAllCrews().get(0));
	}
}
