package com.linuxcraft.core.wasm;

import com.dylibso.chicory.runtime.HostFunction;
import com.dylibso.chicory.wasm.types.ValueType;
import com.linuxcraft.block.entity.ComputerBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class LinuxSyscalls implements ISyscallHandler {
    private final ComputerBlockEntity computer;

    public LinuxSyscalls(ComputerBlockEntity computer) {
        this.computer = computer;
    }

    @Override
    public List<HostFunction> getFunctions() {
        List<HostFunction> functions = new ArrayList<>();

        // void terminal_write(int x, int y, char c)
        // env.terminal_write
        functions.add(new HostFunction(
            "env",
            "terminal_write",
            List.of(ValueType.I32, ValueType.I32, ValueType.I32), // Args: x, y, char
            List.of(), // Returns: void
            (instance, args) -> {
                int x = (int) args[0];
                int y = (int) args[1];
                char c = (char) args[2];
                
                computer.writeChar(x, y, c);
                // We should technically schedule this on main thread if we are async,
                // but if we tick on server thread it's fine.
                
                return null;
            }
        ));

        return functions;
    }
}
