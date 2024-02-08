package me.asleepp.skfinder.search;


import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {
    private String line;
    private int lineNumber;
    private String filePath;

    public SearchResult(String line, int lineNumber, String filePath) {
        this.line = line;
        this.lineNumber = lineNumber;
        this.filePath = filePath;
    }

    public String getLine() {
        return line;
    }

    public List<String> format(){
        List<String> list = new ArrayList<>();
        list.add(ChatColor.GOLD + ChatColor.BOLD.toString() + "Line " + lineNumber + ": " + ChatColor.GRAY + "(" + filePath + ")");
        list.add(ChatColor.GOLD + "    Line: " + ChatColor.GRAY + line);
        return list;
    }
}
