package com.linuxcraft.datagen;

import com.linuxcraft.LinuxCraft;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, LinuxCraft.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Items meant to be generated here
        // simpleItem(ModItems.EXAMPLE_ITEM);
    }
}
