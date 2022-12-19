package com.dm.earth.heatwaves.impl;

import java.util.ArrayList;
import java.util.Optional;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.dm.earth.heatwaves.api.BlockTemperatureKeeper;
import com.dm.earth.heatwaves.api.BlockTemperatureSource;
import com.dm.earth.heatwaves.api.TemperatureFactor;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Internal
public class BlockTemperatureFactor implements TemperatureFactor {

	public static final int MAX_DISTANCE = 8;

	@Override
	public Info increase(World world, BlockPos pos, int env) {
		Optional<Integer> targetTemp = BlockTemperatureSource.getSource(world, pos);
		if (targetTemp.isPresent())
			return new Info(targetTemp.get(), true, true);
		int range = MAX_DISTANCE;
		ArrayList<BlockPos> sources = new ArrayList<>();
		int ret = 0;
		while (true) {
			Optional<BlockPos> otp = BlockPos.findClosest(pos, range, range,
					cp -> BlockTemperatureSource.getSource(world, cp).isPresent() && !sources.contains(cp));
			if (otp.isEmpty())
				break;
			BlockPos tp = otp.get();
			sources.add(tp);
			ArrayList<BlockPos> passed = new ArrayList<>();
			while (true) {
				Optional<BlockPos> otp2 = BlockPos.findClosest(tp, range, range,
						cp -> Math.abs(cp.getSquaredDistance(pos) + cp.getSquaredDistance(tp)
								- pos.getSquaredDistance(tp)) < 1 && !cp.equals(pos) && !cp.equals(tp)
								&& !passed.contains(cp));
				if (otp2.isEmpty())
					break;
				passed.add(otp2.get());
			}

			int finalTemp = BlockTemperatureSource.getSource(world, tp).get() - env;
			int decrep = (int) Math.pow(finalTemp, 4.5d / MAX_DISTANCE);
			for (BlockPos passing : passed) {
				finalTemp -= decrep;
				finalTemp = BlockTemperatureKeeper.process(world, passing, finalTemp);
				if (decrep * finalTemp < 0) {
					finalTemp = 0;
					break;
				}
			}

			if (finalTemp > ret)
				ret = finalTemp;
		}

		return new Info(ret, false, true);
	}

}
