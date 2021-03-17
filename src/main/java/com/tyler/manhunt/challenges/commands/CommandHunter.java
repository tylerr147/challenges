package com.tyler.manhunt.challenges.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.tyler.manhunt.challenges.Manhunt;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;

public class CommandHunter {
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("hunter")
				.then(Commands.literal("add")
					.then(Commands.argument("player", EntityArgument.player())
						.executes(Manhunt::addHunter)))
				.then(Commands.literal("remove")
					.then(Commands.argument("player", EntityArgument.player())
						.executes(Manhunt::removeHunter)))
				.then(Commands.literal("list")
					.executes(Manhunt::listHunters)));
	}
}
