package com.tyler.manhunt.challenges.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.tyler.manhunt.challenges.Challenges;
import com.tyler.manhunt.challenges.Manhunt;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class CommandSpeedrunner {
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(Commands.literal("speedrunner")
				.then(Commands.literal("set")
						.then(Commands.argument("player", EntityArgument.player())
								.executes((Manhunt::setSpeedrunner))))
				.then(Commands.literal("reset")
						.executes(Manhunt::resetSpeedrunner))
				.executes(Manhunt::listSpeedrunner));
	}
}
