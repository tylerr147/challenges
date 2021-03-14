package com.tyler.manhunt.challenges;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Manhunt { //use uuid?
	public static UUID speedrunner;
	public static List<UUID> hunters;
	
	public static int addHunter(CommandContext<CommandSource> context) {
		hunters.add(
				context.getArgument("player", PlayerEntity.class)
						.getUniqueID()
		);
		return 0;
	}
	public static int removeHunter(CommandContext<CommandSource> context) {
		hunters.remove(context.getArgument("player", PlayerEntity.class)
				.getUniqueID());
		return 0;
	}
	
	public static int setSpeedrunner(CommandContext<CommandSource> context) {
		Challenges.LOGGER.info("setSpeedrunner called");
		speedrunner = context.getArgument("player", PlayerEntity.class)
				.getUniqueID();
		return 0;
	}
	
	public static int listSpeedrunner(CommandContext<CommandSource> context) {
		Challenges.LOGGER.info("Listing speedrunner");
		PlayerEntity speedrunner = context
				.getSource()
				.getWorld()
				.getServer()
				.getPlayerList()
				.getPlayerByUUID(Manhunt.speedrunner);
		if (speedrunner == null) {
			Challenges.LOGGER.info("Returning because speedrunner == null");
			return 0;
		}
		Challenges.LOGGER.info(speedrunner.getDisplayName());
		return 0;
	}
	
	public static int listHunters(CommandContext<CommandSource> context) {
		Challenges.LOGGER.info("Listing hunters");
		Manhunt.hunters.forEach(
				(uuid -> Challenges.LOGGER.info(
						Objects.requireNonNull(context
								.getSource()
								.getWorld()
								.getServer()
								.getPlayerList()
								.getPlayerByUUID(uuid))
								.getDisplayName())));
		return 0;
	}
}
