package com.overmc.quantumwire;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class QuantumWireListener implements Listener {
    private final QuantumWire plugin;
    private final HashMap<UUID, Short> playerHeldWoolIds = new HashMap<UUID, Short>();

    public QuantumWireListener(QuantumWire plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void handleBlockPlace(BlockPlaceEvent e) {
        Block b = e.getBlock();
        if (Material.WOOL.equals(b.getType())) {
            b.setData(playerHeldWoolIds.get(e.getPlayer().getUniqueId()).byteValue());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handlePlayerInteract(PlayerInteractEvent e) {
        if (Action.RIGHT_CLICK_BLOCK.equals(e.getAction()) && (e.getItem() != null) && Material.WOOL.equals(e.getItem().getType())) {
            final UUID uuid = e.getPlayer().getUniqueId();
            playerHeldWoolIds.put(uuid, e.getItem().getDurability());
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run( ) {
                    playerHeldWoolIds.remove(uuid);
                }
            }, 5L);
        }
    }
}
