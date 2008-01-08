package com.willcode4beer.service.equinox;

import static com.willcode4beer.service.equinox.EquinoxRunner.EQRunnerCommands.SHUTDOWN;
import static com.willcode4beer.service.equinox.EquinoxRunner.EQRunnerCommands.STATUS;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import com.willcode4beer.service.equinox.EquinoxRunner.EQRunnerCommands;

public class Bootstrap {

	private static final int defaultCommandPort = 9005;

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Argument required [start,stop,status]");
			System.exit(2);
		}
		String command = args[0];
		String adminPortOpt = System
				.getProperty("equinox.bootstrap.admin.port");

		int adminPort = defaultCommandPort;
		if (adminPortOpt != null) {
			adminPort = Integer.parseInt(adminPortOpt);
		}
		Bootstrap app = new Bootstrap();
		app.runCommand(command, adminPort);
	}

	private void runCommand(String command, int adminPort) {
		final EquinoxRunner runner = new EquinoxRunner(adminPort);
		if ("start".equals(command)) {
			System.out.println("Starting");
			runner.run();
		} else if ("stop".equals(command)) {
			sendCommand(SHUTDOWN, System.out, adminPort);
		} else if ("status".equals(command)) {
			sendCommand(STATUS, System.out, adminPort);
		}
	}

	private void sendCommand(EQRunnerCommands command, OutputStream response,
			int commandPort) {
		try {
			Socket s = new Socket("127.0.0.1", commandPort);
			OutputStream out = s.getOutputStream();
			PrintWriter writer = new PrintWriter(out);
			writer.println(command.name());
			writer.flush();
			InputStream in = s.getInputStream();
			for (int i = 0; (i = in.read()) >= 0;) {
				response.write((byte) i);
			}
			writer.close();
			in.close();
			out.close();
		} catch (Exception e) {
			// ok to fail, for status and shutdown
			throw new RuntimeException(e);
		}
	}
}
