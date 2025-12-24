package com.linuxcraft.block.entity.renderer;

import com.linuxcraft.block.ComputerBlock;
import com.linuxcraft.block.entity.ComputerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class ComputerBlockRenderer implements BlockEntityRenderer<ComputerBlockEntity> {
    private final Font font;

    public ComputerBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    @Override
    public void render(ComputerBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        BlockState blockState = pBlockEntity.getBlockState();
        if (!(blockState.getBlock() instanceof ComputerBlock)) return;

        Direction facing = blockState.getValue(ComputerBlock.FACING);
        
        pPoseStack.pushPose();
        
        // Center on block
        pPoseStack.translate(0.5, 0.5, 0.5);
        
        // Rotate to face correct direction
        float yRot = -facing.toYRot();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        
        // Move to face (Front is Z+)
        pPoseStack.translate(0, 0, 0.501); 
        
        // Scale down directly to fit 51 chars in 1 block (16 pixels)
        // 51 chars * 6 width = 306 pixels needed.
        // Block is 16 pixels (1.0 units).
        // Scale = 1.0 / 306 = ~0.003
        // Let's use specific ComputerCraft scaling/margins roughly.
        // Screen area is roughly 12x12 pixels in center? 
        // Let's assume full face for now. 
        // Monitor is 0.8 x 0.6 units roughly.
        
        // Scale down to fit 51 chars (approx 306 pixels width) into ~0.9 block width
        // 306 * scale = 0.9 => scale = 0.0029
        float scale = 0.003f; 
        
        // Center text: 
        // Width = 51 * 6 = 306. 306 * 0.003 = 0.918
        // Height = 19 * 10 = 190. 190 * 0.003 = 0.57
        
        pPoseStack.translate(-0.45, 0.28, 0); // Top-Left of screen (Centered X, slightly up Y)
        pPoseStack.scale(scale, -scale, scale); // Flip Y
        
        char[] buffer = pBlockEntity.getBuffer();
        int width = ComputerBlockEntity.WIDTH;
        
        // Simple Render Loop
        // Note: Drawing generic strings is expensive in loops, optimize later (VertexConsumer)
        for (int y = 0; y < ComputerBlockEntity.HEIGHT; y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < width; x++) {
                line.append(buffer[y * width + x]);
            }
            // Draw White Text (0xFFFFFFFF)
            this.font.drawInBatch(line.toString(), 0, y * 10, 0xFFFFFFFF, false, pPoseStack.last().pose(), pBufferSource, Font.DisplayMode.NORMAL, 0, pPackedLight);
        }

        pPoseStack.popPose();
    }
}
