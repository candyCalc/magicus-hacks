package io.github.candycalc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;


public class MMOItemsSpell {
    private String id;
    private String mode;
    private double cooldown;
    private long lastCastTimestamp;

    public MMOItemsSpell(String abilityId, String abilityMode, double abilityCooldown) {
        id = abilityId;
        mode = abilityMode;
        cooldown = abilityCooldown;
        lastCastTimestamp = MinecraftClient.getInstance().world != null ? MinecraftClient.getInstance().world.getTime() : 0L;
    }

    public String getId() {
        return id;
    }

    public String getMode() {
        return mode;
    }

    public double getCooldown() {
        return cooldown;
    }

    public Double getCooldownPercent() {
        double cooldownPercent = 0d;
        if (MinecraftClient.getInstance().world != null) {
            cooldownPercent = Math.max(cooldownPercent, (MinecraftClient.getInstance().world.getTime() - lastCastTimestamp) / (20 * cooldown));
            return Math.min(1d, Math.max(0d, cooldownPercent));
        }
        return 1d;
    }

    public static List<MMOItemsSpell> getSpells(ItemStack stack) {
        List<MMOItemsSpell> out = new ArrayList<>();

        NbtCompound customNbt = stack.getComponents().getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        if (customNbt.contains("MMOITEMS_ABILITY")) try {
            JsonArray mmoAbilities = JsonParser.parseString(customNbt.get("MMOITEMS_ABILITY").asString()).getAsJsonArray();
            for (JsonElement ability : mmoAbilities) {
                JsonObject abilityObj = ability.getAsJsonObject();
                out.add(new MMOItemsSpell(
                        abilityObj.get("Id").getAsString(),
                        abilityObj.get("CastMode").getAsString(),
                        abilityObj.get("Modifiers").getAsJsonObject().get("cooldown").getAsLong()
                        ));
            }
        } catch (NullPointerException e) {
            //LogUtils.getLogger().warn(e.toString());
        }
        return out;
    }

    @Override
    public String toString() {
        return String.format("id: %s, cooldown: %d%%", id, (int) (100 * getCooldownPercent()));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MMOItemsSpell) {
            return id.equals(((MMOItemsSpell) o).id);
        }
        return false;
    }
}
