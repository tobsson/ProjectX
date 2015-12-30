package com.example.andrius.findatweet;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by andrius on 2015-11-29.
 */

// Adapter for suggestions.
public class SuggestionSimpleCursorAdapter
        extends android.widget.SimpleCursorAdapter
{


    //Constructor.

    public SuggestionSimpleCursorAdapter(Context context, int layout, Cursor c,
                                         String[] from, int[] to) {
        super(context, layout, c, from, to);
    }

    public SuggestionSimpleCursorAdapter(Context context, int layout, Cursor c,
                                         String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {
        //Returns a CharSequence representation of the specified Cursor as defined
        // by the current CursorToStringConverter.

        int indexColumnSuggestion = cursor.getColumnIndex(SuggestionsDatabase.FIELD_SUGGESTION);

        return cursor.getString(indexColumnSuggestion);
    }
}