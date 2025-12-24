package com.linuxcraft.network;

import com.linuxcraft.block.entity.ComputerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketStoreKeyDown {
    private final BlockPos pos;
    private final int keyCode;
    private final int scanCode;
    private final int modifiers;
    private final char typedChar;

    public PacketStoreKeyDown(BlockPos pos, int keyCode, int scanCode, int modifiers, char typedChar) {
        this.pos = pos;
        this.keyCode = keyCode;
        this.scanCode = scanCode;
        this.modifiers = modifiers;
        this.typedChar = typedChar;
    }

    public PacketStoreKeyDown(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.keyCode = buf.readInt();
        this.scanCode = buf.readInt();
        this.modifiers = buf.readInt();
        this.typedChar = buf.readChar();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(keyCode);
        buf.writeInt(scanCode);
        buf.writeInt(modifiers);
        buf.writeChar(typedChar);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                BlockEntity entity = player.level().getBlockEntity(pos);
                if (entity instanceof ComputerBlockEntity) {
                    ((ComputerBlockEntity) entity).handleInput(keyCode, scanCode, modifiers, typedChar);
                }
            }
        });
        return true;
    }
}
