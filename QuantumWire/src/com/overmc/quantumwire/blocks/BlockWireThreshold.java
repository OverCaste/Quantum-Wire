package com.overmc.quantumwire.blocks;

import net.minecraft.server.v1_7_R3.Block;
import net.minecraft.server.v1_7_R3.BlockCloth;
import net.minecraft.server.v1_7_R3.BlockRedstoneWire;
import net.minecraft.server.v1_7_R3.CreativeModeTab;
import net.minecraft.server.v1_7_R3.Facing;
import net.minecraft.server.v1_7_R3.IBlockAccess;
import net.minecraft.server.v1_7_R3.Material;
import net.minecraft.server.v1_7_R3.TileEntity;
import net.minecraft.server.v1_7_R3.World;

public class BlockWireThreshold extends BlockCloth /* implements IContainer */{

    public BlockWireThreshold( ) {
        super(Material.CLOTH);
        a(CreativeModeTab.b);
        c(0.8f);
        a(l);
        c("cloth");
        d("wool_colored");
    }

    @Override
    public void onPlace(World world, int x, int y, int z) {
        world.update(x, y, z, this);
        updateSuperWireNeighbors(world, x, y, z, true);
        super.onPlace(world, x, y, z);
    }

    @Override
    public void remove(World world, int x, int y, int z, Block block, int l) {
        super.remove(world, x, y, z, block, l);
        world.update(x, y, z, this);
        updateSuperWireNeighbors(world, x, y, z, false);
        // world.s(x, y, z); // remove tile entity
    }

    @Override
    public void doPhysics(World world, int x, int y, int z, Block b) {
        updateSuperWireNeighbors(world, x, y, z, isPowered(world, x, y, z));
    }

    private void updateSuperWireNeighbors(World world, int x, int y, int z, boolean risingEdge) {
        for (int dir = 0; dir < 6; dir++) {
            Block b = world.getType(x + Facing.b[dir], y + Facing.c[dir], z + Facing.d[dir]);
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
                Block b = world.getType(x + Facing.b[dir], y + Facing.c[dir], z + Facing.d[dir]);
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
