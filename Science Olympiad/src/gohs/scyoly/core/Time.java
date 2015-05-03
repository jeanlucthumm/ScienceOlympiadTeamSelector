package gohs.scyoly.core;

public class Time implements Comparable<Time> {
	
	private int hour;
	private int min;
	
	public Time(int hour, int min) {
		this.hour = hour;
		this.min = min;
	}
	
	public Time(String time) {
		String[] temp = time.split(":");
		int hour = Integer.parseInt(temp[0].trim());
		int min = Integer.parseInt(temp[1].trim());
		this.hour = hour;
		this.min = min;
	}

	public int getHour() {
		return hour;
	}

	public int getMin() {
		return min;
	}
	
	@Override
	public int compareTo(Time o) {
		if (this.hour < o.hour)
			return -1;
		else if (this.hour > o.hour)
			return 1;
		else {
			if (this.min < o.min)
				return -1;
			else if (this.min > o.min)
				return 1;
			else
				return 0;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Time)
			return compareTo((Time) obj) == 0;
		else
			return false;
	}
	
}
