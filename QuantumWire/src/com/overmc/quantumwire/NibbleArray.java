package com.overmc.quantumwire;

/**
 * A fairly simple class to save memory. <br>
 * This requires only half the memory of a usual byte array, since you can only use the bottom 4 bits in metadata. (16 states)
 * 
 * @author overcaste
 */
public class NibbleArray {
	private final byte[] data;

	public NibbleArray(int size) {
		boolean odd = ((size & 1) == 1); // Check if odd, this is a little bitwise function similar to (size % 1) == 0
		data = new byte[(size / 2) + (odd ? 1 : 0)]; // If odd, dedicate an entire byte to the last section.
	}

	public NibbleArray(byte[] data) {
		this.data = data; // Just trust the person giving the data, if they put nulls so be it.
	}

	public int getNibbleAt(int index) {
		boolean even = ((index & 1) == 0); // Check if even this time.
		if (even) {
			return data[index / 2] & 0xF; // return lower 4 bits (0xF == 0b1111)
		} else {
			return (data[index / 2] >> 4) & 0xF; // return upper 4 bits ((>>4 & 0xF) == 0b11110000)
		}
	}

	public void setNibbleAt(int index, int value) {
		boolean even = ((index & 1) == 0);
		if (even) {
			data[index / 2] &= 0xF0; // reset lower 4 bits, if you didn't do this the or would add numbers.
			data[index / 2] |= value & 0xF; // set lower 4 bits
		} else {
			data[index / 2] &= 0xF; // reset higher 4 bits, if you didn't do this the or would add numbers.
			data[index / 2] |= (value & 0xF) << 4; // set higher 4 bits
		}
	}

	public byte[] getData( ) {
		return data;
	}
}
