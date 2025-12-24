package com.linuxcraft.core.filesystem;

import net.minecraft.nbt.CompoundTag;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

public class VirtualFileSystem extends FileSystem {
    
    private final RamMount rootMount;
    private final RomMount romMount;
    private final java.util.Map<String, IMount> mounts = new java.util.HashMap<>();
    
    public VirtualFileSystem() {
        super(null); // We handle mounts ourselves
        this.rootMount = new RamMount();
        this.romMount = new RomMount();
        
        mounts.put("rom", romMount);
        
        // Initial files
        rootMount.writeFile("readme.txt", "Welcome to LinuxCraft!".getBytes());
    }

    private IMount getMount(String path) {
        // Simple check: if starts with "rom", use romMount
        // In real impl, we should match longest prefix
        if (path.equals("rom") || path.startsWith("rom/")) {
            return romMount;
        }
        return rootMount;
    }
    
    private String getMountPath(String path) {
        if (path.equals("rom")) return "";
        if (path.startsWith("rom/")) return path.substring(4);
        return path;
    }

    @Override
    public boolean exists(String path) {
        path = cleanPath(path);
        if (path.equals("rom")) return true; // It's a mount point
        return getMount(path).exists(getMountPath(path));
    }

    @Override
    public boolean isDirectory(String path) {
        path = cleanPath(path);
        if (path.equals("rom")) return true;
        return getMount(path).isDirectory(getMountPath(path));
    }

    @Override
    public List<String> list(String path) throws IOException {
        path = cleanPath(path);
        List<String> results = getMount(path).list(getMountPath(path));
        
        // If we are at root, we must add "rom" manually if it's not a real folder in rootMount
        if (path.isEmpty()) {
            if (!results.contains("rom")) {
                List<String> combined = new ArrayList<>(results);
                combined.add("rom");
                return combined;
            }
        }
        return results;
    }

    @Override
    public InputStream openForRead(String path) throws IOException {
        path = cleanPath(path);
        return getMount(path).openForRead(getMountPath(path));
    }
    
    @Override
    public void makeDirectory(String path) throws IOException {
        path = cleanPath(path);
        IMount mount = getMount(path);
        if (mount instanceof RamMount ram) {
            ram.makeDirectory(getMountPath(path));
        } else {
            throw new IOException("Cannot write to read-only mount");
        }
    }
    
    @Override
    public CompoundTag serializeNBT() {
        return rootMount.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        rootMount.deserializeNBT(tag);
    }
    
    private String cleanPath(String path) {
        if (path.startsWith("/")) path = path.substring(1);
        if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
        return path;
    }
}
