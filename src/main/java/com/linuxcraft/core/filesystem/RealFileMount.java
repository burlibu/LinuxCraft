package com.linuxcraft.core.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RealFileMount implements IMount {
    private final File rootDir;

    public RealFileMount(File rootDir) {
        this.rootDir = rootDir;
        if (!this.rootDir.exists()) {
            this.rootDir.mkdirs();
        }
    }

    private File resolve(String path) {
        // Prevent path traversal
        if (path.contains("..")) throw new IllegalArgumentException("Invalid path");
        return new File(rootDir, path);
    }

    @Override
    public boolean exists(String path) {
        return resolve(path).exists();
    }

    @Override
    public boolean isDirectory(String path) {
        return resolve(path).isDirectory();
    }

    @Override
    public List<String> list(String path) throws IOException {
        File f = resolve(path);
        String[] files = f.list();
        if (files == null) return new ArrayList<>();
        return List.of(files);
    }

    @Override
    public long getSize(String path) throws IOException {
        return resolve(path).length();
    }

    @Override
    public InputStream openForRead(String path) throws IOException {
        return new FileInputStream(resolve(path));
    }
}
