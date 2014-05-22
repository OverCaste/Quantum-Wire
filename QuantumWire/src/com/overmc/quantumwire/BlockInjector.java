package com.overmc.quantumwire;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.bukkit.Bukkit;

/**
 * This class is completely unreadable if you aren't versed in reflection. I'd love to see you learn it, though, so drop me a line at overcaste0@gmail.com
 * 
 * @author overcaste
 * 
 */
public class BlockInjector {
    private final QuantumWire plugin;

    private final Class<?> registrySimpleClass;
    private final Class<?> registryMaterialsClass;
    private final Class<?> blockClass;
    private final Class<?> tileEntityClass;

    private final Field REGISTRYField;
    private final Field registrySimpleMapField;

    private final Method registryHashObjectMethod;
    private final Method registryAddMethod;

    private final VersioningClassLoader blockLoader;

    public BlockInjector(QuantumWire plugin) throws Exception {
        this.plugin = plugin;
        registrySimpleClass = Class.forName("net.minecraft.server." + getMinecraftVersion() + ".RegistrySimple");
        registryMaterialsClass = Class.forName("net.minecraft.server." + getMinecraftVersion() + ".RegistryMaterials");
        blockClass = Class.forName("net.minecraft.server." + getMinecraftVersion() + ".Block"); // Just to deal with ever-changing versions.
        tileEntityClass = Class.forName("net.minecraft.server." + getMinecraftVersion() + ".TileEntity"); // ^
        REGISTRYField = blockClass.getDeclaredField("REGISTRY");
        REGISTRYField.setAccessible(true);
        registrySimpleMapField = registrySimpleClass.getDeclaredField("c");
        registrySimpleMapField.setAccessible(true);
        registryHashObjectMethod = registryMaterialsClass.getDeclaredMethod("c", new Class[] {String.class});
        registryHashObjectMethod.setAccessible(true);
        registryAddMethod = registryMaterialsClass.getDeclaredMethod("a", new Class[] {int.class, String.class, Object.class});
        registryAddMethod.setAccessible(true);
        blockLoader = new VersioningClassLoader(getClass().getClassLoader(), getMinecraftVersion());
    }

    @SuppressWarnings("rawtypes")
    private void setBlock(String blockName, Class<?> clazz, int id) throws Exception {
        Object REGISTRY = REGISTRYField.get(null);
        Object key = registryHashObjectMethod.invoke(null, blockName);
        Map registrySimpleMap = (Map) registrySimpleMapField.get(REGISTRY);
        registrySimpleMap.remove(key); // Remove the original object from the registry
        Object value = clazz.getConstructor().newInstance();
        registryAddMethod.invoke(REGISTRY, id, blockName, value);
        plugin.getLogger().info("Injected block [" + value.getClass().getSimpleName() + "] to [" + blockName + "]"); // We've successfully injected!
    }

    @SuppressWarnings("unchecked")
    private boolean setTileEntity(String tileEntityId, Class<?> clazz) throws Exception {
        try {
            Field fidMap = tileEntityClass.getDeclaredField("i"); // Generic obfuscated method, this one is a HashMap<String, Class>
            Field fclassMap = tileEntityClass.getDeclaredField("j"); // Generic obfuscated method, this one is a HashMap<Class, String>
            fidMap.setAccessible(true); // They are private, by the way
            fclassMap.setAccessible(true); // ^
            ((Map<Object, Object>) fidMap.get(null)).put(tileEntityId, clazz); // Get the object, which is also static(.get(null) is for static fields)
            ((Map<Object, Object>) fclassMap.get(null)).put(clazz, tileEntityId); // ^
            plugin.getLogger().info("Injected tile entity [" + tileEntityId + "]"); // No exceptions, we're in!
            return true;
        } catch (Throwable t) {
            plugin.getLogger().warning("Failed to inject tile entity [" + tileEntityId + "]: " + t.getMessage());
            t.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    public boolean checkBlockInjected(String blockName) throws Exception {
        Object REGISTRY = REGISTRYField.get(null);
        Object key = registryHashObjectMethod.invoke(null, blockName);
        Map registrySimpleMap = (Map) registrySimpleMapField.get(REGISTRY);
        return (registrySimpleMap.get(key).getClass().getName().startsWith("com.overmc.quantumwire")); // On a reload, stateful information is entirely wiped.
        // Such that one instance of 'org.overmc.quantumwire.blocks.BlockSuperWire' is not an instanceof another one loaded from another classloader.
        // Thus this ugly hack.
    }

    // Like it's possible to make due without .getId, right?
    public void injectClasses( ) throws Exception {
        // Tile Entities
        if (checkBlockInjected("lapis_block")) { // Otherwise on every reload it would get re-injected, and that isn't good at all.
            plugin.getLogger().info("Blocks were already injected. No further injections taking place.");
            return;
        }
        if (setTileEntity("Super Wire", blockLoader.findClass("com.overmc.quantumwire.blocks.TileEntitySuperWire"))) { // Injecting the tile first, if it succeeded, inject the block
            // removeBlock(Material.LAPIS_BLOCK.getId()); // Remove the original block, you need to do this or you get exceptions in the constructor on the next line. (1.7 changed this)
            setBlock("lapis_block", blockLoader.findClass("com.overmc.quantumwire.blocks.BlockSuperWire"), 22); // Inject the block.
        }
        // Blocks
        // removeBlock(Material.WOOL.getId()); //1.7 changed this
        setBlock("wool", blockLoader.findClass("com.overmc.quantumwire.blocks.BlockWireThreshold"), 35);
    }

    public static String getMinecraftVersion( ) {
        String[] split = Bukkit.getServer().getClass().getName().split("\\."); // Have to escape that regex.
        if (split.length != 5) { // Something went wrong, the format should be org.bukkit.craftbukkit.v###.CraftServer
            throw new RuntimeException("Couldn't find minecraft version, are you running a compatible version of CraftBukkit?");
        }
        return split[3]; // return the v###. Unfortunately the other blocks need specific imports, but I'm working on this.
    }
}
