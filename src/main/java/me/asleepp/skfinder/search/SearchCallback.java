package me.asleepp.skfinder.search;

import java.util.List;

@FunctionalInterface
public interface SearchCallback {
    void accept(List<SearchResult> searchResults);
}
