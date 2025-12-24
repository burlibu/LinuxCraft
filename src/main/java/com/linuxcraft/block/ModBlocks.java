package com.linuxcraft.block;

import com.linuxcraft.LinuxCraft;
import com.linuxcraft.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LinuxCraft.MOD_ID);

    public static final DeferredBlock<Block> COMPUTER_BLOCK = registerBlock("computer",
            () -> new ComputerBlock(BlockBehaviour.Properties.of()
                    .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                    .strength(5.0F, 6.0F)
                    .sound(net.minecraft.world.level.block.SoundType.METAL)
                    .noOcclusion()
                    .isValidSpawn((bs, l, p, et) -> false)
                    .isRedstoneConductor((bs, l, p) -> false)
                    .isSuffocating((bs, l, p) -> false)
                    .isViewBlocking((bs, l, p) -> false)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
