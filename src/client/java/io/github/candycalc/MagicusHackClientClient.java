package io.github.candycalc;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.*;

public class MagicusHackClientClient implements ClientModInitializer {
	public static HashMap<String, List<MMOItemsSpell>> cooldowns = new HashMap<>();


	public static String getOraxenId(ItemStack stack) {
		try {
			return stack.getComponents().get(DataComponentTypes.CUSTOM_DATA).copyNbt().getCompound("PublicBukkitValues").getString("oraxen:id");
		} catch (NullPointerException e) {
//			LogUtils.getLogger().warn(e.toString());
		}
		return stack.getItem().toString();
	}

	private static void setCooldown(String itemId, MMOItemsSpell spell) {
		List<MMOItemsSpell> thing = cooldowns.getOrDefault(itemId, new ArrayList<>());
        thing.remove(spell);
		thing.add(spell);
		cooldowns.put(itemId, thing);
	}

	public static double getCooldown(String itemId) {
		double cooldownPercent = 0d;
		if (cooldowns.containsKey(itemId)) {
			for (MMOItemsSpell spell : cooldowns.get(itemId)) {
				cooldownPercent = Math.max(cooldownPercent, spell.getCooldownPercent());
			}
			return cooldownPercent;
		}
		return 1d;
	}

	public static void castMMOSpell(String mode) {
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null) {
			for (ItemStack stack : player.getHandItems()) {
				List<MMOItemsSpell> spells = MMOItemsSpell.getSpells(stack);
				for (MMOItemsSpell spell : spells) {
					if (spell.getMode().equals(mode) && getCooldown(getOraxenId(stack)) > 0.99999d) {
						setCooldown(
								getOraxenId(stack),
								spell
						);
					}
				}
			}
		}
	}

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}