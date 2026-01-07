package io.github.candycalc.mixin.client;

import io.github.candycalc.MMOItemsSpell;
import io.github.candycalc.MagicusHackClientClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector3i;
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
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        for (String cdItemId : MagicusHackClientClient.cooldowns.keySet()) {
            if (cdItemId.equals(MagicusHackClientClient.getOraxenId(stack))) {
                for (MMOItemsSpell spell : MagicusHackClientClient.cooldowns.get(cdItemId)) {
                    double cd = spell.getCooldownPercent();
                    Vector3i color = switch (spell.getMode()) {
                        case "SHIFT_RIGHT_CLICK" -> new Vector3i(0, 242, 0);
                        case "RIGHT_CLICK" -> new Vector3i(168, 215, 0);
                        case "SHIFT_LEFT_CLICK" -> new Vector3i(255, 163, 0);
                        case "ATTACK" -> new Vector3i(255, 0, 0);
                        default -> new Vector3i(255, 255, 255);
                    };
                    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getGuiOverlay());
                    vertexConsumer.vertex(matrix4f, x, y, 1000).color(color.x, color.y, color.z, 50);
                    vertexConsumer.vertex(matrix4f, x, y + 16 * (1 - (float) cd), 1000).color(color.x, color.y, color.z, 170);
                    vertexConsumer.vertex(matrix4f, x + 16, y + 16 * (1 - (float) cd), 1000).color(color.x, color.y, color.z, 170);
                    vertexConsumer.vertex(matrix4f, x + 16, y, 1000).color(color.x, color.y, color.z, 50);
                }
            }
        }

    }
}
