package gohs.scyoly.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

public class Team {

	private static List<Team> allTeams = new ArrayList<>();

	public static List<Team> getAllTeams() {
		return allTeams;
	}

	public static Team getTeamByName(String name) {

		for (int i = 0; i < allTeams.size(); i++) {
			if (allTeams.get(i).asString.equalsIgnoreCase(name)) {
				return allTeams.get(i);
			}
		}

		return null;
	}

	private String asString;
	private List<Student> members;

	public Team(Collection<Student> students) {
		allTeams.add(this);
		members = new ArrayList<Student>(3);
		members.addAll(students);
		asString = WordUtils
				.capitalizeFully(convertStudentCollectionToString(students));
	}

	public Team(String students) {
		allTeams.add(this);
		members = new ArrayList<Student>(3);
		asString = WordUtils.capitalizeFully(students);
		addStudentsFromString(students);
	}

	private void addStudentsFromString(String students) {
		String[] s = students.split(",");
		Student temp;
		
		for (int i = 0; i < s.length; i++) {
			// check if student exists
			temp = Student.getStudentByName(s[i].trim());
			if (temp != null) {
				members.add(temp);
			}
			else
				members.add(new Student(s[i].trim()));
		}
	}

	private String convertStudentCollectionToString(Collection<Student> students) {
		StringBuilder br = new StringBuilder();
		Iterator<Student> i = students.iterator();

		while (i.hasNext()) {
			br.append(i.next().toString());
			if (i.hasNext())
				br.append(", ");
		}

		return br.toString();
	}

	public List<Student> getMembers() {
		return members;
	}

	public String getMembersAsString() {
		return asString;
	}
	
	public int getSize() {
		return members.size();
	}
	
	public boolean sharesStudents(Team o) {
		for (Student s1 : members)
			for (Student s2 : o.members)
				if (s1 == s2)
					return true;
		return false;
	}
	
	public boolean containsStudent(Student s) {
		for (Student student : members)
			if (student.equals(s))
				return true;
		return false;
	}
	
	public boolean containsStudent(String name) {
		for (Student student : members)
			if (student.getName().equals(name))
				return true;
		return false;
	}

	@Override
	public String toString() {
		return asString;
	}
	
}
