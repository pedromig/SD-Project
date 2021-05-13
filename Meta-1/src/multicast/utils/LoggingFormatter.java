package multicast.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * A simple utility class containing 2 formatting classes that extend {@link SimpleFormatter}  and that are used to
 * customized the logging messages emitted by the logging system used in the multicast server. This class is marked
 * with final to prevent the object to be mutated.
 *
 * @author Pedro Rodrigues
 * @author Miguel Rabuge
 * @version 1.0
 * @see java.util.Date
 * @see java.util.logging.LogRecord
 * @see java.util.logging.SimpleFormatter
 * @see java.util.logging.Logger
 */
public final class LoggingFormatter {

	/**
	 * @implNote ASCII terminal color codes (for colorful console output)
	 */
	private static final String RED = "\u001B[31m";
	private static final String BLUE = "\u001B[34m";
	private static final String YELLOW = "\u001B[33m";
	private static final String RESET = "\u001B[0m";

	/**
	 * @implNote The format string used by the simple formatter as a preamble of the log messages
	 */
	private static final String format = "[%1$tF %1$tT][%2$s]: %3$s %n";

	/**
	 * The inner class that provides the implementation of the format method for a console logger.
	 */
	public static class ConsoleFormatter extends SimpleFormatter {
		/**
		 * Format the given LogRecord.
		 * <p>
		 * The formatting can be customized by specifying the
		 * <a href="../Formatter.html#syntax">format string</a>
		 * in the <a href="#formatting">
		 * {@code java.util.logging.SimpleFormatter.format}</a> property.
		 * The given {@code LogRecord} will be formatted as if by calling:
		 * <pre>
		 *    {@link String#format String.format}(format, date, source, logger, level, message, thrown);
		 * </pre>
		 * where the arguments are:<br>
		 * <ol>
		 * <li>{@code format} - the {@link java.util.Formatter
		 *     java.util.Formatter} format string specified in the
		 *     {@code java.util.logging.SimpleFormatter.format} property
		 *     or the default format.</li>
		 * <li>{@code date} - a {@link ZonedDateTime} object representing
		 *     {@linkplain LogRecord#getInstant() event time} of the log record
		 *      in the {@link ZoneId#systemDefault()} system time zone.</li>
		 * <li>{@code source} - a string representing the caller, if available;
		 *     otherwise, the logger's name.</li>
		 * <li>{@code logger} - the logger's name.</li>
		 * <li>{@code level} - the {@linkplain Level#getLocalizedName
		 *     log level}.</li>
		 * <li>{@code message} - the formatted log message
		 *     returned from the {@link Formatter#formatMessage(LogRecord)}
		 *     method.  It uses {@link java.text.MessageFormat java.text}
		 *     formatting and does not use the {@code java.util.Formatter
		 *     format} argument.</li>
		 * <li>{@code thrown} - a string representing
		 *     the {@linkplain LogRecord#getThrown throwable}
		 *     associated with the log record and its backtrace
		 *     beginning with a newline character, if any;
		 *     otherwise, an empty string.</li>
		 * </ol>
		 *
		 * @param record the log record to be formatted.
		 * @return a formatted log record
		 */
		@Override
		public synchronized String format(LogRecord record) {
			switch (record.getLevel().getName().toUpperCase()) {
				case "INFO":
					return String.format(format,
							new Date(record.getMillis()),
							BLUE + record.getLevel().getLocalizedName() + RESET,
							record.getMessage()
					);
				case "SEVERE":
					return String.format(format,
							new Date(record.getMillis()),
							RED + "ERROR" + RESET,
							record.getMessage()
					);
				case "WARNING":
					return String.format(format,
							new Date(record.getMillis()),
							YELLOW + "WARN" + RESET,
							record.getMessage()
					);
			}
			return null;
		}
	}

	/**
	 * The inner class that provides the implementation of the format method for a file logger.
	 */
	public static class FileFormatter extends SimpleFormatter {

		/**
		 * Format the given LogRecord.
		 * <p>
		 * The formatting can be customized by specifying the
		 * <a href="../Formatter.html#syntax">format string</a>
		 * in the <a href="#formatting">
		 * {@code java.util.logging.SimpleFormatter.format}</a> property.
		 * The given {@code LogRecord} will be formatted as if by calling:
		 * <pre>
		 *    {@link String#format String.format}(format, date, source, logger, level, message, thrown);
		 * </pre>
		 * where the arguments are:<br>
		 * <ol>
		 * <li>{@code format} - the {@link java.util.Formatter
		 *     java.util.Formatter} format string specified in the
		 *     {@code java.util.logging.SimpleFormatter.format} property
		 *     or the default format.</li>
		 * <li>{@code date} - a {@link ZonedDateTime} object representing
		 *     {@linkplain LogRecord#getInstant() event time} of the log record
		 *      in the {@link ZoneId#systemDefault()} system time zone.</li>
		 * <li>{@code source} - a string representing the caller, if available;
		 *     otherwise, the logger's name.</li>
		 * <li>{@code logger} - the logger's name.</li>
		 * <li>{@code level} - the {@linkplain Level#getLocalizedName
		 *     log level}.</li>
		 * <li>{@code message} - the formatted log message
		 *     returned from the {@link Formatter#formatMessage(LogRecord)}
		 *     method.  It uses {@link java.text.MessageFormat java.text}
		 *     formatting and does not use the {@code java.util.Formatter
		 *     format} argument.</li>
		 * <li>{@code thrown} - a string representing
		 *     the {@linkplain LogRecord#getThrown throwable}
		 *     associated with the log record and its backtrace
		 *     beginning with a newline character, if any;
		 *     otherwise, an empty string.</li>
		 * </ol>
		 *
		 * @param record the log record to be formatted.
		 * @return a formatted log record
		 */
		@Override
		public synchronized String format(LogRecord record) {
			switch (record.getLevel().getName().toUpperCase()) {
				case "INFO":
					return String.format(format,
							new Date(record.getMillis()),
							record.getLevel().getLocalizedName(),
							record.getMessage()
					);
				case "SEVERE":
					return String.format(format,
							new Date(record.getMillis()),
							"ERROR",
							record.getMessage()
					);
				case "WARNING":
					return String.format(format,
							new Date(record.getMillis()),
							"WARN",
							record.getMessage()
					);
			}
			return null;
		}
	}
}
