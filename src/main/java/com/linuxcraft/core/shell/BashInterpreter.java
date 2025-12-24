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

    public void handleInput(String input) {
        if (input.length() > 0) {
             // If input is \n or \r, treat as enter
             char c = input.charAt(0);
             if (c == '\n' || c == '\r') {
                 handleKey(257); // Enter key code
             } else {
                 handleChar(c);
             }
        }
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
                case "mkdir":
                     if (parts.length > 1) {
                         String newPath = resolvePath(parts[1]);
                         if (fs.exists(newPath)) {
                             computer.writeLine("mkdir: cannot create directory '" + parts[1] + "': File exists");
                         } else {
                             fs.makeDirectory(newPath);
                         }
                     } else {
                         computer.writeLine("mkdir: missing operand");
                     }
                     break;
                case "cd":
                     if (parts.length > 1) {
                         String newPath = resolvePath(parts[1]);
                         if (fs.isDirectory(newPath)) {
                             this.currentDirectory = newPath;
                             this.currentDirectory = this.currentDirectory.endsWith("/") ? this.currentDirectory : this.currentDirectory + "/";
                         } else {
                             computer.writeLine("bash: cd: " + parts[1] + ": Not a directory");
                         }
                     } else {
                         // cd home or root?
                         this.currentDirectory = "/";
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

    private String resolvePath(String path) {
        if (path.equals("/")) return "/";
        
        String base = path.startsWith("/") ? "/" : currentDirectory;
        if (!base.endsWith("/")) base += "/";
        
        // Simple resolution
        String fullPath = path.startsWith("/") ? path : base + path;
        
        // Handle .. (hacky)
        // In real impl, split by / and stack
        java.util.Stack<String> stack = new java.util.Stack<>();
        for (String part : fullPath.split("/")) {
            if (part.isEmpty() || part.equals(".")) continue;
            if (part.equals("..")) {
                if (!stack.isEmpty()) stack.pop();
            } else {
                stack.push(part);
            }
        }
        
        if (stack.isEmpty()) return "/";
        return String.join("/", stack);
    }
}
