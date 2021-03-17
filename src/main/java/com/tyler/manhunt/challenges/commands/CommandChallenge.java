package com.tyler.manhunt.challenges.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tyler.manhunt.challenges.Challenges;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class CommandChallenge {
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		LiteralArgumentBuilder<CommandSource> baseCommand =
				Commands.literal("challenge");
		
		for (String challengeName : Challenges.Status.challengeList) {
			baseCommand = baseCommand
					.then(Commands.literal(challengeName)
							.then(Commands.argument("status", BoolArgumentType.bool())
									.executes(Challenges.Status::setStatus)));
		}
		baseCommand.executes(context -> {
			StringBuilder message = new StringBuilder();
			for (String challenge : Challenges.Status.challengeList)
				message.append(challenge)
						.append(": ")
						.append(Challenges.getStatus(challenge))
						.append("\n");
			context.getSource().sendFeedback(
					new StringTextComponent(message.toString()), true);
			return 1;
		});
		dispatcher.register(baseCommand);
	}
}
