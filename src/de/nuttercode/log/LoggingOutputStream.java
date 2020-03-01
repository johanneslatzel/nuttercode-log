package de.nuttercode.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.nuttercode.util.assurance.Assurance;
import de.nuttercode.util.assurance.NotNull;

/**
 * <p>
 * a <a href="https://en.wikipedia.org/wiki/Decorator_pattern">decorator</a> for
 * an {@link OutputStream}. Every byte sent through the component will be
 * written to a file. this class can be used to monitor the data sent through a
 * stream without changing a lot of code.
 * </p>
 * 
 * 
 * <h2>Example</h2>
 * <p>
 * (...)<br>
 * LoggingOutputStream out = new LoggingOutputStream(socket.getOutputStream(),
 * new File("/var/log/myCoolProgram/socket_output_stream"));<br>
 * out.write(buffer) (...)
 * </p>
 * 
 * @author Johannes B. Latzel
 *
 */
public class LoggingOutputStream extends OutputStream {

	/**
	 * {@link OutputStream} stream to the file
	 */
	private final FileOutputStream fout;

	/**
	 * component
	 */
	private final OutputStream out;

	/**
	 * 
	 * @param out     some {@link OutputStream} (component)
	 * @param logFile the file in which all data sent through in will be saved
	 * @throws FileNotFoundException if
	 *                               {@link FileOutputStream#FileOutputStream(File)}
	 *                               does
	 */
	public LoggingOutputStream(@NotNull OutputStream out, @NotNull File logFile) throws FileNotFoundException {
		Assurance.assureNotNull(out);
		Assurance.assureNotNull(logFile);
		this.out = out;
		this.fout = new FileOutputStream(logFile);
	}

	@Override
	public void flush() throws IOException {
		fout.flush();
		out.flush();
	}

	@Override
	public void write(byte[] data, int offset, int length) throws IOException {
		fout.write(data, offset, length);
		out.write(data, offset, length);
	}

	@Override
	public void write(byte[] data) throws IOException {
		fout.write(data);
		out.write(data);
	}

	@Override
	public void write(int arg0) throws IOException {
		fout.write(arg0);
		out.write(arg0);
	}

	@Override
	public void close() throws IOException {
		fout.flush();
		fout.close();
	}

}
