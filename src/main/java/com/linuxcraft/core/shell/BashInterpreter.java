package com.linuxcraft.core.shell;

import com.linuxcraft.block.entity.ComputerBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class BashInterpreter {
    private final ComputerBlockEntity computer;
    private StringBuilder currentLine = new StringBuilder();
    private List<String> history = new ArrayList<>();
    private String currentDirectory = "/";

    public BashInterpreter(ComputerBlockEntity computer) {
        this.computer = computer;
        printPrompt();
    }

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
                computer.writeLine("bin  home  usr  var");
                break;
            case "cd":
                 if (parts.length > 1) {
                     if(parts[1].equals("..")) {
                         currentDirectory = "/"; // Mock parent
                     } else {
                         currentDirectory = "/" + parts[1]; // Mock cd
                     }
                 } else {
                     currentDirectory = "/";
                 }
                 break;
            default:
                computer.writeLine("bash: " + cmd + ": command not found");
                break;
        }
    }
}
