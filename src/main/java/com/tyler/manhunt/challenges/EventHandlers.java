package com.tyler.manhunt.challenges;

import com.tyler.manhunt.challenges.util.CompassUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.tyler.manhunt.challenges.Challenges.Status.Label;

import java.util.Objects;


public class EventHandlers {
	static class ServerTickHandler {
		@SubscribeEvent
		public void onServerTick(TickEvent.ServerTickEvent event) {
			//nothing needed in here
		}
	}
	static class PlayerTickHandler {
		@SubscribeEvent
		public void onPlayerTick(TickEvent.PlayerTickEvent event) {
			ServerPlayerEntity player = (ServerPlayerEntity) event.player;
			ServerPlayerEntity speedrunner =
					Objects.requireNonNull(
							player.getServer())
							.getPlayerList()
							.getPlayerByUsername(Manhunt.speedrunner);
			
			boolean isRunner = Objects.equals(Manhunt.speedrunner, player.getDisplayName().getString());
			boolean isHunter = Manhunt.hunters.contains(player.getDisplayName().getString());
			
			if (speedrunner == null && Manhunt.hunters.isEmpty()) return;
			if (!isRunner && !isHunter) return;
			
			//handle speedrunner modifiers
			if (isRunner) {
				//remove fire
				if (Challenges.getStatus(Label.FIRE_RESISTANCE)) player.forceFireTicks(-20);
				
				//remove fall damage without preventing crits
				if (Challenges.getStatus(Label.NO_FALL_DAMAGE)) player.fallDistance = 2;
				
				//give invis if crouching
				if (Challenges.getStatus(Label.CROUCH_INVIS)) {
					EffectInstance invis = new EffectInstance(Effects.INVISIBILITY, 20, 1, false, false);
					if (player.isSneaking()) player.addPotionEffect(invis);
					else player.removePotionEffect(Effects.INVISIBILITY);
				}
			}
			
			//handle hunter modifiers
			if (isHunter) {
				//update tracking compasses
				CompassUtil.updateCompass(player, speedrunner);
				
				//TODO: mechanic needs to be redone
				//current idea - hunter gets 5 seconds of glowing when runner crouches
				// then glow goes on 30 second cooldown
				if (Challenges.getStatus(Label.HUNTERS_GLOW)) {
					EffectInstance glowing = new EffectInstance(Effects.GLOWING, 20, 1, false, false);
					if (!player.isSneaking()) player.addPotionEffect(glowing);
					else player.removePotionEffect(Effects.GLOWING);
				}
			}
			
			
			
			
			
		}
	}
	static class DamageEventHandler {
		@SubscribeEvent
		public void onLivingHurt(LivingHurtEvent event) {
		if (!(event.getEntityLiving() instanceof  ServerPlayerEntity)) return;
			ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
			
			boolean isRunner = Objects.equals(Manhunt.speedrunner, player.getDisplayName().getString());
			boolean isHunter = Manhunt.hunters.contains(player.getDisplayName().getString());
			
			//handle runner damage
			if (isRunner) {
				//only modify fall damage
				if (event.getSource() == DamageSource.FALL) {
					if (Challenges.getStatus(Challenges.Status.Label.FALL_DAMAGE_HEALS)) {
						//reverse fall damage into healing
						Challenges.LOGGER.info("Reversing fall damage");
						float newHealth = player.getHealth() + event.getAmount();
						Challenges.LOGGER.info("[Health] "
								+ player.getHealth()
								+ " + "
								+ event.getAmount()
								+ " -> "
								+ newHealth);
						event.setAmount(0F);
						player.setHealth(newHealth);
						
					}
				}
				if (event.getSource() == DamageSource.LAVA
						|| event.getSource() == DamageSource.IN_FIRE) {
					if (Challenges.getStatus(Label.FIRE_RESISTANCE)) event.setAmount(0);
				}
				
				
				
			}
			
			//handle hunter damage
			if (isHunter) {
				//nothing in here for now
				Challenges.LOGGER.info("Doing something to make warning in if (isHunter) in onLivingHurt in EventHandlers");
			}
			
			
			
		}
	}
	static class RespawnHandler {
		@SubscribeEvent
		public void playerRespawn(PlayerEvent.PlayerRespawnEvent respawn) {
			PlayerEntity player = respawn.getPlayer();
			
			if (Manhunt.hunters.contains(player.getDisplayName().getString()))
				player.inventory.addItemStackToInventory(
						new ItemStack(Items.COMPASS));
			
		}
	}
	
	public static void registerEvents() {
		IEventBus bus = MinecraftForge.EVENT_BUS;
		//MinecraftForge.EVENT_BUS.register(new ServerTickHandler());
		bus.register(new PlayerTickHandler());
		bus.register(new RespawnHandler());
		bus.register(new DamageEventHandler());
		//bus.register(new ItemUseHandler());
	}
	
}
