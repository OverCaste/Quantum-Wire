package com.overmc.quantumwire.blocks;

import net.minecraft.server.v1_6_R3.*;

public class BlockWireThreshold extends BlockCloth /* implements IContainer */{

	public BlockWireThreshold( ) {
		super(35, Material.CLOTH);
		c(0.8F);
		a(n);
		c("cloth");
		d("wool_colored");
		// isTileEntity = true;
	}

	@Override
	public void onPlace(World world, int x, int y, int z) {
		world.update(x, y, z, 0);
		updateSuperWireNeighbors(world, x, y, z, true);
		super.onPlace(world, x, y, z);
	}

	@Override
	public void remove(World world, int x, int y, int z, int i, int j) {
		super.remove(world, x, y, z, i, j);
		world.update(x, y, z, 0);
		updateSuperWireNeighbors(world, x, y, z, false);
		// world.s(x, y, z); // remove tile entity
	}

	@Override
	public void doPhysics(World world, int x, int y, int z, int l) {
		updateSuperWireNeighbors(world, x, y, z, isPowered(world, x, y, z));
	}

	private void updateSuperWireNeighbors(World world, int x, int y, int z, boolean risingEdge) {
		for (int dir = 0; dir < 6; dir++) {
			Block b = Block.byId[world.getTypeId(x + Facing.b[dir], y + Facing.c[dir], z + Facing.d[dir])];
			if ((b != null) && (b instanceof BlockSuperWire)) {
				((BlockSuperWire) b).updateSuperState(world, x + Facing.b[dir], y + Facing.c[dir], z + Facing.d[dir], dir, world.getData(x, y, z), risingEdge);
			}
		}
	}

	/*
	 * @Override
	 * public boolean b(World world, int x, int y, int z, int eventId, int eventData) { // tile event received
	 * super.b(world, x, y, z, eventId, eventData);
	 * TileEntity tile = world.getTileEntity(x, y, z);
	 * return tile != null ? tile.b(eventId, eventData) : false;
	 * }
	 */

	/*
	 * @Override
	 * public TileEntity b(World arg0) { // get tile entity
	 * return new TileEntityWireThreshold();
	 * }
	 */

	public boolean isPowered(World world, int x, int y, int z) {
		for (int dir = 0; dir < 6; dir++) {
			if (world.getBlockFacePower(x + Facing.b[dir], y + Facing.c[dir], z + Facing.d[dir], dir) >= 1) { // if a diode or torch is providing any amount of power
				Block b = Block.byId[world.getTypeId(x + Facing.b[dir], y + Facing.c[dir], z + Facing.d[dir])];
				if ((b != null) && (!((b instanceof BlockRedstoneWire) || (b instanceof BlockWireThreshold))) && b.isPowerSource()) { // all actually powering, non-redstone wire blocks
					return true;
				}
			}
		}
		return false;
	}

	public int getColor(World world, int x, int y, int z) {
		return world.getData(x, y, z);
	}

	@Override
	public boolean isPowerSource( ) {
		return true;
	}

	@Override
	public int b(IBlockAccess world, int x, int y, int z, int l) {// get weak power
		return checkSuperPowered(world, x, y, z) ? 15 : 0;
	}

	private boolean checkSuperPowered(IBlockAccess world, int x, int y, int z) {
		int color = world.getData(x, y, z);
		for (int direction = 0; direction < 6; direction++) {
			if (checkSuperPowered(color, world.getTileEntity(x + Facing.b[direction], y + Facing.c[direction], z + Facing.d[direction]))) {
				return true;
			}
		}
		return false;
	}

	private boolean checkSuperPowered(int color, TileEntity tile) {
		if ((tile != null) && (tile instanceof TileEntitySuperWire)) {
			if (((TileEntitySuperWire) tile).isColorPowered(color)) {
				return true;
			}
		}
		return false;
	}
}
