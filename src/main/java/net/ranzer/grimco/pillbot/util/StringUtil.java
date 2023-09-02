package net.ranzer.grimco.pillbot.util;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.zone.ZoneRulesException;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	/**
	 * pieces together a collection of strings separated by the supplied delimiter
	 * @param stringArray the collection of strings to be appended
	 * @param delimiter the separator between each string in the stringArray
	 * @return a single string of all the strings in the stringArray separated by the delimiter
	 */
	public static String arrayToString(Collection<String> stringArray, String delimiter) {
		StringBuilder sb = new StringBuilder();

		for (String s: stringArray) {
			sb.append(s).append(delimiter);
		}
		sb.delete(sb.length()-delimiter.length(),sb.length());

		return sb.toString();
	}

	/**
	 * overloaded method of {@link StringUtil#arrayToString(Collection stringArray, String delimiter)}
	 */
	public static String arrayToString(String[] stringArray, String delimiter){
		return arrayToString(Arrays.asList(stringArray), delimiter);
	}

	/**
	 * calculates a human readable time from a seconds long
	 * @param runtime time in seconds
	 * @return human readable time string
	 */
	public static String calcTime(long runtime) {
		StringBuilder rtn = new StringBuilder();

		long hrs = runtime / 3600;
		long mins = (runtime % 3600)/60;
		long secs = runtime % 60;

		if(hrs >1){
			rtn.append(hrs).append(" Hours ");
		} else if (hrs==1){
			rtn.append(hrs).append(" Hour ");
		}
		if(mins>1){
			rtn.append(mins).append(" Minutes ");
		} else if (mins==1){
			rtn.append(mins).append(" Minute ");
		}
		if (secs>1){
			rtn.append(secs).append(" Seconds");
		} else if (secs==1){
			rtn.append(secs).append(" Second");
		}
		return rtn.toString();
	}

	/**
	 * @param string String to be shortened
	 * @param size max length of returned string
	 * @return shortened version of the supplied string. if the string is shorter
	 * than the max length, it returns the string as is
	 */
	public static String truncate(String string, int size) {
		if (string.length()<size) {
			return string;
		} else {
			return string.substring(0, size);
		}
	}

	/**
	 * overloaded method with default width of 32
	 * @param current current length to be displayed
	 * @param total the total length to be represented
	 * @return a discord formatted code block with a graphical progress bar
	 * @see StringUtil#playingBar(long current, long total, int width)
	 */
	public static String playingBar(long current, long total){
		return playingBar(current,total,32);
	}

	/**
	 * calculates the proper tic per time
	 * @param current current length to be displayed
	 * @param total the total length to be represented
	 * @param width the overall width of the progress bar to be displayed
	 * @return a discord formatted code block with a graphical progress bar
	 */
	public static String playingBar(long current, long total,int width) {
		StringBuilder sb = new StringBuilder();

		long timePerBar = total/width;

		sb.append("```\n");
		sb.append("-");
		sb.append("-".repeat(Math.max(0, width)));
		sb.append("-\n");
		sb.append("|");

		for (int i = 0; i < current/timePerBar; i++) {
			sb.append("=");
		}
		sb.append("|");
		for (int i = 0; i < width-1-(current/timePerBar); i++) {
			sb.append(" ");
		}

		sb.append("|\n");
		sb.append("-");
		sb.append("-".repeat(Math.max(0, width)));
		sb.append("-\n")
				.append("```\n")
				.append(calcTime(current / 1000))
				.append(" of ")
				.append(calcTime(total / 1000));

		return sb.toString();
	}

	public static ZonedDateTime parseTimeWithTimeZone(String userInput) throws DateTimeParseException,
	                                                                           ZoneRulesException {
		// Define a regular expression to match various time formats including time zone
		String pattern = "(?<hour>[0-9]{1,2}):?(?<minute>[0-9]{2})?(?::?([0-9]{2}))?\\s*(?<ap>[ap]m?\\b)?\\s*(?<tz>[A-Za-z_/\\\\]+)?";
		Pattern regexPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = regexPattern.matcher(userInput);

		if (matcher.matches()) {
			int hours = Integer.parseInt(matcher.group("hour"));
			int minutes = matcher.group("minute")!=null ?Integer.parseInt(matcher.group("minute")) : 0;
			String amPm = matcher.group("ap");
			String timeZone = matcher.group("tz");

			if (amPm != null) {
				if (amPm.toLowerCase().charAt(0)== 'p'&& hours < 12) {
					hours += 12;
				} else if (amPm.toLowerCase().charAt(0) == 'a' && hours == 12) {
					hours = 0;
				}
			}
				// Parse the time
				LocalTime localTime = LocalTime.of(hours, minutes, 0);

				// If a time zone is provided, create a ZonedDateTime
				if (timeZone != null) {
					ZoneId zoneId = ZoneId.of(timeZone);
					return ZonedDateTime.of(ZonedDateTime.now().toLocalDate(), localTime, zoneId);
				} else {
					// If no time zone is provided, use the system's default time zone
					return ZonedDateTime.of(ZonedDateTime.now().toLocalDate(), localTime, ZoneId.systemDefault());
				}
		}

		return null; // Return null if the input doesn't match any valid format
	}

	public static void main(String[] args) {
		String[] userInputs = {"6:30 pm", "10:15am US/Mountain", "5p GMT", "2a US/eastern", "22:15", "1:15", "14:30", "10:00 AM US/Central","a;lsdkf"};

		ZoneId.getAvailableZoneIds().stream().sorted().forEach(System.out::println);

		for (String userInput : userInputs) {
			try {
				ZonedDateTime parsedTime = parseTimeWithTimeZone(userInput);
				if (parsedTime != null) {
					System.out.println("User Input: " + userInput);
					System.out.println("Parsed Time: " + parsedTime);
					System.out.println("that is: "+parsedTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalTime() +" Central Time");
					System.out.println();
				} else {
					System.out.println("Error parsing time from: " + userInput);
				}
			} catch (ZoneRulesException zre){
				System.out.println("I'm sorry but i didn't understand the timezone. i got the error \""+zre.getMessage() +
				                   "\" This is likely caused by Capitalization or a space. the I tried to make the code as user " +
				                   "friendly as possible, but Java is persnickety about Timezones. Acceptable Timezones include: \n" +
				                   "America/New_York\n" +
				                   "US/Central\n" +
				                   "GMT\n" +
				                   "UTC+3");
			}
		}
	}
}
