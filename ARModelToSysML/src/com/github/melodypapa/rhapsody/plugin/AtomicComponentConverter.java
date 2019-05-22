package com.github.melodypapa.rhapsody.plugin;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.github.melodypapa.rhapsody.plugin.controller.ARModelToSysMLController;
import com.github.melodypapa.rhapsody.plugin.controller.AbstractController;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.RPUserPlugin;
import com.telelogic.rhapsody.core.RhapsodyRuntimeException;

public class AtomicComponentConverter extends RPUserPlugin {

	private static final String VERSION = "0.0.6";

	private IRPApplication application = null;
	private static Logger logger = LogManager.getLogManager().getLogger("");
	private Handler handler = null;

	@Override
	public void RhpPluginInit(IRPApplication rpyApplication) {
		application = rpyApplication;

		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$s] %5$s %n");

		handler = new RhapsodyLoggerHandler(application);
		handler.setLevel(Level.WARNING);
		logger.addHandler(handler);
		logger.info(String.format("Atomic Component Converter plug-in (Ver: %s) was loaded successful", VERSION));
	}

	@Override
	public void OnMenuItemSelect(String menuItem) {
		try {
			IRPModelElement selectedElement = application.getSelectedElement();
			AbstractController controller;

			if (menuItem.equals("AUTOSAR tools\\Atomic Component Conversion")) {
				if (selectedElement instanceof IRPPackage) {
					IRPPackage rootPackage = (IRPPackage) selectedElement;
					controller = new ARModelToSysMLController(application);
					controller.execute(rootPackage);
				}
				logger.severe("Atomic Component Conversion is done.");
			}
			else {
				logger.severe("MenuSelect:<" + "No Action" + ">");
			}
		}
		catch (RhapsodyRuntimeException ex) {
			logger.severe(ex.getMessage());
		}
	}

	@Override
	public void OnTrigger(String trigger) {
		logger.info("Trigger:" + trigger + "");
	}

	@Override
	public boolean RhpPluginCleanup() {
		if (this.handler != null) {
			logger.removeHandler(this.handler);
			this.handler = null;
		}
		return true;
	}

	@Override
	public void RhpPluginFinalCleanup() {
	}

	@Override
	public void RhpPluginInvokeItem() {
	}
}
