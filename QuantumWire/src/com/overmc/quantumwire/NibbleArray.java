package com.overmc.quantumwire;

public class NibbleArray {
	private final byte[] data;

	public NibbleArray(int size) {
		boolean odd = ((size & 1) == 1);
		data = new byte[(size / 2) + (odd ? 1 : 0)];
	}

	public NibbleArray(byte[] data) {
		this.data = data;
	}

	public int getNibbleAt(int index) {
		boolean even = ((index & 1) == 0);
		if (even) {
			return data[index / 2] & 0xF;
		} else {
			return (data[index / 2] >> 4) & 0xF;
		}
	}

	public void setNibbleAt(int index, int value) {
		boolean even = ((index & 1) == 0);
		if (even) {
			data[index / 2] &= 0xF0; // reset lower 4 bits
			data[index / 2] |= value & 0xF; // set lower 4 bits
		} else {
			data[index / 2] &= 0xF; // reset higher 4 bits
			data[index / 2] |= (value & 0xF) << 4; // set higher 4 bits
		}
	}

	public byte[] getData( ) {
		return data;
	}
}
