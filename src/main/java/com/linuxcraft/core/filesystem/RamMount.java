package com.linuxcraft.core.filesystem;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class RamMount implements IMount {

    private final Map<String, byte[]> fileContent = new HashMap<>();
    private final Set<String> directories = new HashSet<>();

    public RamMount() {
        directories.add(""); // Root
    }

    @Override
    public boolean exists(String path) {
        return fileContent.containsKey(path) || directories.contains(path);
    }

    @Override
    public boolean isDirectory(String path) {
        return directories.contains(path);
    }

    @Override
    public List<String> list(String path) throws IOException {
        if (!isDirectory(path)) throw new IOException("Not a directory");
        
        List<String> result = new ArrayList<>();
        // Simple iteration (inefficient but works for small FS)
        // Find direct children
        for (String f : fileContent.keySet()) {
            if (getParent(f).equals(path)) {
                result.add(getFileName(f));
            }
        }
        for (String d : directories) {
            if (!d.equals(path) && getParent(d).equals(path)) {
                result.add(getFileName(d));
            }
        }
        return result;
    }

    @Override
    public long getSize(String path) throws IOException {
        if (fileContent.containsKey(path)) return fileContent.get(path).length;
        return 0;
    }

    @Override
    public InputStream openForRead(String path) throws IOException {
        if (!fileContent.containsKey(path)) throw new IOException("File not found");
        return new ByteArrayInputStream(fileContent.get(path));
    }

    // Write methods
    public void writeFile(String path, byte[] data) {
        fileContent.put(path, data);
        // Ensure parent dirs exist?
    }

    public void makeDirectory(String path) {
        directories.add(path);
    }

    // Helpers
    private String getParent(String path) {
        if (path.isEmpty()) return null;
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash == -1) return "";
        return path.substring(0, lastSlash);
    }

    private String getFileName(String path) {
        int lastSlash = path.lastIndexOf('/');
        return path.substring(lastSlash + 1);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag dirList = new ListTag();
        for(String d : directories) dirList.add(StringTag.valueOf(d));
        tag.put("dirs", dirList);

        CompoundTag filesTag = new CompoundTag();
        for(Map.Entry<String, byte[]> e : fileContent.entrySet()) {
            filesTag.putByteArray(e.getKey(), e.getValue());
        }
        tag.put("files", filesTag);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        directories.clear();
        fileContent.clear();
        
        if (tag.contains("dirs")) {
            ListTag dirList = tag.getList("dirs", Tag.TAG_STRING);
            for(int i=0; i<dirList.size(); i++) directories.add(dirList.getString(i));
        } else {
            directories.add("");
        }

        if (tag.contains("files")) {
            CompoundTag filesTag = tag.getCompound("files");
            for(String key : filesTag.getAllKeys()) {
                fileContent.put(key, filesTag.getByteArray(key));
            }
        }
    }
}
