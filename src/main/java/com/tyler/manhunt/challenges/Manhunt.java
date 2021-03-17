package com.tyler.manhunt.challenges;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Manhunt { //use uuid?
	public static String speedrunner;
	public static ArrayList<String> hunters = new ArrayList<>();
	
	public static int addHunter(CommandContext<CommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity hunter;
		try {
			EntitySelector entitySelector = context.getArgument("player", EntitySelector.class);
			hunter = entitySelector.selectOnePlayer(context.getSource());
			hunters.add(hunter.getDisplayName().getString());
		} catch(Exception e) {
			context.getSource().sendFeedback(new StringTextComponent(e.toString()), true);
			hunter = null;
		}
		
		assert hunter != null;
		IFormattableTextComponent message =
				new StringTextComponent("Added ")
						.appendSibling(hunter.getDisplayName())
						.appendString(" to hunters");
		context.getSource()
				.sendFeedback(message, true); //TODO: set correct string
		
		return 1;
	}
	public static int resetSpeedrunner(CommandContext<CommandSource> context) {
		speedrunner = null;
		return 1;
	}
	public static int removeHunter(CommandContext<CommandSource> context) throws CommandSyntaxException {
		EntitySelector entitySelector = context.getArgument("player", EntitySelector.class);
		ServerPlayerEntity hunter = entitySelector.selectOnePlayer(context.getSource());
		hunters.remove(hunter.getDisplayName().getString());
		
		
		IFormattableTextComponent message =
				new StringTextComponent("Removed ")
						.appendSibling(hunter.getDisplayName())
						.appendString(" from hunters");
		context.getSource()
				.sendFeedback(message, true); //TODO: set correct string
		
		return 1;
	}
	
	public static int setSpeedrunner(CommandContext<CommandSource> context) throws CommandSyntaxException {
		EntitySelector entitySelector = context.getArgument("player", EntitySelector.class);
		ServerPlayerEntity speedrunnerEntity = entitySelector.selectOnePlayer(context.getSource());
		speedrunner = speedrunnerEntity.getDisplayName().getString();
		
		context.getSource().sendFeedback(new StringTextComponent("Set Manhunt Speedrunner to ")
				.appendSibling(speedrunnerEntity.getDisplayName()), true);
		
		return 1;
	}
	
	public static int listSpeedrunner(CommandContext<CommandSource> context) {
		Challenges.LOGGER.info("Listing speedrunner");
		PlayerEntity speedrunner = context
				.getSource()
				.getWorld()
				.getServer()
				.getPlayerList()
				.getPlayerByUsername(Manhunt.speedrunner);
		if (speedrunner == null) {
			Challenges.LOGGER.info("Returning because speedrunner == null");
			return 0;
		}
		context.getSource().sendFeedback(speedrunner.getDisplayName(), true);
		return 0;
	}
	
	public static int listHunters(CommandContext<CommandSource> context) {
		Challenges.LOGGER.info("Listing hunters");
		Manhunt.hunters.forEach(
				(name -> context.getSource().sendFeedback(
						Objects.requireNonNull(context
							.getSource()
							.getWorld()
							.getServer()
							.getPlayerList()
							.getPlayerByUsername(name))
							.getDisplayName(), true)));
						
		
		return 0;
	}
}
