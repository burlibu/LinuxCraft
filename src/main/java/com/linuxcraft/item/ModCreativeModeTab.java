package com.linuxcraft.item;

import com.linuxcraft.LinuxCraft;
import com.linuxcraft.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeModeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LinuxCraft.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> LINUX_TAB = CREATIVE_MODE_TABS.register("linux_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.COMPUTER_BLOCK.get()))
                    .title(Component.translatable("creativetab.linux_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModBlocks.COMPUTER_BLOCK.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
