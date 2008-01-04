package com.willcode4beer.service.equinox;

public class Bootstrap {

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Argument required [start,stop,status]");
			System.exit(2);
		}

		String adminPortOpt = System
				.getProperty("equinox.bootstrap.admin.port");
		final EquinoxRunner runner;
		if (adminPortOpt != null) {
			runner = new EquinoxRunner(Integer.parseInt(adminPortOpt));
		} else {
			runner = new EquinoxRunner();
		}
		String arg = args[0];
		if ("start".equals(arg)) {
			try {
				runner.start();
			} catch (Exception e) {
				System.exit(2);
			} finally {
				try {
					runner.shutDown();
				} catch (Exception e) {
					System.exit(2);
				}
			}
		} else if ("stop".equals(arg)) {
			runner.sendCommand(EquinoxRunner.commands.SHUTDOWN, System.out);
		} else if ("status".equals(arg)) {
			runner.sendCommand(EquinoxRunner.commands.STATUS, System.out);
		}
	}

}
