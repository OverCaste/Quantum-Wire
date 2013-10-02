package com.overmc.quantumwire;

import java.lang.reflect.*;
import java.util.Map;

import org.bukkit.*;

import com.overmc.quantumwire.blocks.*;

/**
 * This class is completely unreadable if you aren't versed in reflection. I'd love to see you learn it, though, so drop me a line at overcaste0@gmail.com
 * 
 * @author overcaste
 * 
 */
public class BlockInjector {
	private final Class<?> blockClass;
	private final Class<?> tileEntityClass;

	public BlockInjector( ) throws Exception {
		blockClass = Class.forName("net.minecraft.server." + getMinecraftVersion() + ".Block"); // Just to deal with ever-changing versions.
		tileEntityClass = Class.forName("net.minecraft.server." + getMinecraftVersion() + ".TileEntity"); // ^
	}

	private void removeBlock(int id) throws Exception {
		Field byIdField = blockClass.getDeclaredField("byId"); // Get a field, reflection again.
		byIdField.setAccessible(true);
		Object byId = byIdField.get(null); // Get a static field, this one is defined as 'public static Block[] byId = new Block[256];' or something similar
		if (byId.getClass().isArray()) { // Check with reflection if it's an array, just so we can safely use Array.set
			Array.set(byId, id, null); // Set the block at that ID to our injected block.
		} else {
			throw new Exception("byId wasn't an array?!"); // This.. should never happen, but you never know.
		}
	}

	private void setBlock(String blockName, Object value) throws Exception {
		// I used to set the final field here, but it doesn't even matter.
		QuantumWire.logger.info("Injected block [" + value.getClass().getSimpleName() + "] to [" + blockName + "]"); // If the constructor thesn't throw an exception, we're in!
	}

	@SuppressWarnings("unchecked")
	private boolean setTileEntity(String tileEntityId, Class<?> clazz) throws Exception {
		try {
			Field fidMap = tileEntityClass.getDeclaredField("a"); // Generic obfuscated method, this one is a HashMap<String, Class>
			Field fclassMap = tileEntityClass.getDeclaredField("b"); // Generic obfuscated method, this one is a HashMap<Class, String>
			fidMap.setAccessible(true); // They are private, by the way
			fclassMap.setAccessible(true); // ^
			((Map<Object, Object>) fidMap.get(null)).put(tileEntityId, clazz); // Get the object, which is also static(.get(null) is for static fields)
			((Map<Object, Object>) fclassMap.get(null)).put(clazz, tileEntityId); // ^
			QuantumWire.logger.info("Injected tile entity [" + tileEntityId + "]"); // No exceptions, we're in!
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
			return (Array.get(byId, id).getClass().getName().startsWith("com.overmc.quantumwire")); // On a reload, stateful information is entirely wiped.
			// Such that one instance of 'org.overmc.quantumwire.blocks.BlockSuperWire' is not an instanceof another one loaded from another classloader.
			// Thus this ugly hack.
		} else {
			throw new Exception("byId wasn't an array?!");
		}
	}

	@SuppressWarnings("deprecation")
	// Like it's possible to make due without .getId, right?
	public void injectClasses( ) throws Exception {
		// Tile Entities
		if (checkBlockInjected(Material.LAPIS_BLOCK.getId())) { // Otherwise on every reload it would get re-injected, and that isn't good at all.
			QuantumWire.logger.info("Blocks were already injected. No further injections taking place.");
			return;
		}
		if (setTileEntity("Super Wire", TileEntitySuperWire.class)) { // Injecting the tile first, if it succeeded, inject the block
			removeBlock(Material.LAPIS_BLOCK.getId()); // Remove the original block, you need to do this or you get exceptions in the constructor on the next line.
			setBlock("LAPIS_BLOCK", new BlockSuperWire()); // Inject the block.
		}
		// Blocks
		removeBlock(Material.WOOL.getId());
		setBlock("WOOL", new BlockWireThreshold());
	}

	public static String getMinecraftVersion( ) {
		String[] split = Bukkit.getServer().getClass().getName().split("\\."); // Have to escape that regex.
		if (split.length != 5) { // Something went wrong, the format should be org.bukkit.craftbukkit.v###.CraftServer
			throw new RuntimeException("Couldn't find minecraft version, are you running a compatible version of CraftBukkit?");
		}
		return split[3]; // return the v###. Unfortunately the other blocks need specific imports, but I'm working on this.
	}
}
