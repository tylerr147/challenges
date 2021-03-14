package com.tyler.manhunt.challenges.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.tyler.manhunt.challenges.Manhunt;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;

public class CommandSpeedrunner {
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(Commands.literal("speedrunner")
				.then(Commands.argument("player", EntityArgument.player())
						.executes(Manhunt::setSpeedrunner))
				.executes(Manhunt::listSpeedrunner));
	}
}
