package com.linuxcraft.core.wasm;

import java.io.InputStream;

public interface IWasmComputer {
    void loadProgram(InputStream wasmBinary);
    void tick(); // Run a slice of execution
    boolean isRunning();
    
    // Memory Access
    void writeMemory(int address, byte[] data);
    byte[] readMemory(int address, int length);
}
