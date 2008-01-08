package com.willcode4beer.service.equinox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.eclipse.core.runtime.adaptor.EclipseStarter;

public class EquinoxRunner {

	private volatile boolean running;
	private final int adminPort;

	enum EQRunnerCommands {
		SHUTDOWN, STATUS
	};

	public EquinoxRunner(int port) {
		this.adminPort = port;
	}

	public void shutDown() throws Exception {
		this.running = false;
		EclipseStarter.shutdown();
	}

	public boolean status() {
		return EclipseStarter.isRunning();
	}

	private void startAdmin() throws Exception {
		ServerSocket ss = new ServerSocket(adminPort);
		while (running) {
			Socket sock = ss.accept();
			// validate IP here
			if (sock.getInetAddress().getHostAddress().equals("127.0.0.1")) {
				InputStream in = sock.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				String command = reader.readLine();
				OutputStream out = sock.getOutputStream();

				if (EQRunnerCommands.SHUTDOWN.name().equals(command)) {
					sendResponse(out, "shutting down");
					shutDown();
					sendResponse(out, "stopped");
				} else if (EQRunnerCommands.STATUS.name().equals(command)) {
					String response = (status() ? "running" : "not running");
					sendResponse(out, response);
				}
				reader.close();
				in.close();
				out.close();
			}
			sock.close();
		}
	}

	private void sendResponse(OutputStream out, String response)
			throws IOException {
		PrintWriter writer = new PrintWriter(out);
		writer.println(response);
		writer.flush();
	}

	public void run() {
		try {
			EclipseStarter.startup(new String[] {}, null);
			this.running = true;
			startAdmin();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				shutDown();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
