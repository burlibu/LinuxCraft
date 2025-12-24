package com.linuxcraft.core.filesystem;

import java.io.IOException;
import java.io.InputStream;

import java.util.List;

public class FileSystem {
    // For now, simple single mount support or basic mapping
    // We will just use one "Root" mount for simplicity in v1, or a list.
    
    private final IMount rootMount;

    public FileSystem(IMount rootMount) {
        this.rootMount = rootMount;
    }

    private String cleanPath(String path) {
        if (path.startsWith("/")) path = path.substring(1);
        if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
        if (path.isEmpty()) return "";
        return path;
    }

    public boolean exists(String path) {
        return rootMount.exists(cleanPath(path));
    }

    public boolean isDirectory(String path) {
        return rootMount.isDirectory(cleanPath(path));
    }

    public List<String> list(String path) throws IOException {
        return rootMount.list(cleanPath(path));
    }

    public InputStream openForRead(String path) throws IOException {
        return rootMount.openForRead(cleanPath(path));
    }
}
