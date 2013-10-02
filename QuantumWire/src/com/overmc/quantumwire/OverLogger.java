package com.overmc.quantumwire;

import java.util.logging.*;

public class OverLogger extends Logger { // Little class that I use to give a prefix to a logger.
	final String prefix;
	final Logger parent;

	public OverLogger(Logger parent, String name, String prefix) {
		super(name, null);
		this.parent = parent;
		this.prefix = "[" + prefix + "] ";
	}

	@Override
	public void log(LogRecord record) {
		if (record.getMessage() != null) {
			record.setMessage(prefix + record.getMessage());
		}
		parent.log(record);
	}
}
