package com.dm.earth.heatwaves.impl;

import com.dm.earth.heatwaves.api.TemperatureFactor;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BiomeTemperatureFactor implements TemperatureFactor {

	@Override
	@SuppressWarnings({ "deprecation", "UnstableApiUsage" })
	public Info increase(World world, BlockPos pos, int environment) {
		float weather = world.isRaining() ? 0 : (world.getBiome(pos).value().doesNotSnow(pos) ? 3.5f : 8.5f);
		return new Info(
				(int) (processTemp(world.getBiome(pos).value().getTemperature(pos)) + FluidConstants.WATER_TEMPERATURE
						+ weather),
				false, false);
	}

	private static float processTemp(float temp) {
		return ((temp * 20F) - 2.0F) * .95f;
	}

}
