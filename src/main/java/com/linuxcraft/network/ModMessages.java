package com.linuxcraft.network;

import com.linuxcraft.LinuxCraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = LinuxCraft.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModMessages {

    public static void register() {
         // No-op, registration happens via event
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                PacketStoreKeyDown.TYPE,
                PacketStoreKeyDown.STREAM_CODEC,
                PacketStoreKeyDown::handle
        );
    }
}
