package de.nuttercode.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.nuttercode.util.assurance.Assurance;
import de.nuttercode.util.assurance.NotNull;

/**
 * <p>
 * a <a href="https://en.wikipedia.org/wiki/Decorator_pattern">decorator</a> for
 * an {@link InputStream}. Every byte sent through the component will be written
 * to a file. this class can be used to monitor the data sent through a stream
 * without changing a lot of code.
 * </p>
 * 
 * 
 * <h2>Example</h2>
 * <p>
 * (...)<br>
 * LoggingInputStream in = new LoggingInputStream(socket.getInputStream(), new
 * File("/var/log/myCoolProgram/socket_input_stream"));<br>
 * in.read() (...)
 * </p>
 * 
 * @author Johannes B. Latzel
 *
 */
public class LoggingInputStream extends InputStream {

	/**
	 * {@link OutputStream} stream to the file
	 */
	private final FileOutputStream fout;

	/**
	 * component
	 */
	private final InputStream in;

	/**
	 * 
	 * @param in      some {@link InputStream} (component)
	 * @param logFile the file in which all data sent through in will be saved
	 * @throws FileNotFoundException if
	 *                               {@link FileOutputStream#FileOutputStream(File)}
	 *                               does
	 */
	public LoggingInputStream(@NotNull InputStream in, @NotNull File logFile) throws FileNotFoundException {
		Assurance.assureNotNull(in);
		Assurance.assureNotNull(logFile);
		this.in = in;
		this.fout = new FileOutputStream(logFile);
	}

	@Override
	public int read() throws IOException {
		int b = in.read();
		fout.write(b);
		return b;
	}

	@Override
	public int available() throws IOException {
		return in.available();
	}

	@Override
	public void close() throws IOException {
		fout.flush();
		fout.close();
	}

	@Override
	public synchronized void mark(int arg0) {
		in.mark(arg0);
	}

	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	@Override
	public int read(byte[] arg0, int arg1, int arg2) throws IOException {
		int bytesRead = in.read(arg0, arg1, arg2);
		if (bytesRead > 0)
			fout.write(arg0, arg1, bytesRead);
		return bytesRead;
	}

	@Override
	public int read(byte[] arg0) throws IOException {
		int bytesRead = in.read(arg0);
		if (bytesRead > 0)
			fout.write(arg0, 0, bytesRead);
		return bytesRead;
	}

	@Override
	public synchronized void reset() throws IOException {
		in.reset();
	}

	@Override
	public long skip(long arg0) throws IOException {
		return in.skip(arg0);
	}

}
