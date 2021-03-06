package com.perfma.xpocket.plugin.vjtools.vjtop;


import com.perfma.xpocket.plugin.vjtools.vjtop.VMDetailView.ContentMode;
import com.perfma.xpocket.plugin.vjtools.vjtop.VMDetailView.OutputFormat;
import com.perfma.xpocket.plugin.vjtools.vjtop.VMDetailView.ThreadInfoMode;
import com.perfma.xpocket.plugin.vjtools.vjtop.VMInfo.VMInfoState;
import com.perfma.xpocket.plugin.vjtools.vjtop.util.Formats;
import com.perfma.xpocket.plugin.vjtools.vjtop.util.OptionAdvanceParser;
import com.perfma.xpocket.plugin.vjtools.vjtop.util.Utils;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.*;

public class VJTopAdapter {

	public static final String VERSION = "1.0.9";

	public VMDetailView view;
	private Thread mainThread;

	private Integer interval;
	private int maxIterations = -1;

	private volatile boolean needMoreInput = false;
	private long sleepStartTime;


	public static void invoke(String[] args) {
		try {
			// 1. create option parser
			OptionParser parser = OptionAdvanceParser.createOptionParser();
			OptionSet optionSet = parser.parse(args);

			if (optionSet.has("help")) {
				printHelper(parser);
				System.exit(0);
			}

			// 2. create vminfo
			String pid = OptionAdvanceParser.parsePid(parser, optionSet);

			String jmxHostAndPort = null;
			if (optionSet.hasArgument("jmxurl")) {
				jmxHostAndPort = (String) optionSet.valueOf("jmxurl");
			}

			VMInfo vminfo = VMInfo.processNewVM(pid, jmxHostAndPort);
			if (vminfo.state != VMInfoState.ATTACHED) {
				System.out
						.println("\n" + Formats.red("ERROR: Could not attach to process, see the solution in README"));
				return;
			}

			// 3. create view
			ThreadInfoMode threadInfoMode = OptionAdvanceParser.parseThreadInfoMode(optionSet);
			OutputFormat format = OptionAdvanceParser.parseOutputFormat(optionSet);
			ContentMode contentMode = OptionAdvanceParser.parseContentMode(optionSet);

			Integer width = null;
			if (optionSet.hasArgument("width")) {
				width = (Integer) optionSet.valueOf("width");
			}

			Integer interval = OptionAdvanceParser.parseInterval(optionSet);

			VMDetailView view = new VMDetailView(vminfo, format, contentMode, threadInfoMode, width, interval);

			if (optionSet.hasArgument("limit")) {
				Integer limit = (Integer) optionSet.valueOf("limit");
				view.threadLimit = limit;
			}

			if (optionSet.hasArgument("filter")) {
				String filter = (String) optionSet.valueOf("filter");
				view.threadNameFilter = filter;
			}

			// 4. create main application
			VJTopAdapter app = new VJTopAdapter();
			app.mainThread = Thread.currentThread();
			app.view = view;
			app.updateInterval(interval);

			if (optionSet.hasArgument("n")) {
				Integer iterations = (Integer) optionSet.valueOf("n");
				app.maxIterations = iterations;
			}

			// 5. console/cleanConsole mode start thread to get user input
			if (format != OutputFormat.text) {
				InteractiveTask task = new InteractiveTask(app);
				// ?????????????????????????????????????????????????????????
				if (task.inputEnabled()) {
					view.displayCommandHints = true;
					if (app.maxIterations == -1) {
						Thread interactiveThread = new Thread(task, "InteractiveThread");
						interactiveThread.setDaemon(true);
						interactiveThread.start();
					}
				} else {
					// ?????????????????????????????????????????????????????????ansi??????????????????
					format = OutputFormat.cleanConsole;
				}
			}

			// 6. cleanConsole/text mode, ??????ansi???
			if (!format.ansi) {
				Formats.disableAnsi();
				if (format == OutputFormat.cleanConsole) {
					Formats.setCleanClearTerminal();
				} else {
					Formats.setTextClearTerminal();
				}
			}

			// 7. run app
			app.run(view);
		} catch (Exception e) {
			e.printStackTrace(System.out);
			System.out.flush();
		}
	}

	private void run(VMDetailView view) throws Exception {
		try {
			// System.out ??????Buffered???????????????System.out.flush??????
			System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(FileDescriptor.out)), false));

			int iterations = 0;
			while (!view.shouldExit()) {
				waitForInput();
				view.printView();
				if (view.shouldExit()) {
					break;
				}

				System.out.flush();

				if (maxIterations > 0 && iterations >= maxIterations) {
					break;
				}

				// ????????????????????????3???
				int sleepSeconds = (iterations == 0) ? Math.min(3, interval) : interval;

				iterations++;
				sleepStartTime = System.currentTimeMillis();
				Utils.sleep(sleepSeconds * 1000L);
			}
			System.out.println("");
			System.out.flush();
		} catch (NoClassDefFoundError e) {
			e.printStackTrace(System.out);
			System.out.println(Formats.red("ERROR: Some JDK classes cannot be found."));
			System.out.println("       Please check if the JAVA_HOME environment variable has been set to a JDK path.");
			System.out.println("");
			System.out.flush();
		}
	}

	public static void printHelper(OptionParser parser) {
		try {
			System.out.println("vjtop " + VERSION + " - java monitoring for the command-line");
			System.out.println("Usage: vjtop.sh [options...] <PID>");
			System.out.println("");
			parser.printHelpOn(System.out);
		} catch (IOException ignored) {

		}
	}

	public void exit() {
		view.shoulExit();
		mainThread.interrupt();
	}

	public void interruptSleep() {
		mainThread.interrupt();
	}

	public void preventFlush() {
		needMoreInput = true;
	}

	public void continueFlush() {
		needMoreInput = false;
	}

	private void waitForInput() {
		while (needMoreInput) {
			Utils.sleep(1000);
		}
	}

	public int nextFlushTime() {
		return Math.max(0, interval - (int) ((System.currentTimeMillis() - sleepStartTime) / 1000));
	}

	public void updateInterval(int interval) {
		this.interval = interval;
		view.interval = interval;
	}

	public int getInterval() {
		return interval;
	}
}
