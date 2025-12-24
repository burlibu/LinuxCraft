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
    private final RealFileMount diskMount;
    
    public VirtualFileSystem() {
        super(null); // We handle mounts ourselves
        this.rootMount = new RamMount();
        this.romMount = new RomMount();
        this.diskMount = new RealFileMount(new java.io.File("linuxcraft_disk")); // Relative to run dir
        
        // mounts.put("rom", romMount); // Handled by getter logic currently
        
        // Initial files
        rootMount.writeFile("readme.txt", "Welcome to LinuxCraft!".getBytes());
    }

    private IMount getMount(String path) {
        if (path.equals("rom") || path.startsWith("rom/")) {
            return romMount;
        }
        if (path.equals("disk") || path.startsWith("disk/")) {
            return diskMount;
        }
        return rootMount;
    }
    
    private String getMountPath(String path) {
        if (path.equals("rom")) return "";
        if (path.startsWith("rom/")) return path.substring(4);
        if (path.equals("disk")) return "";
        if (path.startsWith("disk/")) return path.substring(5);
        return path;
    }

    @Override
    public boolean exists(String path) {
        path = cleanPath(path);
        if (path.equals("rom") || path.equals("disk")) return true; 
        return getMount(path).exists(getMountPath(path));
    }

    @Override
    public boolean isDirectory(String path) {
        path = cleanPath(path);
        if (path.equals("rom") || path.equals("disk")) return true;
        return getMount(path).isDirectory(getMountPath(path));
    }

    @Override
    public List<String> list(String path) throws IOException {
        path = cleanPath(path);
        List<String> results = getMount(path).list(getMountPath(path));
        
        if (path.isEmpty()) {
            List<String> combined = new ArrayList<>(results);
            if (!combined.contains("rom")) combined.add("rom");
            if (!combined.contains("disk")) combined.add("disk");
            return combined;
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
        // Only allow writing to Ram or Disk
        // Rom is read only
        if (mount instanceof RamMount ram) {
            ram.makeDirectory(getMountPath(path));
        } else if (mount instanceof RealFileMount) {
            // Real file mount doesn't have makeDirectory exposed in IMount interface yet?
            // Oops, IMount doesn't have write methods. We casted to RamMount before.
            // Let's postpone writing to disk for a second or update IMount. 
            // For now just error if not ram.
            throw new IOException("Cannot write to this mount (Implementation Pending)");
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
