package multicast.utils;

import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public final class LoggingFormatter {

	private static final String RED = "\u001B[31m";
	private static final String BLUE = "\u001B[34m";
	private static final String YELLOW = "\u001B[33m";
	private static final String RESET = "\u001B[0m";

	private static final String format = "[%1$tF %1$tT][%2$s]: %3$s %n";

	public static class ConsoleFormatter extends SimpleFormatter {
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

	public static class FileFormatter extends SimpleFormatter {
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
