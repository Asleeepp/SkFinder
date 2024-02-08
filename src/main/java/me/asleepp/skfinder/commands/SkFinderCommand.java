package me.asleepp.skfinder.commands;


import me.asleepp.skfinder.SkFinder;
import me.asleepp.skfinder.search.SearchResult;
import me.asleepp.skfinder.search.Searcher;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SkFinderCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("search")) {
                if (args.length > 1) {
                    StringBuilder searchString = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        searchString.append(args[i]).append(" ");
                    }
                    searchFiles(sender, searchString.toString().trim());
                    return true;
                } else {
                    sender.sendMessage("Usage: /" + label + " search <phrase>");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("skfinder.reload")) {
                    SkFinder.getInstance().reloadConfiguration();
                    sender.sendMessage(ChatColor.GREEN + "Configuration reloaded.");
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                }
                return true;
            }
        }

        sender.sendMessage("Usage: /" + label + " <search|reload>");
        return true;
    }
    public void searchFiles(CommandSender sender, String query) {
        searchFiles(sender, 1, query);
    }
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("search");
            completions.add("reload");
        }
        return completions;
    }

    public void searchFiles(CommandSender sender, int page, String query) {
        int resultsPerPage = SkFinder.getResultsPerPage();
        Searcher searcher = new Searcher(query, false);
        if (!searcher.dirExists()) {
            message(sender, ChatColor.RED + String.format("Folder %s doesn't exist!", searcher.getBaseDir().getPath()));
            return;
        }

        try {
            searcher.search(searchResults -> {
                List<SearchResult> filteredResults = searchResults.stream()
                        .filter(result -> result.getLine().contains(query))
                        .collect(Collectors.toList());

                if (filteredResults.isEmpty()) {
                    message(sender, ChatColor.RED + "No search results found for query: " + query);
                    return;
                }

                int totalPages = (int) Math.ceil((double) filteredResults.size() / resultsPerPage);

                if (page < 1 || page > totalPages) {
                    message(sender, ChatColor.RED + "Invalid page number. Please provide a valid page number between 1 and " + totalPages + ".");
                    return;
                }

                int startIndex = (page - 1) * resultsPerPage;
                int endIndex = Math.min(startIndex + resultsPerPage, filteredResults.size());

                sender.sendMessage(ChatColor.GOLD + "=== Search Results for '" + query + "' (Page " + page + "/" + totalPages + ") ===");

                for (int i = startIndex; i < endIndex; i++) {
                    SearchResult result = filteredResults.get(i);
                    result.format().forEach(sender::sendMessage);
                }

                TextComponent arrowLeft = new TextComponent(" < Previous page |");
                TextComponent arrowRight = new TextComponent("| Next page >");

                if (page > 1) {
                    arrowLeft.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/skfind searchpage " + (page - 1) + " " + query));
                    arrowLeft.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Go to previous page").create()));
                    arrowLeft.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                } else {
                    arrowLeft.setColor(net.md_5.bungee.api.ChatColor.RED);
                }

                if (page < totalPages) {
                    arrowRight.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/skfind searchpage " + (page + 1) + " " + query));
                    arrowRight.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Go to next page").create()));
                    arrowRight.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                } else {
                    arrowRight.setColor(net.md_5.bungee.api.ChatColor.RED);
                }

                sender.spigot().sendMessage(arrowLeft, new TextComponent("  "), arrowRight);
                message(sender, ChatColor.GREEN + "Page " + page + "/" + totalPages + " for query: " + query);
            });
        } catch (RuntimeException e) {
            message(sender, ChatColor.RED + "Encountered ERROR while searching! See console for logs.");
            e.printStackTrace();
        }
    }

    public void message(CommandSender sender, String msg) {
        sender.sendMessage(SkFinder.PREFIX + msg);
    }
}
