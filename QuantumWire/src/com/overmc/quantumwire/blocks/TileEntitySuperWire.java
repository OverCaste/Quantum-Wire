package com.overmc.quantumwire.blocks;

import net.minecraft.server.v1_6_R3.*;

import org.bukkit.DyeColor;

import com.overmc.quantumwire.NibbleArray;

public class TileEntitySuperWire extends TileEntity {
	private int strengthMap = 0;
	private NibbleArray nibble;

	public TileEntitySuperWire( ) {
		super();
		nibble = new NibbleArray(16);
		for (int i = 0; i < 16; i++) {
			nibble.setNibbleAt(i, 6);
		}
	}

	@Override
	public void a(NBTTagCompound tag) { // read
		super.a(tag);
		nibble = new NibbleArray(tag.getByteArray("data"));
	}

	@Override
	public void b(NBTTagCompound tag) { // write
		super.b(tag);
		tag.setByteArray("data", nibble.getData());
	}

	@SuppressWarnings("deprecation")
	public int getPowerDirection(DyeColor index) {
		return getPowerDirection(index.getWoolData());
	}

	public int getPowerDirection(int index) {
		return nibble.getNibbleAt(index);
	}

	@SuppressWarnings("deprecation")
	public void setPowerDirection(DyeColor index, int value) {
		setPowerDirection(index.getWoolData(), value);
	}

	public void setPowerDirection(int index, int value) {
		nibble.setNibbleAt(index, value);
		if (value >= 6) {
			strengthMap &= ~(1 << index);
		} else {
			strengthMap |= (1 << index);
		}
	}

	public int getStrengthMap( ) {
		return strengthMap;
	}

	public boolean isColorPowered(int color) {
		return getPowerDirection(color) <= 5;
	}
}
