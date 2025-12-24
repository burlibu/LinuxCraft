package com.linuxcraft.core.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IMount {
    boolean exists(String path);
    boolean isDirectory(String path);
    List<String> list(String path) throws IOException;
    long getSize(String path) throws IOException;
    InputStream openForRead(String path) throws IOException;
}
