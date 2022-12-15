package com.dm.earth.heatwaves;

import com.dm.earth.heatwaves.api.BlockTemperature;
import com.dm.earth.heatwaves.api.BlockTemperatureKeeper;
import com.dm.earth.heatwaves.api.BlockTemperatureSource;
import com.dm.earth.heatwaves.api.TemperatureFactor;
import com.dm.earth.heatwaves.impl.BiomeTemperatureFactor;
import com.dm.earth.heatwaves.impl.BlockTemperatureFactor;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;

import net.minecraft.block.Blocks;
import net.minecraft.command.CommandBuildContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

@SuppressWarnings("UnstableApiUsage")
public class Heatwaves implements ModInitializer, CommandRegistrationCallback {
	@Override
	public void onInitialize(ModContainer mod) {
		TemperatureFactor.register(new BlockTemperatureFactor());
		TemperatureFactor.register(new BiomeTemperatureFactor());
		BlockTemperatureSource.register(BlockTemperatureSource.simple(FluidConstants.LAVA_TEMPERATURE, Blocks.LAVA));
		BlockTemperatureKeeper.register(BlockTemperatureKeeper.simple(80, Blocks.WATER));
	}

	@Override
	public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandBuildContext buildContext, RegistrationEnvironment environment) {
		dispatcher.register(CommandManager.literal("heatwaves").then(CommandManager.literal("getTemperature").executes(ctx -> {
			ctx.getSource().sendFeedback(Text.of(String.valueOf(BlockTemperature.getTemperature(ctx.getSource().getWorld(), ctx.getSource().getPlayer().getBlockPos(), true) - FluidConstants.WATER_TEMPERATURE)), false);
			return Command.SINGLE_SUCCESS;
		})));
	}
}
