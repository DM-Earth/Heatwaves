package com.dm.earth.heatwaves.api;

import java.util.List;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTemperature {

	private BlockTemperature() {
	}

	/**
	 * Get the temperature of the target block.
	 *
	 * @param world           The world.
	 * @param pos             The position.
	 * @param containsSpecial Whether to include special factors.
	 * @return The temperature.
	 */
	public static int getTemperature(World world, BlockPos pos, boolean containsSpecial) {
		List<TemperatureFactor.Info> infos = TemperatureFactor.FACTORS.stream()
				.map(factor -> factor.increase(world, pos, 0)).filter(info -> !info.special()).toList();
		int ret = 0;
		for (TemperatureFactor.Info info : infos)
			if (!info.special()) {
				if (info.override())
					return info.increase();
				else
					ret += info.increase();
			}
		if (containsSpecial) {
			int env = ret;
			List<TemperatureFactor.Info> specialInfos = TemperatureFactor.FACTORS.stream()
					.map(factor -> factor.increase(world, pos, env)).filter(TemperatureFactor.Info::special).toList();
			for (TemperatureFactor.Info info : specialInfos)
				if (info.override())
					return info.increase();
				else {
					ret += info.increase();
				}
		}
		// 0 is the lowest temperature
		return Math.max(ret, 0);
	}
}
