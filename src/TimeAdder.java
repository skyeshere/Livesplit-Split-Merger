/* REPURPOSED FROM https://stackoverflow.com/questions/26786098/java-add-dates-of-format-ddhhmmss */

public class TimeAdder
{
	int hours = 0;
	int minutes = 0;
	double seconds = 0.000; //decimal accounts for milliseconds

	public TimeAdder parse(String input_time)
	{
		String[] time_parts = input_time.split(":");
		TimeAdder ta = new TimeAdder();

		ta.hours = ((time_parts.length >= 1) ? Integer.parseInt(time_parts[0]) : 0);
		ta.minutes = ((time_parts.length >= 2) ? Integer.parseInt(time_parts[1]) : 0);
		ta.seconds = ((time_parts.length >= 3) ? Double.parseDouble(time_parts[2]) : 0.00);

		return ta;
	}

	public TimeAdder add(TimeAdder a)
	{
		this.seconds += a.seconds;

		int additional_minutes = 0;

		while (this.seconds >= 60.00000)
		{
			additional_minutes++;
			this.seconds -= 60.00000;
		}

		this.minutes += a.minutes + additional_minutes;

		int additional_hours = 0;
		while (this.minutes >= 60)
		{
			additional_hours++;
			this.minutes -= 60;
		}

		this.hours += a.hours + additional_hours;

		return this;
	}

	@Override
	public String toString()
	{
		return String.format("%02d:%02d:%.7f", hours, minutes, seconds);
	}
}