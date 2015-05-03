package gohs.scyoly.core;

import gohs.scyoly.io.DataReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
						
						System.out.println("Found Conflict: " + event1 + " and " + event2); // DEBUG
						
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
		
	}
	

	class MissingDataStructureException extends Exception {

		private static final long serialVersionUID = 1L;
	}

	public static void main(String[] args) throws BiffException, IOException,
			RowsExceededException, WriteException, MissingDataStructureException {
		// DataWriter.generateTemplate("out.xls", 8);

		DataReader dr = new DataReader("in.xls");

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
		
		
		

	}
}
