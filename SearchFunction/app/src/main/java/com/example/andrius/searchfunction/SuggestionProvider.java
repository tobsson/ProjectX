package com.example.andrius.searchfunction;

/**
 * Created by andrius on 2015-11-29.
 */
import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.provider.SearchRecentSuggestions;

public class SuggestionProvider extends SearchRecentSuggestionsProvider {
    static SearchRecentSuggestions getBridge(Context ctxt) {
        return (new SearchRecentSuggestions(ctxt,
                "com.example.MainActivity", DATABASE_MODE_QUERIES));
    }

    public SuggestionProvider() {
        super();
        setupSuggestions("com.app.searchfunction", DATABASE_MODE_QUERIES);
    }
}