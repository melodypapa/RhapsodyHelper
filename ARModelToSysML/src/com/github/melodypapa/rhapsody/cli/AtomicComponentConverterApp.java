package com.github.melodypapa.rhapsody.cli;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.github.melodypapa.rhapsody.plugin.RhapsodyLoggerHandler;
import com.github.melodypapa.rhapsody.plugin.controller.ARModelToSysMLController;
import com.github.melodypapa.rhapsody.plugin.controller.AbstractController;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.RhapsodyAppServer;

public class AtomicComponentConverterApp {

	public static void main(String[] args) {
		System.setProperty("java.util.logging.SimpleFormatter.format",
	              "[%1$tF %1$tT] [%4$s] %5$s %n");
		
		IRPApplication app = RhapsodyAppServer.getActiveRhapsodyApplication();
		
		if (app == null){
			System.err.println("Please run the rhapsody first.");
			return;
		}
		
		Logger logger = LogManager.getLogManager().getLogger("");
		Handler handler = new RhapsodyLoggerHandler(app);
		handler.setLevel(Level.INFO);
		logger.addHandler(handler);
		
		IRPModelElement element = app.getSelectedElement();
		if (element instanceof IRPPackage){
			
			AbstractController controller = new ARModelToSysMLController(app);
			controller.execute(element);
		}
	}

}
