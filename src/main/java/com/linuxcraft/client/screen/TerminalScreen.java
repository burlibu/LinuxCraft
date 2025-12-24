package com.linuxcraft.client.screen;

import com.linuxcraft.LinuxCraft;
import com.linuxcraft.block.entity.ComputerBlockEntity;
import com.linuxcraft.world.inventory.TerminalMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TerminalScreen extends AbstractContainerScreen<TerminalMenu> {


    public TerminalScreen(TerminalMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        // Size of the GUI window
        this.imageWidth = 176;
        this.imageHeight = 222; 
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        // Do not render "Inventory" or title labels
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        
        // Full screen background for immersive feel? Or just the window.
        // Let's make the window look like a Monitor.
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Dark Bezel
        guiGraphics.fill(x - 5, y - 5, x + imageWidth + 5, y + 150, 0xFF222222); 
        
        // Black Screen Area
        int termX = x + 8;
        int termY = y + 8;
        int termW = imageWidth - 16;
        int termH = 135;
        guiGraphics.fill(termX, termY, termX + termW, termY + termH, 0xFF000000); 
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
        
        // Render Terminal Text
        renderTerminalText(guiGraphics);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Send key to server
        // 256 is Escape, let it close GUI
        if (keyCode == 256) return super.keyPressed(keyCode, scanCode, modifiers);
        
        com.linuxcraft.network.ModMessages.sendToServer(new com.linuxcraft.network.PacketStoreKeyDown(
            this.menu.blockEntity.getBlockPos(), keyCode, scanCode, modifiers, (char)0));
            
        return true; 
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        com.linuxcraft.network.ModMessages.sendToServer(new com.linuxcraft.network.PacketStoreKeyDown(
            this.menu.blockEntity.getBlockPos(), 0, 0, modifiers, codePoint));
        return true;
    }
    
    private void renderTerminalText(GuiGraphics gfx) {
        char[] buffer = this.menu.blockEntity.getBuffer();
        int width = ComputerBlockEntity.WIDTH;
        int height = ComputerBlockEntity.HEIGHT;
        
        int startX = (this.width - this.imageWidth) / 2 + 10;
        int startY = (this.height - this.imageHeight) / 2 + 10;
        
        // Font line height is 9, usually give 10 spacing
        for(int y=0; y<height; y++) {
            StringBuilder line = new StringBuilder();
            for(int x=0; x<width; x++) {
                line.append(buffer[y*width + x]);
            }
            // Draw text small? Or clip?
            // With standard font, 51 chars is huge. We might need a smaller scale or just show part.
            // CC uses specific font. We'll use scale 0.5 for now to fit.
            gfx.pose().pushPose();
            gfx.pose().translate(startX, startY + (y * 6), 0);
            gfx.pose().scale(0.5f, 0.5f, 1.0f);
            gfx.drawString(this.font, line.toString(), 0, 0, 0xFFFFFFFF);
            gfx.pose().popPose();
        }
    }
}
