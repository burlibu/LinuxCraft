package com.linuxcraft.core.shell;

import com.linuxcraft.block.entity.ComputerBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class BashInterpreter {

    private final com.linuxcraft.core.filesystem.FileSystem fs;
    private final ComputerBlockEntity computer;
    private StringBuilder currentLine = new StringBuilder();
    private List<String> history = new ArrayList<>();
    private String currentDirectory = "/";

    public BashInterpreter(ComputerBlockEntity computer, com.linuxcraft.core.filesystem.FileSystem fs) {
        this.computer = computer;
        this.fs = fs;
        printPrompt();
    }

    // ... handleChar/handleKey ...

    public void handleChar(char c) {
        if (c >= 32 && c < 127) {
            currentLine.append(c);
            computer.writeChar(computer.getCursorX(), computer.getCursorY(), c);
            computer.advanceCursor();
        }
    }

    public void handleKey(int keyCode) {
        // Enter
        if (keyCode == 257) {
            String line = currentLine.toString();
            computer.newLine();
            processCommand(line);
            currentLine.setLength(0);
            printPrompt();
        } 
        // Backspace
        else if (keyCode == 259) {
            if (currentLine.length() > 0) {
                currentLine.deleteCharAt(currentLine.length() - 1);
                computer.backspace();
            }
        }
    }

    private void printPrompt() {
        String prompt = "root@linuxcraft:" + currentDirectory + "$ ";
        computer.writeStr(prompt);
    }

    private void processCommand(String line) {
        line = line.trim();
        if (line.isEmpty()) return;

        String[] parts = line.split("\\s+");
        String cmd = parts[0];

        try {
            switch (cmd) {
                case "help":
                    computer.writeLine("LinuxCraft Bash v1.0");
                    computer.writeLine("Commands: help, clear, ls, cd, echo");
                    break;
                case "clear":
                    computer.clearScreen();
                    break;
                case "echo":
                    if (parts.length > 1) {
                        computer.writeLine(line.substring(5));
                    }
                    break;
                case "ls":
                    List<String> files = fs.list(currentDirectory);
                    if (files.isEmpty()) {
                        // computer.writeLine("(empty)");
                    } else {
                        computer.writeLine(String.join("  ", files));
                    }
                    break;
                case "cd":
                     // Basic mock support for CD as we only have root mount for now
                     if (parts.length > 1) {
                         computer.writeLine("cd: directories not implemented fully yet");
                     }
                     break;
                default:
                    computer.writeLine("bash: " + cmd + ": command not found");
                    break;
            }
        } catch (Exception e) {
            computer.writeLine("Error: " + e.getMessage());
        }
    }
}
