package com.dm.earth.heatwaves.impl;

import com.dm.earth.heatwaves.api.BlockTemperatureKeeper;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public class BasicBlockTemperatureKeeper
		implements BlockTemperatureKeeper.SimpleKeeper, BlockTemperatureKeeper.Container {

	@Override
	public BlockTemperatureKeeper getKeeper() {
		return this;
	}

	@Override
	public boolean isValid(WorldAccess world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		return state.isFullCube(world, pos) && this.getKept(world, pos) > 0;
	}

	@Override
	public int getKept(WorldAccess world, BlockPos pos) {
		int ret = 0;
		BlockState state = world.getBlockState(pos);
		Material material = state.getMaterial();

		if (material == Material.WOOL)
			ret = 100;
		if (material == Material.PORTAL)
			ret = 1;
		if (material == Material.ICE || material == Material.SNOW_BLOCK)
			ret = (int) (FluidConstants.WATER_TEMPERATURE * 0.9);
		if (material == Material.STONE)
			ret = 50;
		if (material == Material.METAL)
			ret = 15;
		if (material == Material.WOOD || material == Material.NETHER_WOOD)
			ret = 75;

		return ret;
	}

	@Override
	public int keep(WorldAccess world, BlockPos pos, int temperature) {
		Material material = world.getBlockState(pos).getMaterial();
		if (material == Material.PORTAL)
			return temperature;
		return SimpleKeeper.super.keep(world, pos, temperature);
	}

}
