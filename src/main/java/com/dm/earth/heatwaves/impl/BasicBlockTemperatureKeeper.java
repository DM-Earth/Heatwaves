package com.dm.earth.heatwaves.impl;

import com.dm.earth.heatwaves.api.BlockTemperatureKeeper;

import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public class BasicBlockTemperatureKeeper
		implements BlockTemperatureKeeper.SimpleKeeper, BlockTemperatureKeeper.Container {

	public static final BasicBlockTemperatureKeeper INSTANCE = new BasicBlockTemperatureKeeper();

	@Override
	public BlockTemperatureKeeper getKeeper() {
		return INSTANCE;
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

		if (material == Material.WOOL) ret = 100;

		return ret;
	}

}
