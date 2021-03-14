package com.tyler.manhunt.challenges;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Objects;

public class EventHandlers {
	static class ServerTickHandler {
		@SubscribeEvent
		public void onServerTick(TickEvent.ServerTickEvent event) {
			//System.out.println("Ticking server");
		}
	}
	static class PlayerTickHandler {
		@SubscribeEvent
		public void onPlayerTick(TickEvent.PlayerTickEvent event) {
			PlayerEntity player = event.player;
			
			if (Challenges.Status.noFallDamage
			&& Manhunt.speedrunner == player.getUniqueID()) {
				player.fallDistance = 0;
			}
		}
	}
	static class RespawnHandler {
		@SubscribeEvent
		public void playerRespawn(PlayerEvent.PlayerRespawnEvent respawn) {
			PlayerEntity player = respawn.getPlayer();
			
			if (Manhunt.hunters.contains(player.getUniqueID()))
				player.inventory.addItemStackToInventory(
						new ItemStack(Items.COMPASS));
			
		}
	}
	static class ItemUseHandler {
		@SubscribeEvent
		public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
			Challenges.LOGGER.info("onRightClickItem called");
			Item item = event.getItemStack().getItem();
			ItemStack itemStack = event.getItemStack();
			PlayerEntity player = event.getPlayer();
			World world = event.getWorld();
			
			if (item != Items.COMPASS) return;
			
			PlayerEntity speedrunner = Objects.requireNonNull(
					world.getServer())
					.getPlayerList().getPlayerByUUID(
							Manhunt.speedrunner);
			assert player != null && speedrunner != null;
			
			if (Manhunt.hunters.contains(player.getUniqueID())) {
				write(speedrunner.world.getDimensionKey(),
						speedrunner.getPosition(),
						itemStack.getOrCreateTag());
			}
		}
		
		private void write(RegistryKey<World> lodestoneDimension, BlockPos lodestonePos, CompoundNBT nbt) {
			nbt.put("LodestonePos", NBTUtil.writeBlockPos(lodestonePos));
			World.CODEC.encodeStart(NBTDynamicOps.INSTANCE, lodestoneDimension).resultOrPartial(Challenges.LOGGER::error).ifPresent(
					(p_234668_1_) -> nbt.put("LodestoneDimension", p_234668_1_)
			);
			nbt.putBoolean("LodestoneTracked", true);
		}
	}
	public static void registerEvents() {
		IEventBus bus = MinecraftForge.EVENT_BUS;
		//MinecraftForge.EVENT_BUS.register(new ServerTickHandler());
		bus.register(new PlayerTickHandler());
		bus.register(new RespawnHandler());
		bus.register(new ItemUseHandler());
	}
	
}
