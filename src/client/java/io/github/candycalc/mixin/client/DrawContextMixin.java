package io.github.candycalc.mixin.client;

import io.github.candycalc.MMOItemsSpell;
import io.github.candycalc.MagicusHackClientClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Shadow public abstract void draw();

    @Shadow @Final private MatrixStack matrices;

    @Shadow @Final private VertexConsumerProvider.Immediate vertexConsumers;

    @Inject(method = "drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawCooldownProgress(Lnet/minecraft/item/ItemStack;II)V"))
    private void renderCooldown(TextRenderer textRenderer, ItemStack stack, int x, int y, String stackCountText, CallbackInfo ci) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getGui());
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        double cd = 1d;
        for (String cdItemId : MagicusHackClientClient.cooldowns.keySet()) {
            if (cdItemId.equals(MagicusHackClientClient.getOraxenId(stack))) {
                for (MMOItemsSpell spell : MagicusHackClientClient.cooldowns.get(cdItemId)) {
                    if (cd > spell.getCooldownPercent()) {
                        cd = spell.getCooldownPercent();
                    }
                }
            }
        }
        vertexConsumer.vertex(matrix4f, x, y, 200).color(Integer.MAX_VALUE);
        vertexConsumer.vertex(matrix4f, x, y + 16 * (1 - (float) cd), 200).color(Integer.MAX_VALUE);
        vertexConsumer.vertex(matrix4f, x + 16, y + 16 * (1 - (float) cd), 200).color(Integer.MAX_VALUE);
        vertexConsumer.vertex(matrix4f, x + 16, y, 200).color(Integer.MAX_VALUE);
    }
}
