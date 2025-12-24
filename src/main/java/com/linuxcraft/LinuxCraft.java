package com.linuxcraft;

import com.mojang.logging.LogUtils;
import com.linuxcraft.block.ModBlocks;
import com.linuxcraft.item.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

@Mod(LinuxCraft.MOD_ID)
public class LinuxCraft {
    public static final String MOD_ID = "linuxcraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LinuxCraft() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        modEventBus.addListener(this::commonSetup);
        
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        com.linuxcraft.item.ModCreativeModeTab.register(modEventBus);
        com.linuxcraft.block.entity.ModBlockEntities.register(modEventBus);
        com.linuxcraft.world.inventory.ModMenuTypes.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private void commonSetup(final net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) {
        com.linuxcraft.network.ModMessages.register();
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
             net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
                 com.linuxcraft.block.entity.ModBlockEntities.COMPUTER_BE.get(),
                 com.linuxcraft.block.entity.renderer.ComputerBlockRenderer::new
             );
             net.minecraft.client.gui.screens.MenuScreens.register(
                 com.linuxcraft.world.inventory.ModMenuTypes.TERMINAL_MENU.get(),
                 com.linuxcraft.client.screen.TerminalScreen::new
             );
        }
    }
}
