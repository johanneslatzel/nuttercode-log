package de.nuttercode.log;
import java.io.IOException;

/**
 * thrown by {@link} Log if some {@link IOException} has been thrown
 * 
 * @author johannes.latzel
 *
 */
public class LogException extends RuntimeException {

	private static final long serialVersionUID = 4472030006730414907L;

	public LogException(String message) {
		super(message);
	}

	public LogException(Exception e) {
		super(e);
	}

	public LogException(String message, Exception e) {
		super(message, e);
	}

}
