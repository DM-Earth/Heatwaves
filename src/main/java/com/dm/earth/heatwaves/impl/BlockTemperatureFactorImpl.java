package com.dm.earth.heatwaves.impl;

import java.util.ArrayList;
import java.util.Optional;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.dm.earth.heatwaves.api.BlockTemperature;
import com.dm.earth.heatwaves.api.TemperatureFactor;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Internal
public class BlockTemperatureFactorImpl implements TemperatureFactor {

	public static final int MAX_DISTANCE = 8;

	@Override
	public Info increase(World world, BlockPos pos) {
		Optional<Integer> targetTemp = BlockTemperature.get(world, pos);
		if (targetTemp.isPresent())
			return new Info(targetTemp.get(), true);
		int range = (MAX_DISTANCE * 2) + 1;
		ArrayList<BlockPos> sources = new ArrayList<>();
		while (true) {
			Optional<BlockPos> otp = BlockPos.findClosest(pos, range, range,
					cp -> BlockTemperature.get(world, cp).isPresent() && !sources.contains(cp));
			if (otp.isEmpty())
				break;
			BlockPos tp = otp.get();
			sources.add(tp);
		}
		return Info.of(0);
	}

}
