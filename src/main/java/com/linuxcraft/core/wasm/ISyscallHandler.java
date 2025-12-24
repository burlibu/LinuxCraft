package com.linuxcraft.core.wasm;

import com.dylibso.chicory.runtime.HostFunction;
import java.util.List;

public interface ISyscallHandler {
    List<HostFunction> getFunctions();
}
