package com.dm.earth.heatwaves;

import java.util.function.Supplier;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dm.earth.heatwaves.api.BlockTemperature;
import com.dm.earth.heatwaves.api.BlockTemperatureKeeper;
import com.dm.earth.heatwaves.api.BlockTemperatureSource;
import com.dm.earth.heatwaves.api.TemperatureFactor;
import com.dm.earth.heatwaves.impl.BasicBlockTemperatureKeeper;
import com.dm.earth.heatwaves.impl.BiomeTemperatureFactor;
import com.dm.earth.heatwaves.impl.BlockTemperatureFactor;
import com.dm.earth.heatwaves.impl.OverworldTimeTemperatureFactor;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CandleBlock;
import net.minecraft.command.CommandBuildContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;

@SuppressWarnings("UnstableApiUsage")
public class Heatwaves implements ModInitializer, CommandRegistrationCallback {
	public static final String MOD_ID = "heatwaves";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static boolean debug = false;

	@Override
	public void onInitialize(ModContainer mod) {
		TemperatureFactor.register(new BlockTemperatureFactor());
		TemperatureFactor.register(new BiomeTemperatureFactor());
		TemperatureFactor.register(new OverworldTimeTemperatureFactor());

		BlockTemperatureKeeper.register(BlockTemperatureKeeper.simple(FluidConstants.WATER_TEMPERATURE, Blocks.WATER));
		BlockTemperatureKeeper.register(new BlockTemperatureKeeper.Container.Custom(
				(BlockTemperatureKeeper.SimpleKeeper) (world, pos) -> FluidConstants.WATER_TEMPERATURE,
				(world, pos) -> {
					BlockState state = world.getBlockState(pos);
					return state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED);
				}));
		BlockTemperatureKeeper.register(new BasicBlockTemperatureKeeper());

		BlockTemperatureSource.register(BlockTemperatureSource.simple(TemperatureConstants.LAVA_TEMPERATURE,
				Blocks.LAVA, Blocks.LAVA_CAULDRON));
		BlockTemperatureSource.register(BlockTemperatureSource.simple(TemperatureConstants.FIRE_TEMPERATURE,
				Blocks.FIRE, Blocks.CAMPFIRE, Blocks.MAGMA_BLOCK));
		BlockTemperatureSource.register(BlockTemperatureSource.simple(TemperatureConstants.SOUL_FIRE_TEMPERATURE,
				Blocks.SOUL_FIRE, Blocks.SOUL_CAMPFIRE));
		BlockTemperatureSource.register(BlockTemperatureSource.custom((world, pos) -> 365,
				(world, pos) -> world.getBlockState(pos).getBlock() instanceof AbstractFurnaceBlock
						&& world.getBlockState(pos).get(AbstractFurnaceBlock.LIT)));
		BlockTemperatureSource
				.register(BlockTemperatureSource.simple(TemperatureConstants.ICE_WATER_TEMPERATURE, Blocks.ICE));
		BlockTemperatureSource.register(
				BlockTemperatureSource.simple(TemperatureConstants.ICE_WATER_TEMPERATURE - 10, Blocks.PACKED_ICE));
		BlockTemperatureSource.register(
				BlockTemperatureSource.simple(TemperatureConstants.ICE_WATER_TEMPERATURE - 20, Blocks.BLUE_ICE));
		BlockTemperatureSource
				.register(BlockTemperatureSource.simple(TemperatureConstants.FIRE_TEMPERATURE - 35, Blocks.TORCH));
		BlockTemperatureSource
				.register(BlockTemperatureSource.simple(TemperatureConstants.FIRE_TEMPERATURE - 50, Blocks.SOUL_TORCH));
		BlockTemperatureSource.register(BlockTemperatureSource.custom(
				(world, pos) -> world.getBlockState(pos).get(CandleBlock.CANDLES)
						* 5 + TemperatureConstants.FIRE_TEMPERATURE - 55,
				(world, pos) -> world.getBlockState(pos).getBlock() instanceof CandleBlock
						&& world.getBlockState(pos).get(CandleBlock.LIT)));
	}

	@Override
	public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandBuildContext buildContext,
			RegistrationEnvironment environment) {
		dispatcher.register(
				CommandManager.literal("heatwaves").then(CommandManager.literal("getTemperature")
						.requires(src -> src.hasPermissionLevel(3)).executes(ctx -> {
							ctx.getSource().sendFeedback(Text.of("Current block temperature: " + (BlockTemperature
									.getTemperature(ctx.getSource().getWorld(),
											ctx.getSource().getPlayer().getBlockPos(), true)
									- FluidConstants.WATER_TEMPERATURE)), false);
							return Command.SINGLE_SUCCESS;
						})).then(CommandManager.literal("debug").requires(src -> src.hasPermissionLevel(4))
								.executes(ctx -> {
									debug = !debug;
									ctx.getSource().sendFeedback(
											Text.of("Debug mode turned " + (debug ? "on" : "off") + " for Heatwaves!"),
											true);
									return Command.SINGLE_SUCCESS;
								})));
	}

	public static boolean debug(Supplier<String> message) {
		if (debug)
			LOGGER.info("[Heatwaves/DEBUG] " + message.get());
		return debug;
	}
}
