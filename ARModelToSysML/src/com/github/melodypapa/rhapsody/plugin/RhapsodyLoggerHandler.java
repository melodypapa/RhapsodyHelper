package com.github.melodypapa.rhapsody.plugin;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import com.telelogic.rhapsody.core.IRPApplication;

public final class RhapsodyLoggerHandler extends Handler {
	
	private IRPApplication app;
	private static final String LOG = "Log";
	
	public RhapsodyLoggerHandler(IRPApplication app) {
		super();
		
		this.app = app;
		
		setFormatter(new SimpleFormatter());
	}
	
	@Override
	public void close() throws SecurityException {
		
	}

	@Override
	public void flush() {
		
	}

	@Override
	public void publish(LogRecord record) {
		if (isLoggable(record)){
			app.writeToOutputWindow(LOG, getFormatter().format(record));
			//System.out.println(getFormatter().format(record) + "\n");
		}
	}
}
