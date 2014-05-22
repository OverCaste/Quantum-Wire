package com.overmc.quantumwire;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class QuantumWireListener implements Listener {
    private final QuantumWire plugin;

    public QuantumWireListener(QuantumWire plugin) {
        this.plugin = plugin;
    }

    public void register( ) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH)
    public void handleBlockPlace(BlockPlaceEvent e) {
        Block b = e.getBlock();
        Thread.dumpStack();
        System.out.println("Block placed: " + e.getBlock());
        if (Material.WOOL.equals(b.getType())) {
            System.out.println("Setting wool: " + b.getData());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handlePlayerInteract(PlayerInteractEvent e) {
        System.out.println("Cancelled: " + e.isCancelled() + ", block: " + e.isBlockInHand() + ", action: " + e.getAction() + ", useInteracted: " + e.useInteractedBlock() + ", use item: "
                + e.useItemInHand());
        if (Action.RIGHT_CLICK_BLOCK.equals(e.getAction()) && (e.getItem() != null) && Material.WOOL.equals(e.getItem().getType())) {
            // e.setUseItemInHand(Result.ALLOW);
        }
        boolean result = e.useItemInHand() != Result.ALLOW;
        System.out.println("Result: " + result);
    }

    @EventHandler
    public void handleBlockCanBuild(BlockCanBuildEvent e) {
        System.out.println("CanBuild: " + e.isBuildable());
    }
}
