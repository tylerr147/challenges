package com.tyler.manhunt.challenges.util;

import com.tyler.manhunt.challenges.Challenges;
import com.tyler.manhunt.challenges.Manhunt;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;

public class CompassUtil {
	
	public static void updateCompass(ServerPlayerEntity hunter, ServerPlayerEntity speedrunner) {
		if (speedrunner == null) return;
		if (Challenges.getStatus(Challenges.Status.Label.CROUCH_NO_TRACKED) && speedrunner.isSneaking()) return;
		CompassUtil.trackPlayer(hunter, speedrunner, false);
	}
	
	public static void setCompassTrack(ItemStack compass, ServerPlayerEntity trackedPlayer) {
		CompoundNBT playerCoordTag = compass.getOrCreateTag();
		
		playerCoordTag.put("LodestonePos", NBTUtil.writeBlockPos(trackedPlayer.getPosition()));
		
		World.CODEC.encodeStart(NBTDynamicOps.INSTANCE, trackedPlayer.world.getDimensionKey()).result()
				.ifPresent(tag -> playerCoordTag.put("LodestoneDimension", tag));
		
		playerCoordTag.putBoolean("LodestoneTracked", false);
		playerCoordTag.putUniqueId("TrackingPlayer", trackedPlayer.getUniqueID());
		
		StringTextComponent name = new StringTextComponent(
				"Tracker Compass (" + trackedPlayer.getDisplayName().getString() + ")");
		compass.setDisplayName(name);
		
		compass.setTag(playerCoordTag);
	}
	
	public static void trackPlayer(ServerPlayerEntity player, ServerPlayerEntity trackedPlayer, boolean giveCompass) {
		boolean updatingCompass = true;
		ItemStack trackingCompass = getCompass(player);
		
		if (trackingCompass == null) {
			trackingCompass = new ItemStack(Items.COMPASS);
			updatingCompass = false;
		}
		
		setCompassTrack(trackingCompass, trackedPlayer);
		
		if (giveCompass) {
			StringTextComponent trackingMessage = new StringTextComponent(
					"Now tracking: " + trackedPlayer.getName().getString());
			player.sendMessage(trackingMessage, player.getUniqueID());
			
			if (!updatingCompass) {
				givePlayerItems(player, trackingCompass);
			}
		}
	}
	
	public static void givePlayerItems(ServerPlayerEntity player, ItemStack items) {
		if (!player.addItemStackToInventory(items)) {
			// Drop compass on the ground if the player doesn't have inventory space for it
			player.dropItem(items, false);
		}
	}
	
	public static ArrayList<ItemStack> fullPlayerInventory(ServerPlayerEntity player) {
		ArrayList<ItemStack> allInventories = new ArrayList<>();
		allInventories.addAll(player.inventory.mainInventory);
		allInventories.addAll(player.inventory.offHandInventory);
		
		return allInventories;
	}
	
	public static ItemStack getCompass(ServerPlayerEntity player) {
		for (ItemStack item : fullPlayerInventory(player)) {
			if (item.getItem().equals(Items.COMPASS)) {
				return item;
			}
		}
		
		return null;
	}
}
