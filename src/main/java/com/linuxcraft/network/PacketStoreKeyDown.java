package com.linuxcraft.network;

import com.linuxcraft.LinuxCraft;
import com.linuxcraft.block.entity.ComputerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketStoreKeyDown(BlockPos pos, int keyCode, int scanCode, int modifiers, char typedChar) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketStoreKeyDown> TYPE = new CustomPacketPayload.Type<>(
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(LinuxCraft.MOD_ID, "store_key_down"));

    public static final StreamCodec<FriendlyByteBuf, PacketStoreKeyDown> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, PacketStoreKeyDown::pos,
        net.minecraft.network.codec.ByteBufCodecs.INT, PacketStoreKeyDown::keyCode,
        net.minecraft.network.codec.ByteBufCodecs.INT, PacketStoreKeyDown::scanCode,
        net.minecraft.network.codec.ByteBufCodecs.INT, PacketStoreKeyDown::modifiers,
        net.minecraft.network.codec.ByteBufCodecs.VAR_INT.map(i -> (char) i.intValue(), c -> (int) c), PacketStoreKeyDown::typedChar,
        PacketStoreKeyDown::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PacketStoreKeyDown payload, IPayloadContext context) {
         context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                BlockEntity entity = player.level().getBlockEntity(payload.pos());
                if (entity instanceof ComputerBlockEntity computer) {
                    computer.handleInput(payload.keyCode(), payload.scanCode(), payload.modifiers(), payload.typedChar());
                }
            }
        });
    }
}
