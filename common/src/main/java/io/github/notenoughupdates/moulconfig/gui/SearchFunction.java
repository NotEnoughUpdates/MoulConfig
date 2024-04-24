package io.github.notenoughupdates.moulconfig.gui;

@FunctionalInterface
public interface SearchFunction {

    /**
     * Checks if an option fulfills a search query
     * @param editor The option that is being checked if it can fulfil the search query
     * @param word The query that is being searched
     * @return Returns true if the editor fulfills the search otherwise false
     */
    boolean fulfillsSearch(GuiOptionEditor editor, String word);
}
