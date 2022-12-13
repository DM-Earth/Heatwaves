package com.dm.earth.heatwaves.impl;

import com.dm.earth.heatwaves.api.TemperatureFactor;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BiomeTemperatureFactor implements TemperatureFactor {

	@Override
	@SuppressWarnings({"deprecation", "UnstableApiUsage"})
	public Info increase(World world, BlockPos pos, int environment) {
		return new Info((int) ((world.getBiome(pos).value().getTemperature(pos) * 35d) + FluidConstants.WATER_TEMPERATURE), false, false);
	}

}
