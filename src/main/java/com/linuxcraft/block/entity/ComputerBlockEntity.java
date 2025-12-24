package com.linuxcraft.block.entity;

import com.linuxcraft.block.ModBlocks;
import com.linuxcraft.core.filesystem.VirtualFileSystem;
import com.linuxcraft.core.shell.BashInterpreter;
import com.linuxcraft.world.inventory.TerminalMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class ComputerBlockEntity extends BlockEntity implements MenuProvider {
    
    public static final int WIDTH = 40;
    public static final int HEIGHT = 25;

    private final ItemStackHandler itemHandler = new ItemStackHandler(0) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final VirtualFileSystem fileSystem;
    private final BashInterpreter shell;
    
    // Screen buffer
    private final char[] screenBuffer = new char[WIDTH * HEIGHT];
    private int cursorX = 0;
    private int cursorY = 0;

    public ComputerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.COMPUTER_BE.get(), pPos, pBlockState);
        this.fileSystem = new VirtualFileSystem();
        clearScreen();
        this.shell = new BashInterpreter(this, this.fileSystem); // Shell needs 'this'
    }
    
    public char[] getBuffer() {
        return screenBuffer;
    }
    
    public int getCursorX() { return cursorX; }
    public int getCursorY() { return cursorY; }

    public void writeChar(int x, int y, char c) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            screenBuffer[y * WIDTH + x] = c;
            setChanged();
        }
    }
    
    public void clearScreen() {
        for(int i=0; i<screenBuffer.length; i++) screenBuffer[i] = ' ';
        cursorX = 0;
        cursorY = 0;
        setChanged();
    }
    
    public void writeLine(String text) {
        for (char c : text.toCharArray()) {
            writeChar(cursorX, cursorY, c);
            cursorX++;
            if (cursorX >= WIDTH) {
                cursorX = 0;
                cursorY++;
                if (cursorY >= HEIGHT) cursorY = 0; // Wrap
            }
        }
        newLine();
    }
    
    public void newLine() {
        cursorX = 0;
        cursorY++;
        if (cursorY >= HEIGHT) {
             // Scroll up
             System.arraycopy(screenBuffer, WIDTH, screenBuffer, 0, WIDTH * (HEIGHT - 1));
             for (int i = WIDTH * (HEIGHT - 1); i < WIDTH * HEIGHT; i++) screenBuffer[i] = ' ';
             cursorY = HEIGHT - 1;
        }
        setChanged();
    }
    
    public void backspace() {
        if (cursorX > 0) {
            cursorX--;
            writeChar(cursorX, cursorY, ' ');
        }
    }
    
    public void advanceCursor() {
        cursorX++;
        if (cursorX >= WIDTH) newLine();
    }
    
    public void writeStr(String s) {
        for(char c : s.toCharArray()) {
             writeChar(cursorX, cursorY, c);
             advanceCursor();
        }
    }

    public void drops() {
        // SimpleItemHandler drop logic if needed
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.linuxcraft.computer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new TerminalMenu(pContainerId, pPlayerInventory, this, new SimpleContainerData(2));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, net.minecraft.core.HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("fileSystem", fileSystem.serializeNBT());
        // Serialize screen buffer
        StringBuilder sb = new StringBuilder(screenBuffer.length);
        sb.append(screenBuffer);
        pTag.putString("screenBuffer", sb.toString());
        pTag.putInt("cursorX", cursorX);
        pTag.putInt("cursorY", cursorY);
    }

    @Override
    public void loadAdditional(CompoundTag pTag, net.minecraft.core.HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if(pTag.contains("fileSystem")) {
            fileSystem.deserializeNBT(pTag.getCompound("fileSystem"));
        }
        if (pTag.contains("screenBuffer")) {
            String s = pTag.getString("screenBuffer");
            if (s.length() == screenBuffer.length) {
                System.arraycopy(s.toCharArray(), 0, screenBuffer, 0, screenBuffer.length);
            }
        }
        if (pTag.contains("cursorX")) cursorX = pTag.getInt("cursorX");
        if (pTag.contains("cursorY")) cursorY = pTag.getInt("cursorY");
    }

    @Override
    public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket pkt, net.minecraft.core.HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
        // This is called on client when packet received
        // loadAdditional(pkt.getTag()); // already called by super? No, BaseEntityBlock doesn't auto-call load for packets usually in older versions, but let's check.
        // Actually ClientboundBlockEntityDataPacket uses getUpdateTag usually.
        // We can manually call handleUpdateTag if needed.
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, net.minecraft.core.HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        loadAdditional(tag, lookupProvider);
    }
    
    public static void tick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, ComputerBlockEntity entity) {
        // Tick logic for computer if needed
    }

    public void handleInput(int keyCode, int scanCode, int modifiers, char typedChar) {
        if (typedChar != 0) {
            shell.handleInput(String.valueOf(typedChar));
        } else {
            // Handle special keys via key code if needed
            shell.handleKey(keyCode);
        }
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider pRegistries) {
        CompoundTag tag = super.getUpdateTag(pRegistries);
        saveAdditional(tag, pRegistries);
        return tag;
    }
}
