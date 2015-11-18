package gohs.scyoly.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

public class Student {

	private static List<Student> allStudents = new ArrayList<>();

	public static List<Student> getAllStudents() {
		return allStudents;
	}

	public static Student getStudentByName(String name) {
		for (Student s : allStudents)
			if (s.name.equalsIgnoreCase(name))
				return s;
		return null;
	}

	public static boolean exists(String name) {
		return getStudentByName(name) != null;
	}

	private String name;
	private Crew crew;
	private List<Event> events; // the amount of events they are actually
								// partaking in, not the amount they took test
								// in

	public Student(String name) {
		this.name = WordUtils.capitalizeFully(name.trim());
		this.crew = null;
		this.events = new ArrayList<>();
		allStudents.add(this);
	}

	public String getName() {
		return name;
	}

	public List<Event> getEvents() {
		return events;
	}

	public Crew getCrew() {
		return crew;
	}

	public void setCrew(Crew crew) {
		this.crew = crew;
	}

	public void addEvent(Event e) {
		events.add(e);
	}

	public boolean removeEvent(Event e) {
		return events.remove(e);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Student) {
			Student s = (Student) o;
			return name.equalsIgnoreCase(s.name);
		} else {
			return false;
		}
	}
}
