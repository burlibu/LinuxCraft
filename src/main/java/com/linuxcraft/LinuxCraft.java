package com.linuxcraft;

import com.linuxcraft.block.ModBlocks;
import com.linuxcraft.item.ModItems;
import com.linuxcraft.item.ModCreativeModeTab;
import com.linuxcraft.block.entity.ModBlockEntities;
import com.linuxcraft.world.inventory.ModMenuTypes;
import com.linuxcraft.network.ModMessages;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.common.EventBusSubscriber;
import org.slf4j.Logger;

@Mod(LinuxCraft.MOD_ID)
public class LinuxCraft {
    public static final String MOD_ID = "linuxcraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LinuxCraft(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Registers to the mod event bus so blocks get registered
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeModeTab.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModMessages.register(); // This might need to be on mod bus via event
    }



// ...

    // Client setup is now best done in a separate class or via @EventBusSubscriber
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = net.neoforged.api.distmarker.Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
             event.register(ModMenuTypes.TERMINAL_MENU.get(), com.linuxcraft.client.screen.TerminalScreen::new);
        }
        
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
             net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
                 ModBlockEntities.COMPUTER_BE.get(),
                 com.linuxcraft.block.entity.renderer.ComputerBlockRenderer::new
             );
             event.enqueueWork(() -> {
                 net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(ModBlocks.COMPUTER_BLOCK.get(), net.minecraft.client.renderer.RenderType.cutout());
             });
        }
    }
}
