package io.github.candycalc;

import com.mojang.logging.LogUtils;
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

	public static double getCooldown(String itemId, String spellId) {
		if (cooldowns.containsKey(itemId)) {
			for (MMOItemsSpell spell : cooldowns.get(itemId)) {
				if (spell.getId().equals(spellId)) {
					return spell.getCooldownPercent();
				}
			}
		}
		return 1d;
	}

	public static void castMMOSpell(String mode) {
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null) {
			for (ItemStack stack : player.getHandItems()) {
				for (MMOItemsSpell spell : MMOItemsSpell.getSpells(stack)) {
					if (spell.getMode().equals(mode) && getCooldown(getOraxenId(stack), spell.getId()) > 0.99999d) {
						setCooldown(
								getOraxenId(stack),
								spell
						);
						LogUtils.getLogger().info("spell {} was cast using {}", spell.getId(), spell.getMode());
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