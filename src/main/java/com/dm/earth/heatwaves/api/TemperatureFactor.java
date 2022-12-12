package com.dm.earth.heatwaves.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface TemperatureFactor {
	/**
	 * Get the temperature increase.
	 *
	 * @param world The world.
	 * @param pos   The position.
	 * @return The temperature increase.
	 */
	Info increase(World world, BlockPos pos);

	public static record Info(int increase, boolean override) {
		public static Info of(int increase) {
			return new Info(increase, false);
		}
	}
}
