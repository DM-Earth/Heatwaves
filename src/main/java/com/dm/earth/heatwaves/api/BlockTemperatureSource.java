package com.dm.earth.heatwaves.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

/**
 * An interface for getting the temperature of a block.
 */
@FunctionalInterface
public interface BlockTemperatureSource {
	/**
	 * Container for {@code BlockTemperatureSource}.
	 */
	public static interface Container {
		/**
		 * A simple implementation of {@code Container}.
		 */
		public static class Simple implements Container {
			/**
			 * Creates a new {@code Simple} instance.
			 *
			 * @param temperature The temperature of the blocks.
			 * @param blocks      The blocks.
			 * @return The created instance.
			 */
			public static Simple of(BlockTemperatureSource temperature, Block... blocks) {
				return new Simple(temperature, List.of(blocks));
			}

			protected final BlockTemperatureSource temperature;

			protected final List<Block> blocks;

			protected Simple(BlockTemperatureSource temperature, List<Block> blocks) {
				this.temperature = temperature;
				this.blocks = blocks;
			}

			@Override
			public BlockTemperatureSource getTemperature() {
				return this.temperature;
			}

			@Override
			public boolean isValid(WorldAccess world, BlockPos pos) {
				return blocks.contains(world.getBlockState(pos).getBlock());
			}
		}

		/**
		 * @return The temperature of the blocks.
		 */
		BlockTemperatureSource getTemperature();

		/**
		 * @param world The world.
		 * @param pos   The position.
		 * @return Whether the block is valid.
		 */
		boolean isValid(WorldAccess world, BlockPos pos);
	}

	static final ArrayList<Container> TEMPERATURES = new ArrayList<>();

	/**
	 * Registers a {@code BlockTemperatureSource$Container}.
	 *
	 * @param temperature The temperature container to register.
	 * @return The input temperature.
	 */
	static <T extends Container> T register(T temperature) {
		if (!TEMPERATURES.contains(temperature))
			TEMPERATURES.add(temperature);
		return temperature;
	}

	/**
	 * Gets the self temperature of the target block.
	 *
	 * @param world The world.
	 * @param pos   The position.
	 * @return The temperature of the block.
	 */
	static Optional<Integer> getSource(WorldAccess world, BlockPos pos) {
		ArrayList<Integer> temperatures = new ArrayList<>();
		for (Container container : TEMPERATURES) {
			if (container.isValid(world, pos))
				temperatures.add(container.getTemperature().get(world, pos));
		}
		if (temperatures.size() > 0)
			return Optional.of(temperatures.stream().mapToInt(Integer::intValue).sum() / temperatures.size());
		else
			return Optional.empty();
	}

	/**
	 * Creates a new {@code BlockTemperatureSource$Container.Simple} instance.
	 *
	 * @param temperature The temperature of the blocks.
	 * @param blocks      The blocks.
	 * @return The created instance.
	 */
	static Container.Simple simple(int temperature, Block... blocks) {
		return Container.Simple.of((world, pos) -> temperature, blocks);
	}

	/**
	 * @return The temperature of the block.
	 */
	int get(WorldAccess world, BlockPos pos);
}
