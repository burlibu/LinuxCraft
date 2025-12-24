package com.linuxcraft.block.entity;

import com.linuxcraft.LinuxCraft;
import com.linuxcraft.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, LinuxCraft.MOD_ID);

    public static final RegistryObject<BlockEntityType<ComputerBlockEntity>> COMPUTER_BE =
            BLOCK_ENTITIES.register("computer_be", () ->
                    BlockEntityType.Builder.of(ComputerBlockEntity::new,
                            ModBlocks.COMPUTER_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
