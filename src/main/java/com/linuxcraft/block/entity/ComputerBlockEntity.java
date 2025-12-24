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


    private int cursorX = 0;
    private int cursorY = 0;
    
    private com.linuxcraft.core.filesystem.FileSystem fileSystem;
    private com.linuxcraft.core.shell.BashInterpreter shell;

    public ComputerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.COMPUTER_BE.get(), pPos, pBlockState);
        
        // Init Filesystem
        this.fileSystem = new com.linuxcraft.core.filesystem.FileSystem(new com.linuxcraft.core.filesystem.RomMount());
        
        // Fill with space
        for(int i=0; i<screenBuffer.length; i++) screenBuffer[i] = ' ';
        
        // Debug text
        writeStr(0, 0, "LinuxCraft BIOS v0.0.1");
        writeStr(0, 1, "Mounting ROM...");
        writeStr(0, 2, "Initializing Shell...");
        
        cursorY = 2;
        shell = new com.linuxcraft.core.shell.BashInterpreter(this, this.fileSystem);
        newLine();
    }

    public void handleInput(int keyCode, int scanCode, int modifiers, char typedChar) {
        if (shell != null) {
            if (typedChar != 0) {
                shell.handleChar(typedChar);
            } else {
                shell.handleKey(keyCode);
            }
        }
        
        setChanged();     
        if (level != null) {
            level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // --- Screen API for Shell ---

    public int getCursorX() { return cursorX; }
    public int getCursorY() { return cursorY; }

    public void advanceCursor() {
        cursorX++;
        if (cursorX >= WIDTH) {
            cursorX = 0;
            cursorY++;
            if (cursorY >= HEIGHT) scrollUp();
        }
    }
    
    public void newLine() {
        cursorX = 0;
        cursorY++;
        if (cursorY >= HEIGHT) scrollUp();
    }
    
    public void backspace() {
        if (cursorX > 0) {
            cursorX--;
            writeChar(cursorX, cursorY, ' ');
        } else if (cursorY > 0) {
             // Wrap back to previous line? stick to simple for now
        }
    }
    
    public void writeLine(String s) {
        writeStr(s);
        newLine();
    }
    
    public void writeStr(String s) {
        for (int i = 0; i < s.length(); i++) {
            writeChar(cursorX, cursorY, s.charAt(i));
            advanceCursor();
        }
    }

    public void clearScreen() {
        for(int i=0; i<screenBuffer.length; i++) screenBuffer[i] = ' ';
        cursorX = 0;
        cursorY = 0;
    }

    private void scrollUp() {
        // Shift all lines up by 1
        for (int y = 0; y < HEIGHT - 1; y++) {
            System.arraycopy(screenBuffer, (y + 1) * WIDTH, screenBuffer, y * WIDTH, WIDTH);
        }
        // Clear last line
        for (int x = 0; x < WIDTH; x++) {
            screenBuffer[(HEIGHT - 1) * WIDTH + x] = ' ';
        }
        cursorY = HEIGHT - 1;
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
    @Override
    public Component getDisplayName() {
        return Component.literal("LinuxCraft Terminal");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new TerminalMenu(pContainerId, pPlayerInventory, this, new net.minecraft.world.inventory.SimpleContainerData(2));
    }

    public char[] getBuffer() {
        return screenBuffer;
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
}
