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
        
        // Aligned to (2,2) pixels from Top-Left
        // Top-Left is (-0.5, 0.5). +2 pixels X (0.125), -2 pixels Y (0.125)
        pPoseStack.translate(-0.375, 0.375, 0); 
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

        // Blinking Cursor
        long time = java.lang.System.currentTimeMillis();
        if (time % 1000 < 500) {
            int cx = pBlockEntity.getCursorX();
            int cy = pBlockEntity.getCursorY();
            if (cx >= 0 && cx < width && cy >= 0 && cy < ComputerBlockEntity.HEIGHT) {
                // Draw cursor at cx, cy
                this.font.drawInBatch("_", cx * 6, cy * 10, 0xFFFFFFFF, false, pPoseStack.last().pose(), pBufferSource, Font.DisplayMode.NORMAL, 0, pPackedLight);
            }
        }

        pPoseStack.popPose();
    }
}
