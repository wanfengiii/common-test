package com.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

public abstract class DateUtils {

	public static final String DATE_PATTERN = "yyyy-MM-dd";

	public static final String TIME_PATTERN = "HH:mm:ss";

	public static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);
	
	public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

	/**
	 * multiple patterns supported
	 * 
	 * yyyy-MM-dd'T'HH:mm:ss
	 * yyyy-MM-dd HH:mm:ss
	 * yyyy-MM-dd'T'HH:mm:ssZ
	 * yyyy-MM-dd HH:mm:ssZ
	 */
	public static final DateTimeFormatter DATETIME_FORMATTERS = 
		new DateTimeFormatterBuilder()
			.append(DATE_FORMATTER)
			.optionalStart()
			.appendPattern("'T'")
			.optionalEnd()
			.optionalStart()
			.appendPattern(" ")
			.optionalEnd()
			.append(TIME_FORMATTER)
			.optionalStart()
			.appendOffset("+HHMM", "0000")
			.optionalEnd()
			.toFormatter();

	public static String format(LocalDate date) {
		return date.format(DATE_FORMATTER);
	}

	public static String getDay(int days) {
		return LocalDate.now().plusDays(days).format(DATE_FORMATTER);
	}
   
	public static String plusDays(String day, int days) {
		return LocalDate.parse(day, DATE_FORMATTER).plusDays(days).format(DATE_FORMATTER);
	}

	public static LocalDateTime toLocalDateTime(Date d) {
		return (d == null ? null : d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
	}

}