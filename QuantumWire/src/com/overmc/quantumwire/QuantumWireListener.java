package com.overmc.quantumwire;

import net.minecraft.server.v1_7_R3.Item;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
        if (!result) {
            net.minecraft.server.v1_7_R3.ItemStack i = new net.minecraft.server.v1_7_R3.ItemStack(Item.d(e.getItem().getTypeId()), e.getItem().getAmount(), e.getItem().getDurability());
            if (i.placeItem(((CraftPlayer) e.getPlayer()).getHandle(), ((CraftWorld) e.getPlayer().getWorld()).getHandle(), e.getClickedBlock().getX(), e.getClickedBlock().getY(), e.getClickedBlock()
                    .getZ(), CraftBlock.blockFaceToNotch(e.getBlockFace()), 0f, 0f, 0f)) {
                System.out.println("Placed item!");
            } else {
                System.out.println("No place item. :(");
            }
        }
    }
}
