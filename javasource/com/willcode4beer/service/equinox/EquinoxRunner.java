package com.willcode4beer.service.equinox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.adaptor.EclipseStarter;

public class EquinoxRunner {

	private volatile boolean running;
	private final int adminPort;

	enum commands {
		SHUTDOWN, STATUS
	};

	public EquinoxRunner(int port) {
		this.adminPort = port;
	}

	public EquinoxRunner() {
		this.adminPort = 9005; // default port
	}

	public void start() throws Exception {
		startEquinox();
		this.running = true;
		startAdmin();
	}

	public void shutDown() throws Exception {
		this.running = false;
		EclipseStarter.shutdown();
	}

	public boolean status() {
		return EclipseStarter.isRunning();
	}

	private void startEquinox() throws Exception {
		Map<String, String> props = new HashMap<String, String>();
		props.put("eclipse.ignoreApp", "true");
		props.put("osgi.noShutdown", "true");
		EclipseStarter.setInitialProperties(props);
		EclipseStarter.startup(new String[] {}, null);
	}

	private void startAdmin() throws Exception {
		ServerSocket ss = new ServerSocket(adminPort);
		while (running) {
			Socket sock = ss.accept();
			InputStream in = sock.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String command = reader.readLine();
			OutputStream out = sock.getOutputStream();
			if (commands.SHUTDOWN.name().equals(command)) {
				sendResponse(out, "shutting down");
				shutDown();
				sendResponse(out, "stopped");
			} else if (commands.STATUS.name().equals(command)) {
				String response = (status() ? "running" : "not running");
				sendResponse(out, response);
			}
			reader.close();
			in.close();
			out.close();
		}
	}

	private void sendResponse(OutputStream out, String response)
			throws IOException {
		PrintWriter writer = new PrintWriter(out);
		writer.println(response);
		writer.flush();
	}

	public void sendCommand(commands command, OutputStream responseHandler) {
		try {
			Socket s = new Socket("localhost", adminPort);
			OutputStream out = s.getOutputStream();
			PrintWriter writer = new PrintWriter(out);
			writer.println(command.name());
			writer.flush();
			InputStream in = s.getInputStream();
			for (int i = 0; (i = in.read()) >= 0;) {
				responseHandler.write((byte) i);
			}
			writer.close();
			in.close();
			out.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
