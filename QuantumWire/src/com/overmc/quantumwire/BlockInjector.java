package com.overmc.quantumwire;

import java.lang.reflect.*;
import java.util.Map;

import org.bukkit.*;

import com.overmc.quantumwire.blocks.*;

public class BlockInjector {
	private final Class<?> blockClass;
	private final Class<?> tileEntityClass;

	public BlockInjector( ) throws Exception {
		blockClass = Class.forName("net.minecraft.server." + getMinecraftVersion() + ".Block");
		tileEntityClass = Class.forName("net.minecraft.server." + getMinecraftVersion() + ".TileEntity");
	}

	private void removeBlock(int id) throws Exception {
		Field byIdField = blockClass.getDeclaredField("byId");
		byIdField.setAccessible(true);
		Object byId = byIdField.get(null);
		if (byId.getClass().isArray()) {
			Array.set(byId, id, null);
		} else {
			throw new Exception("byId wasn't an array?!");
		}
	}

	private void setBlock(String blockName, Object value) throws Exception {
		// I used to set the final field here, but it doesn't even matter.
		QuantumWire.logger.info("Injected block [" + value.getClass().getSimpleName() + "] to [" + blockName + "]");
	}

	@SuppressWarnings("unchecked")
	private boolean setTileEntity(String tileEntityId, Class<?> clazz) throws Exception {
		try {
			Field fidMap = tileEntityClass.getDeclaredField("a");
			Field fclassMap = tileEntityClass.getDeclaredField("b");
			fidMap.setAccessible(true);
			fclassMap.setAccessible(true);
			((Map<Object, Object>) fidMap.get(null)).put(tileEntityId, clazz);
			((Map<Object, Object>) fclassMap.get(null)).put(clazz, tileEntityId);
			QuantumWire.logger.info("Injected tile entity [" + tileEntityId + "]");
			return true;
		} catch (Throwable t) {
			QuantumWire.logger.warning("Failed to inject tile entity [" + tileEntityId + "]: " + t.getMessage());
			t.printStackTrace();
		}
		return false;
	}

	public boolean checkBlockInjected(int id) throws Exception {
		Field byIdField = blockClass.getDeclaredField("byId");
		byIdField.setAccessible(true);
		Object byId = byIdField.get(null);
		if (byId.getClass().isArray()) {
			return (Array.get(byId, id).getClass().getName().startsWith("com.overmc.quantumwire"));
		} else {
			throw new Exception("byId wasn't an array?!");
		}
	}

	@SuppressWarnings("deprecation")
	public void injectClasses( ) throws Exception {
		// Tile Entities
		if (checkBlockInjected(Material.LAPIS_BLOCK.getId())) {
			QuantumWire.logger.info("Blocks were already injected. No further injections taking place.");
			return;
		}
		if (setTileEntity("Super Wire", TileEntitySuperWire.class)) {
			removeBlock(Material.LAPIS_BLOCK.getId());
			setBlock("LAPIS_BLOCK", new BlockSuperWire());
		}
		// Blocks
		removeBlock(Material.WOOL.getId());
		setBlock("WOOL", new BlockWireThreshold());
	}

	public static String getMinecraftVersion( ) {
		String[] split = Bukkit.getServer().getClass().getName().split("\\.");
		if (split.length != 5) {
			throw new RuntimeException("Couldn't find minecraft version, are you running a compatible version of CraftBukkit?");
		}
		return split[3];
	}
}
