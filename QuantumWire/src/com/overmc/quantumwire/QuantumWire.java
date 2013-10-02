package com.overmc.quantumwire;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class is quite simple. Just a generic plugin with a bit of configuration.
 * 
 * @author overcaste
 * @see #BlockInjector
 * @see com.overmc.quantumwire.blocks.BlockSuperWire
 * @see com.overmc.quantumwire.blocks.TileEntitySuperWire
 */
public class QuantumWire extends JavaPlugin {
	public static final OverLogger logger = new OverLogger(Bukkit.getLogger(), "QuantumWire", "Quantum Wire");
	private static boolean error = false;
	private BlockInjector injector;
	private boolean stopOnCrash = true;

	@Override
	public void onEnable( ) {
		try {
			logger.info("Injecting blocks into the server data.");
			injector = new BlockInjector();
			injector.injectClasses();
			initConfig();
			logger.info(getDescription().getName() + " v" + getDescription().getVersion() + " enabled!");
		} catch (Throwable t) {
			error = true;
			t.printStackTrace();
			if (stopOnCrash) {
				Bukkit.shutdown();
			} else {
				setEnabled(false);
			}
		}
	}

	private void initConfig( ) {
		getConfig().options().copyDefaults(true);
		getConfig().addDefault("stop-on-crash", true);
		saveConfig();
		stopOnCrash = getConfig().getBoolean("stop-on-crash");
	}

	@Override
	public void onDisable( ) {
		if (!error) {
			logger.info(getDescription().getName() + " disabled.");
		}
	}

}
