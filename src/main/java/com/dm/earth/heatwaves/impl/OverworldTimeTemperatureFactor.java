package com.dm.earth.heatwaves.impl;

import com.dm.earth.heatwaves.api.TemperatureFactor;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OverworldTimeTemperatureFactor implements TemperatureFactor {

	@Override
	public Info increase(World world, BlockPos pos, int environment) {
		if (world.getRegistryKey() != World.OVERWORLD)
			return new Info(0, false, false);
		return new Info((int) (2.5f - (processTime(world.getTime()) / 2000f)), false, false);
	}

	/**
	 * Process the time of day.
	 *
	 * @param time The time of day.
	 * @return The processed time of day.
	 */
	private static long processTime(long time) {
		long raw = time - 7000;
		if (raw < 0)
			raw += 24000;
		if (raw > 12000)
			raw = 24000 - raw;
		return raw;
	}

}
