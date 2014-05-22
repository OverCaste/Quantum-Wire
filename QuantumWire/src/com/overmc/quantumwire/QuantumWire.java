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
    private static boolean error = false;
    private BlockInjector injector;
    private boolean stopOnCrash = true;

    private QuantumWireListener listener;

    @Override
    public void onEnable( ) {
        try {
            getLogger().info("Injecting blocks into the server data.");
            injector = new BlockInjector(this);
            injector.injectClasses();
            initConfig();
            initListener();
            getLogger().info(getDescription().getName() + " v" + getDescription().getVersion() + " enabled!");
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

    private void initListener( ) {
        listener = new QuantumWireListener(this);
        listener.register();
    }

    @Override
    public void onDisable( ) {
        if (!error) {
            getLogger().info(getDescription().getName() + " disabled.");
        }
    }

}
