package io.github.candycalc.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onOpenScreen", at = @At(value = "HEAD"), cancellable = true)
    private void fuckOffEnchantingTableMenu(OpenScreenS2CPacket packet, CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        // If the server tells you to open the enchantment table menu, but you're holding a glass bottle: refuse.
        if (player != null && packet.getScreenHandlerType().equals(ScreenHandlerType.ENCHANTMENT) && player.getInventory().getMainHandStack().isOf(Items.GLASS_BOTTLE)) {
            ci.cancel();
        }
    }
}