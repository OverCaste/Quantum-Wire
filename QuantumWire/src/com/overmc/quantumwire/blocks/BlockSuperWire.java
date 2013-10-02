package com.overmc.quantumwire.blocks;

import java.util.Random;

import net.minecraft.server.v1_6_R3.*;

public class BlockSuperWire extends Block implements IContainer {
	public BlockSuperWire( ) {
		super(22, Material.STONE); // Id, Material
		this.c(3.0F); // These are obfuscated methods from Block.class, let's see, this one is hardness.
		this.b(5.0F); // This one is blast resistance.
		this.a(k); // This one is step sound
		this.c("blockLapis"); // Localized name
		this.a(CreativeModeTab.b); // Creative tab
		this.d("lapis_block"); // Internal name
		isTileEntity = true; // Makes the system know there's a tile
	}

	@Override
	public void onPlace(World world, int x, int y, int z) {
		for (int color = 0; color < 16; color++) {
			handleWireChange(world, x, y, z, 6, color, true); // Update all wire colors
		}
	}

	@Override
	public void remove(World world, int x, int y, int z, int i, int j) {
		super.remove(world, x, y, z, i, j);
		for (int color = 0; color < 16; color++) {
			setUnpowered(world, x, y, z, color); // Just to update the neighbors.
		}
		world.s(x, y, z); // Remove tile entity
	}

	@Override
	public void doPhysics(World world, int x, int y, int z, int l) {
		// do nothing
	}

	@Override
	public void a(World world, int i, int j, int k, Random random) {
		// do nothing
	}

	public void handleWireChange(World world, int x, int y, int z, int fromDir, int color, boolean risingEdge) {
		TileEntitySuperWire tile = getSuperTile(world, x, y, z);
		if (tile != null) {
			int powerDirection = getPowerDirection(world, x, y, z, color);
			if (risingEdge) { // something turned on
				if (powerDirection >= 6) { // block is currently off and something turned on
					for (int dir = 0; dir < 6; dir++) {
						if (isPowered(world, x + Facing.b[dir], y + Facing.c[dir], z + Facing.d[dir], Facing.OPPOSITE_FACING[dir], color, true)) {
							setPoweredBy(world, x, y, z, color, dir);
							return;
						}
					}
				} // otherwise color is on, and something turned on, do nothing
			} else { // falling edge
				if (isPowered(tile, color)) { // we were powered and something unpowered near us.
					if ((fromDir == 6) || (powerDirection == Facing.OPPOSITE_FACING[fromDir])) { // the block powering us turned off
						for (int dir = 0; dir < 6; dir++) {
							if (isPowered(world, x + Facing.b[dir], y + Facing.c[dir], z + Facing.d[dir], Facing.OPPOSITE_FACING[dir], color, false)) {
								setPoweredBy(world, x, y, z, color, dir); // we were powered, something unpowered, but we found a new block
								return;
							}
							// we were powered, and there is nothing else powering us
							setUnpowered(world, x, y, z, color);
						}
					}
				}
			}
		}
	}

	private boolean isPowered(World world, int x, int y, int z, int direction, int color, boolean allowWires) {
		Block b = Block.byId[world.getTypeId(x, y, z)];
		if (b instanceof BlockSuperWire) {
			if (!allowWires) {
				return false;
			}
			TileEntitySuperWire tile = getSuperTile(world, x, y, z);
			if (tile == null) { // In case that block was placed without the plugin knowing.
				return false;
			}
			return tile.isColorPowered(color) && (tile.getPowerDirection(color) != direction); // is tile powered and not powered by us?
		}
		if (b instanceof BlockWireThreshold) {
			return ((BlockWireThreshold) b).isPowered(world, x, y, z) && (((BlockWireThreshold) b).getColor(world, x, y, z) == color); // threshold is emitting our color of superpower.
		}
		return false;
	}

	private boolean isPowered(TileEntitySuperWire tile, int color) {
		return tile.isColorPowered(color);
	}

	public int getPowerDirection(World world, int x, int y, int z, int color) {
		TileEntitySuperWire tile = getSuperTile(world, x, y, z);
		if (tile != null) {
			return tile.getPowerDirection(color);
		}
		return 6;
	}

	public void setPoweredBy(World world, int x, int y, int z, int color, int dir) {
		TileEntitySuperWire tile = getSuperTile(world, x, y, z);
		if (tile != null) {
			tile.setPowerDirection(color, dir);
			updateSuperNeighbors(world, x, y, z, color, true);
		}
	}

	public void setUnpowered(World world, int x, int y, int z, int color) {
		TileEntitySuperWire tile = getSuperTile(world, x, y, z);
		if (tile != null) {
			tile.setPowerDirection(color, 6);
			updateSuperNeighbors(world, x, y, z, color, false);
		}
	}

	public void updateSuperNeighbors(World world, int x, int y, int z, int color, boolean risingEdge) {
		for (int dir = 0; dir < 6; dir++) {
			updateSuperState(world, x + Facing.b[dir], y + Facing.c[dir], z + Facing.d[dir], dir, color, risingEdge); // powered, rising edge
		}
	}

	public void updateSuperState(World world, int x, int y, int z, int fromDir, int color, boolean risingEdge) {
		Block b = Block.byId[world.getTypeId(x, y, z)];
		if (b instanceof BlockSuperWire) {
			((BlockSuperWire) b).handleWireChange(world, x, y, z, fromDir, color, risingEdge);
		} else if (b instanceof BlockWireThreshold) {
			if (world.getData(x, y, z) == color) { // only update thresholds that are our color
				world.update(x, y, z, 0);
			}
		}
	}

	@Override
	public boolean b(World world, int x, int y, int z, int eventId, int eventData) { // tile event received
		super.b(world, x, y, z, eventId, eventData);
		TileEntity tile = world.getTileEntity(x, y, z);
		return tile != null ? tile.b(eventId, eventData) : false;
	}

	@Override
	public TileEntity b(World arg0) { // get tile entity
		return new TileEntitySuperWire();
	}

	public TileEntitySuperWire getSuperTile(World world, int x, int y, int z) { // Utility method to get a tile entity.
		return (TileEntitySuperWire) world.getTileEntity(x, y, z);
	}
}
