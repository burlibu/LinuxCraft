package com.linuxcraft.core.filesystem;




import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class RomMount implements IMount {
    // We can't easily list "classpath" in Java without a specific list file.
    // So usually we maintain a "Directory Listing" file in JSON or Text.
    // For this prototype, we will hardcode the available files or define them.
    
    // Virtual file system for now
    private final List<String> programs = new ArrayList<>();

    public RomMount() {
        // Built-in programs
        programs.add("hello.wasm");
        programs.add("readme.txt");
    }

    @Override
    public boolean exists(String path) {
        return path.isEmpty() || programs.contains(path);
    }

    @Override
    public boolean isDirectory(String path) {
        return path.isEmpty(); // Root is dir
    }

    @Override
    public List<String> list(String path) throws IOException {
        if (path.isEmpty()) {
            return new ArrayList<>(programs);
        }
        return new ArrayList<>();
    }

    @Override
    public long getSize(String path) throws IOException {
        return 0; // Unknown
    }

    @Override
    public InputStream openForRead(String path) throws IOException {
        // In real mod: Loading from assets/linuxcraft/rom/
        // For prototype: return dummy streams or load real resource if we add one.
        
        // ResourceLocation loc = new ResourceLocation(LinuxCraft.MOD_ID, "rom/" + path);
        // We need a ResourceManager access ideally, but for now let's use ClassLoader
        InputStream is =  getClass().getResourceAsStream("/assets/linuxcraft/rom/" + path);
        
        if (is == null) throw new IOException("File not found: " + path);
        return is;
    }
}
