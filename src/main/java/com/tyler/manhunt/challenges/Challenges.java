package com.tyler.manhunt.challenges;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.tyler.manhunt.challenges.commands.*;
import net.minecraft.block.*;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

@Mod("challenges")
public class Challenges {
	
	// Directly reference a log4j logger.
	public static final Logger LOGGER = LogManager.getLogger();
	
	public Challenges() {
		Status.initMap();
		
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		eventBus.addListener(this::setup);
		eventBus.addListener(this::doClientStuff);
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	private void setup(final FMLCommonSetupEvent event) {
		// some preinit code
		LOGGER.info("HELLO FROM PREINIT");
		LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
	}
	
	private void doClientStuff(final FMLClientSetupEvent event) {
		// do something that can only be done on the client
		LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
	}
	
	// You can use SubscribeEvent and let the Event Bus discover methods to call
	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {
		// do something when the server starts
		EventHandlers.registerEvents();
		LOGGER.info("Server starting");
	}
	
	@SubscribeEvent
	public void registerCommands(RegisterCommandsEvent event)
	{
		CommandSpeedrunner.register(event.getDispatcher());
		CommandHunter.register(event.getDispatcher());
		CommandChallenge.register(event.getDispatcher());
	}
	
	// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
	// Event bus for receiving Registry Events)
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		@SubscribeEvent
		public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
		
		}
		@SubscribeEvent
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
			// register a new block here
			LOGGER.info("HELLO from Register Block");
		}
	}
	
	
	public static boolean getStatus(Status.Label challenge) {
		return Status.map.get(challenge.name);
	}
	
	public static boolean getStatus(String challenge) {
		return Status.map.get(challenge);
	}
	
	public static class Status {
		public enum Label {
			CROUCH_INVIS("crouchInvis"),
			FIRE_RESISTANCE("fireResistance"),
			NO_FALL_DAMAGE("noFallDamage"),
			HUNTERS_GLOW("huntersGlow"), //TODO: rewrite mechanic; 30 second cooldown?
			CROUCH_NO_TRACKED("crouchNoTracked"), //TODO: test
			FALL_DAMAGE_HEALS("fallDamageHeals");
			
			String name;
			Label(String name) {
				this.name = name;
			}
		}
		public final static ArrayList<String> challengeList = new ArrayList<>();
		
		public static int setStatus(CommandContext<CommandSource> context) {
			String input = context.getInput().split(" ")[1];
			boolean status = BoolArgumentType.getBool(
					context, "status");
			
			map.put(input, status);
			context.getSource().sendFeedback(new StringTextComponent("Set " + input + " to " + status), true);
			return 1;
		}
		
		public static HashMap<String, Boolean> map = new HashMap<>();
		public static void initMap() {
			for (Label challenge : Label.values())
				challengeList.add(challenge.name);
			
			for (String name : challengeList)
				map.put(name, false);
		}
		
	}
}
