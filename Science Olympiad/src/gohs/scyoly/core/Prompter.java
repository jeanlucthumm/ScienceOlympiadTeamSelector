package gohs.scyoly.core;

import java.util.Scanner;

public class Prompter {

	private final static String[] HELP = {
			"e - new/rm event | e [add/rm] [name]",
			"t - new/rm team | t [add/rm] [name] [event] [score]", "b - break",
			"l - list | l events | l teams [event] | l teams all" };

	private final static String PROMPT = ">>> ";
	private final static String SUCCESS = "Command completed succesfuly.";
	private final static String FAIL = "Command failed.";
	private final static String UNKNOWN = "Unknown command. Type 'h' for help.";

	private Scanner scanner;

	public Prompter() {
		scanner = new Scanner(System.in);
	}

	public void mainLoop() {
		String input;
		String[] args;
		boolean success;

		main: while (true) {

			success = false;
			System.out.print(PROMPT);

			input = scanner.nextLine();
			args = input.split(" ");

			try {
				switch (args[0]) {

				case "b":
					break main;
				case "h":
					printHelp();
					continue;
				case "l":
					printList(args);
					continue;
				case "e":
					success = manageEvent(args);
					break;
				case "t":
					success = manageTeam(args);
					break;
				default:
					System.out.println(UNKNOWN);
					break;
				}

				if (success) {
					System.out.println(SUCCESS);
				} else {
					System.out.println(FAIL);
				}
			} catch (Exception e) {
				System.out.println(FAIL);
			}
		} // end of while loop
	}

	private void printHelp() {
		for (int i = 0; i < HELP.length; i++) {
			System.out.println(HELP[i]);
		}
	}

	private void printList(String[] args) {
		switch (args[1]) {
		case "events":
			for (Event e : Event.getAllEvents()) {
				System.out.println(e);
			}
			break;
		case "teams":

			if (args[2].equals("all")) {
				for (Team t : Team.getAllTeams()) {
					System.out.println(t);
				}

				break;
			}

			Event e = Event.getEventByName(args[2]);
			if (e == null)
				break;

			for (Entry entry : e.getSortedEntries()) {
				System.out.println(entry);
			}
			break;
		default:
			System.out.println(FAIL);
		}
	}

	private boolean manageEvent(String[] args) {

		if (args[1].equals("add")) {
//			new Event(args[2]);
			return true;
		} else if (args[1].equals("rm")) {
			return Event.removeEvent(args[2]);
		} else {
			return false;
		}
	}

	private boolean manageTeam(String[] args) {
		if (args[1].equals("add")) {
			// add a team

			Team t = new Team(args[2]);
			Event e = Event.getEventByName(args[3]);
			if (e == null)
				// no such team was found
				return false;
			else {
				e.addEntry(t, Integer.parseInt(args[4]));
				return true;
			}
		} else if (args[1].equals("rm")) {
			// remove a team
			
			Event e = Event.getEventByName(args[3]);
			if (e == null)
				// no such team was found
				return false;
			else {
				return Event.removeEvent(e);
			}
		} else {
			// command could not be understood
			return false;
		}
	}

	public static void main(String[] args) {
		Prompter p = new Prompter();
		p.mainLoop();
	}

}
