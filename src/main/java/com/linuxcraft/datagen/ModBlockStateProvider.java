package com.linuxcraft.datagen;

import com.linuxcraft.LinuxCraft;
import com.linuxcraft.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;


public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, LinuxCraft.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(ModBlocks.COMPUTER_BLOCK.get(), cubeAll(ModBlocks.COMPUTER_BLOCK.get()));
    }
}
