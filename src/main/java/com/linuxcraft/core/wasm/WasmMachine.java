package com.linuxcraft.core.wasm;

import com.dylibso.chicory.runtime.HostFunction;
import com.dylibso.chicory.runtime.ImportValues;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.wasm.Parser;
import com.dylibso.chicory.wasm.WasmModule;

import java.io.InputStream;
import java.util.List;

public class WasmMachine implements IWasmComputer {
    private Instance instance;
    private final ISyscallHandler syscalls;

    public WasmMachine(ISyscallHandler syscalls) {
        this.syscalls = syscalls;
    }

    @Override
    public void loadProgram(InputStream wasmBinary) {
        try {
            // Load Module
            WasmModule module = Parser.parse(wasmBinary);
            
            // Build Imports
            ImportValues.Builder imports = ImportValues.builder();
            for (HostFunction func : syscalls.getFunctions()) {
                imports.addFunction(func);
            }

            // Instantiate
            this.instance = Instance.builder(module).withImportValues(imports.build()).build();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tick() {
        if (instance != null) {
            // In a real scenario, we might want to run the 'main' or 'loop' export.
            // For now, let's assume the program runs on start or exposes a 'tick' function.
            // We'll call 'update' if it exists.
            try {
                // instance.export("update").apply(); 
            } catch (Exception e) {
                // Ignore if not present
            }
        }
    }

    @Override
    public boolean isRunning() {
        return instance != null;
    }

    @Override
    public void writeMemory(int address, byte[] data) {
        if (instance != null) {
            instance.memory().write(address, data);
        }
    }

    @Override
    public byte[] readMemory(int address, int length) {
        if (instance != null) {
            return instance.memory().readBytes(address, length);
        }
        return new byte[0];
    }
}
