package com.dm.earth.heatwaves.api;

import java.util.ArrayList;
import java.util.List;

import com.dm.earth.heatwaves.Heatwaves;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

@FunctionalInterface
public interface BlockTemperatureKeeper {
	/**
	 * Container for {@code BlockTemperatureKeeper}.
	 */
	interface Container {
		/**
		 * A simple implementation of {@code Container}.
		 */
		class Simple implements Container {
			/**
			 * Creates a new {@code Simple} instance.
			 *
			 * @param keeper The keeper of the blocks.
			 * @param blocks The blocks.
			 * @return The created instance.
			 */
			public static Simple of(BlockTemperatureKeeper keeper, Block... blocks) {
				return new Simple(keeper, List.of(blocks));
			}

			protected final BlockTemperatureKeeper keeper;

			protected final List<Block> blocks;

			protected Simple(BlockTemperatureKeeper keeper, List<Block> blocks) {
				this.keeper = keeper;
				this.blocks = blocks;
			}

			@Override
			public BlockTemperatureKeeper getKeeper() {
				return this.keeper;
			}

			@Override
			public boolean isValid(WorldAccess world, BlockPos pos) {
				return blocks.contains(world.getBlockState(pos).getBlock());
			}
		}

		class Custom implements Container {
			protected final BlockTemperatureKeeper keeper;
			protected final ValidPredicate predicate;

			public Custom(BlockTemperatureKeeper keeper, ValidPredicate predicate) {
				this.keeper = keeper;
				this.predicate = predicate;
			}

			@Override
			public BlockTemperatureKeeper getKeeper() {
				return this.keeper;
			}

			@Override
			public boolean isValid(WorldAccess world, BlockPos pos) {
				return this.predicate.test(world, pos);
			}

			@FunctionalInterface
			public static interface ValidPredicate {
				boolean test(WorldAccess world, BlockPos pos);
			}

		}

		/**
		 * @return The keeper of the blocks.
		 */
		BlockTemperatureKeeper getKeeper();

		/**
		 * Checks if the block is valid.
		 *
		 * @param world The world.
		 * @param pos   The position.
		 * @return Whether the block is valid.
		 */
		boolean isValid(WorldAccess world, BlockPos pos);
	}

	/**
	 * A simple variant of {@code BlockTemperatureKeeper}.
	 */
	@FunctionalInterface
	interface SimpleKeeper extends BlockTemperatureKeeper {

		@Override
		default int keep(WorldAccess world, BlockPos pos, int temperature) {
			return Math.min(Math.abs(temperature), getKept(world, pos));
		}

		int getKept(WorldAccess world, BlockPos pos);

	}

	static final ArrayList<Container> KEEPERS = new ArrayList<>();

	/**
	 * Registers the keeper of the blocks.
	 *
	 * @param keeper The keeper.
	 * @param blocks The blocks.
	 * @return The keeper.
	 */
	static <T extends Container> T register(T keeper) {
		KEEPERS.add(keeper);
		return keeper;
	}

	/**
	 * Creates a new {@code Simple} instance.
	 *
	 * @param kept   The kept temperature.
	 * @param blocks The blocks.
	 * @return The created instance.
	 */
	static Container.Simple simple(int kept, Block... blocks) {
		return Container.Simple.of((SimpleKeeper) (world, pos) -> kept, blocks);
	}

	/**
	 * Process a temperature addition.
	 *
	 * @param world    The world.
	 * @param pos      The position.
	 * @param addition The input temperature addition.
	 * @return The processed temperature addition.
	 */
	static int process(WorldAccess world, BlockPos pos, int addition) {
		if (addition == 0)
			return 0;
		ArrayList<Integer> keepers = new ArrayList<>();
		for (Container container : KEEPERS) {
			if (container.isValid(world, pos)) {
				keepers.add(container.getKeeper().keep(world, pos, addition));
				Heatwaves.debug(() -> "Detected keeper at " + pos.toString() + ": "
						+ Registries.BLOCK.getId(world.getBlockState(pos).getBlock()).toString());
			} else
				Heatwaves.debug(() -> "Not a keeper at " + pos.toString() + ": "
						+ Registries.BLOCK.getId(world.getBlockState(pos).getBlock()).toString());
		}
		int ret = addition + ((addition > 0 ? -1 : 1)
				* (keepers.isEmpty() ? 0 : keepers.stream().mapToInt(Integer::intValue).sum() / keepers.size()));
		return (ret * addition) < 0 ? 0 : ret;
	}

	/**
	 * Keeps the temperature to the block.
	 *
	 * @param world       The world.
	 * @param pos         The position.
	 * @param temperature The input temperature.
	 * @return The kept temperature to the block.
	 */
	int keep(WorldAccess world, BlockPos pos, int temperature);
}
