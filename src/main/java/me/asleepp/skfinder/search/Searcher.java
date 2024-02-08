package me.asleepp.skfinder.search;


import me.asleepp.skfinder.SkFinder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Searcher {
    private final SkFinder plugin = SkFinder.getInstance();
    public final File SKRIPT_DIR = new File(plugin.getDataFolder().getParentFile(), "Skript/scripts");

    private final String query;
    private final boolean caseSensitive;
    private final File baseDir;

    public Searcher(String query, boolean caseSensitive, File baseDir) {
        this.query = query;
        this.caseSensitive = caseSensitive;
        this.baseDir = baseDir;
    }

    public Searcher(String query, boolean caseSensitive) {
        this.query = query;
        this.caseSensitive = caseSensitive;
        this.baseDir = SKRIPT_DIR;
    }

    public boolean dirExists(File dir){
        return dir.exists() && dir.isDirectory();
    }

    public boolean dirExists(){
        return dirExists(baseDir);
    }

    public void search(SearchCallback searchCallback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                List<SearchResult> searchResults = searchDir(baseDir);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    searchCallback.accept(searchResults);
                });
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to Search in folder!" ,e);
            }
        });
    }

    public List<SearchResult> searchFile(File file) throws IOException {
        if (file.isDirectory()) return searchDir(file);
        List<SearchResult> searchResults = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
        int lineNumber = 1;
        String line;
        while ((line = reader.readLine()) != null) {
            if (!caseSensitive) line = line.toLowerCase();
            line = line.trim();
            if (lineMatches(line)) {
                line = line.replace(query, ChatColor.YELLOW + query + ChatColor.GRAY);
                searchResults.add(new SearchResult(line, lineNumber, getRelativePath(file)));
            }
            lineNumber++;
        }
        reader.close();
        return searchResults;
    }

    public String getRelativePath(File file){
        String baseDirPath = baseDir.getPath() + "/";
        String filePath = file.getPath();
        return filePath.replace(baseDirPath, "");
    }

    public List<SearchResult> searchDir(File dir) throws IOException {
        if (dir.isFile()) return searchFile(dir);
        if (!dirExists(dir)){
            throw new IllegalArgumentException(String.format("Folder %s doesn't exists!", dir.getPath()));
        }
        List<SearchResult> searchResults = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files == null){
            return searchResults;
        }
        for (final File file : files) {
            searchResults.addAll(searchFile(file));
        }
        return searchResults;
    }

    public boolean lineMatches(String line){
        return line.contains(query);
    }

    public String getQuery() {
        return query;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public File getBaseDir() {
        return baseDir;
    }
}
