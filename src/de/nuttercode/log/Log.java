package de.nuttercode.log;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;

import de.nuttercode.util.assurance.Assurance;
import de.nuttercode.util.assurance.NotEmpty;
import de.nuttercode.util.assurance.NotNull;

/**
 * thread-safe log
 * 
 * @author Johannes B. Latzel
 *
 */
public class Log implements Closeable {

	private final static String LOG_FILE_SUFFIX = ".log";

	/**
	 * folder in which all log files will be stored
	 */
	private final File directory;

	/**
	 * the {@link File} the log points to
	 */
	private File logFile;

	/**
	 * object all operations will be synchronized with
	 */
	private final Object fileLock;

	/**
	 * writer pointing to the {@link #logFile}
	 */
	private BufferedWriter writer;

	/**
	 * points to current logFile after {@link #setupLogFile()} has been called
	 */
	private final String logName;

	/**
	 * 
	 * @param directory
	 * @throws NullPointerException     if folder is null
	 * @throws IllegalArgumentException if folder is not a folder
	 * @throws SecurityException        if {@link File#isDirectory()} does
	 * @throws LogException             if an {@link IOException} has been thrown
	 */
	public Log(@NotNull File directory, @NotEmpty String logName) {
		Assurance.assureNotNull(directory);
		if (!directory.isDirectory())
			throw new IllegalArgumentException("argument directory is not a directory");
		Assurance.assureNotEmpty(logName);
		this.directory = directory;
		this.logName = logName;
		fileLock = new Object();
		setupLogFile();
	}

	/**
	 * sets up {@link #logFile}
	 * 
	 * @throws LogException if an {@link IOException} has been thrown
	 */
	private void setupLogFile() {
		logFile = Paths.get(directory.getAbsolutePath(), logName + LOG_FILE_SUFFIX).toFile();
		try {
			writer = new BufferedWriter(new FileWriter(logFile, true));
		} catch (IOException e) {
			throw new LogException(e);
		}
	}

	/**
	 * tries to log the message with supplied logLevel
	 * 
	 * @param message
	 * @param logLevel
	 * @throws LogException if an {@link IOException} has been thrown
	 */
	private void log(String message, LogLevel logLevel) {
		synchronized (fileLock) {
			try {
				writer.write(Instant.now().toString());
				writer.write(" - ");
				writer.write(logLevel.toString());
				if (message != null && !message.isEmpty()) {
					writer.write(": ");
					writer.write(message);
				}
				writer.write('\n');
				writer.flush();
			} catch (IOException e) {
				throw new LogException("could not log message", e);
			}
		}
	}

	/**
	 * renames the current log file "[name].log" to "[name]_[timestamp in
	 * milliseconds].log" and creates a new "[name].log" file
	 * 
	 * @throws SecurityException if {@link File#renameTo(File)} does
	 * @throws LogException      if an {@link IOException} has been thrown
	 */
	public void snap() {
		synchronized (fileLock) {
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				throw new LogException(e);
			}
			String absp = logFile.getAbsolutePath();
			logFile.renameTo(
					new File(absp.substring(0, absp.length() - 4) + System.currentTimeMillis() + LOG_FILE_SUFFIX));
			setupLogFile();
		}

	}

	/**
	 * logs the message with {@link LogLevel#INFO}
	 * 
	 * @param message
	 * @throws LogException if an {@link IOException} has been thrown
	 */
	public void logInfo(String message) {
		log(message, LogLevel.INFO);
	}

	/**
	 * logs the message with {@link LogLevel#WARNING}
	 * 
	 * @param message
	 * @throws LogException if an {@link IOException} has been thrown
	 */
	public void logWarning(String message) {
		log(message, LogLevel.WARNING);
	}

	/**
	 * logs the message with {@link LogLevel#ERROR}
	 * 
	 * @param message
	 * @throws LogException if an {@link IOException} has been thrown
	 */
	public void logError(String message) {
		log(message, LogLevel.ERROR);
	}

	@Override
	public void close() throws IOException {
		synchronized (fileLock) {
			writer.flush();
			writer.close();
		}
	}

}
