package com.linuxcraft.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import com.linuxcraft.world.inventory.TerminalMenu;
import org.jetbrains.annotations.Nullable;

public class ComputerBlockEntity extends BlockEntity implements MenuProvider {


    // 51 columns, 19 rows (Standard CC size)
    public static final int WIDTH = 51;
    public static final int HEIGHT = 19;
    
    // Simple buffer: char + byte color (packed)? For now just chars.
    private char[] screenBuffer = new char[WIDTH * HEIGHT];


    public ComputerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.COMPUTER_BE.get(), pPos, pBlockState);
        // Fill with space
        for(int i=0; i<screenBuffer.length; i++) screenBuffer[i] = ' ';
        // Debug text
        writeStr(0, 0, "LinuxCraft BIOS v0.0.1");
        writeStr(0, 1, "Initializing WASM Core...");
    }

    public void tick() {
        // Will handle Wasm simulation here
    }
    
    public void writeChar(int x, int y, char c) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            screenBuffer[y * WIDTH + x] = c;
        }
    }
    
    public void writeStr(int x, int y, String s) {
        for (int i = 0; i < s.length(); i++) {
            writeChar(x + i, y, s.charAt(i));
        }
    }

    public char[] getBuffer() {
        return screenBuffer;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("LinuxCraft Terminal");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new TerminalMenu(pContainerId, pPlayerInventory, this, new net.minecraft.world.inventory.SimpleContainerData(2));
    }

    // Cursor position
    private int cursorX = 0;
    private int cursorY = 2; // Start below debug text

    public void handleInput(int keyCode, int scanCode, int modifiers, char typedChar) {
        // Basic Echo Implementation
        // 257 = Enter ? In GLFW
        if (keyCode == 257) { // Enter
            cursorX = 0;
            cursorY++;
            if (cursorY >= HEIGHT) cursorY = HEIGHT - 1; // Scroll? Later.
        } else if (keyCode == 259) { // Backspace
            if (cursorX > 0) {
                cursorX--;
                writeChar(cursorX, cursorY, ' ');
            }
        } else {
            // Printable char
            if (typedChar >= 32 && typedChar < 127) {
                 writeChar(cursorX, cursorY, typedChar);
                 cursorX++;
                 if (cursorX >= WIDTH) {
                     cursorX = 0;
                     cursorY++;
                 }
            }
        }
        
        // Mark block for update so client sees it
        setChanged();     
        if (level != null) {
            level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
    
    @Override
    public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public net.minecraft.nbt.CompoundTag getUpdateTag() {
        net.minecraft.nbt.CompoundTag tag = super.getUpdateTag();
        // Pack buffer into tag (Naive compression)
        tag.putString("ScreenData", new String(screenBuffer));
        return tag;
    }
    
    @Override
    public void load(net.minecraft.nbt.CompoundTag tag) {
        super.load(tag);
        if (tag.contains("ScreenData")) {
             String s = tag.getString("ScreenData");
             if (s.length() == screenBuffer.length) {
                 screenBuffer = s.toCharArray();
             }
        }
    }
}
