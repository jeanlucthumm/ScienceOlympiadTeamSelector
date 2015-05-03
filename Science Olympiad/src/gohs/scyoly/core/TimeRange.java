package gohs.scyoly.core;

public class TimeRange {

	private Time start;
	private Time stop;

	public TimeRange(Time start, Time stop) {
		this.start = start;
		this.stop = stop;
	}

	public TimeRange(String start, String stop) {
		this(new Time(start), new Time(stop));
	}

	public Time getStart() {
		return start;
	}

	public Time getStop() {
		return stop;
	}

	public boolean containTime(Time t) {
		if (start.compareTo(t) < 0 && t.compareTo(stop) < 0)
			return true;
		else
			return false;
	}

	public boolean overlapsWith(TimeRange other) {
		return start.equals(other.start) || containTime(other.start) || other.containTime(start);
	}
	
	@Override
	public String toString() {
		return start.toString() + "-" + stop.toString();
	}

}
