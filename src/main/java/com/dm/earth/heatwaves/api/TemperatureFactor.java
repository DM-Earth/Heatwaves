package com.dm.earth.heatwaves.api;

import java.util.ArrayList;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface TemperatureFactor {
	/**
	 * Get the temperature increase.
	 *
	 * @param world The world.
	 * @param pos   The position.
	 * @param environment The environment temperature.
	 * @return The temperature increase.
	 */
	Info increase(World world, BlockPos pos, int environment);

	public static record Info(int increase, boolean override, boolean special) {
	}

	static final ArrayList<TemperatureFactor> FACTORS = new ArrayList<>();

	/**
	 * Register a temperature factor.
	 *
	 * @param <T>    The type of the factor.
	 * @param factor The factor.
	 * @return The registered factor.
	 */
	static <T extends TemperatureFactor> T register(T factor) {
		if (!FACTORS.contains(factor))
			FACTORS.add(factor);
		return factor;
	}
}
