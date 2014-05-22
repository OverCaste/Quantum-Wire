package com.overmc.quantumwire;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import user.theovercaste.overdecompiler.constantpool.ConstantPoolEntries;
import user.theovercaste.overdecompiler.constantpool.ConstantPoolEntry;
import user.theovercaste.overdecompiler.constantpool.ConstantPoolEntryUtf8;

import com.google.common.collect.ImmutableSet;

public class VersioningClassLoader extends ClassLoader {
    protected final ImmutableSet<String> validFiles = ImmutableSet
            .of("com.overmc.quantumwire.blocks.BlockSuperWire", "com.overmc.quantumwire.blocks.BlockWireThreshold", "com.overmc.quantumwire.blocks.TileEntitySuperWire");

    protected final String replace;

    public VersioningClassLoader(ClassLoader parent, String replace) {
        super(parent);
        this.replace = replace;
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] b = getClassData(name);
        if (b == null) {
            return getParent().loadClass(name);
        }
        return defineClass(name, b, 0, b.length);
    }

    private byte[] getClassData(String name) {
        if (!validFiles.contains(name)) {
            return null;
        }
        ByteArrayOutputStream ret = new ByteArrayOutputStream();

        try (DataInputStream din = new DataInputStream(getClass().getResourceAsStream("/" + name.replace('.', '/') + ".class")); DataOutputStream dout = new DataOutputStream(ret)) {
            dout.writeInt(din.readInt()); // Ignore magic.
            dout.writeShort(din.readUnsignedShort()); // Ignore minor
            dout.writeShort(din.readUnsignedShort()); // Ignore major
            int constantPoolCount = din.readUnsignedShort();
            ConstantPoolEntry[] constantPool = new ConstantPoolEntry[constantPoolCount];
            for (int i = 1; i < constantPoolCount; i++) {
                constantPool[i] = ConstantPoolEntries.readEntry(din);
                if (constantPool[i] == null) {
                    System.out.println("Invalid constant pool entry found: " + i);
                    constantPoolCount--;
                }
            }
            dout.writeShort(constantPoolCount);
            for (ConstantPoolEntry e : constantPool) {
                if (e != null) {
                    ConstantPoolEntry written = e;
                    if (e.getTag() == ConstantPoolEntries.UTF8_TAG) {
                        written = new ConstantPoolEntryUtf8(e.getTag(), ((ConstantPoolEntryUtf8) e).getValue().replace("net/minecraft/server/v1_7_R3/", replace)); // Replace all instances of 'v1_7_R3' to our value.
                    }
                    written.write(dout);
                }
            }

            byte[] buffer = new byte[2048]; // We only care about the constant pool, everything else can be written.
            int bytesRead;
            while ((bytesRead = din.read(buffer)) != -1) {
                dout.write(buffer, 0, bytesRead);
            }
            return ret.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
