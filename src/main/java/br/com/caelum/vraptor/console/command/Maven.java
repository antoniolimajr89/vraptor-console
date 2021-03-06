package br.com.caelum.vraptor.console.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Maven {

	private static final Logger LOGGER = LoggerFactory.getLogger(Maven.class);
	private final File output;
	private File workingDir;
	
	public Maven() {
		this.output = new File("/tmp/output.txt");
		this.workingDir = new File(".");
	}

	public void execute(CommandLine... commands) {
		for (CommandLine c : commands)
			execute(output, c);
	}

	private void execute(File output, CommandLine command) {
		try {
			String[] commands = command.prepend("mvn");
			Process process = new ProcessBuilder(commands)
				.directory(workingDir)
				.redirectErrorStream(true).start();
			RedirectOutput redirectOutput = new RedirectOutput(output,
					process.getInputStream());
			new Thread(redirectOutput).start();
			waitTillFinish(command, process);
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void waitTillFinish(CommandLine command, Process process)
			throws InterruptedException {
		int value = process.waitFor();
		if (value != 0) {
			LOGGER.error("Unable to run " + command + ", returning " + value);
		}
	}
	
	public void useWorkingDir(File wd) {
		workingDir = wd;
		
	}

}

