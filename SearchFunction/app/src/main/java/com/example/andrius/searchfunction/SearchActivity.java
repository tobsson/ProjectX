package com.example.andrius.searchfunction;

/**
 * Created by andrius on 2015-11-29.
 */
import android.os.Bundle;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;

abstract public class SearchActivity extends ListActivity {
    abstract ListAdapter makeMeAnAdapter(Intent intent);

    private static final int LOCAL_SEARCH_ID = Menu.FIRST + 1;
    private static final int GLOBAL_SEARCH_ID = Menu.FIRST + 2;
    EditText selection;
    ArrayList<String> items = new ArrayList<String>();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        selection = (EditText) findViewById(R.id.selection);

        try {
            XmlPullParser xpp = getResources().getXml(R.xml.word);

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("word")) {
                        items.add(xpp.getAttributeValue(0));
                    }
                }
                xpp.next();
            }
        } catch (Throwable t) {
            Toast.makeText(this, "Request failed: " + t.toString(), 4000)
                    .show();
        }

        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
        onNewIntent(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        ListAdapter adapter = makeMeAnAdapter(intent);
        if (adapter == null) {
            finish();
        } else {
            setListAdapter(adapter);
        }
    }

    public void onListItemClick(ListView parent, View v, int position, long id) {
        selection.setText(parent.getAdapter().getItem(position).toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, LOCAL_SEARCH_ID, Menu.NONE, "Local Search")
                .setIcon(android.R.drawable.ic_search_category_default);
        menu.add(Menu.NONE, GLOBAL_SEARCH_ID, Menu.NONE, "Global Search")
                .setIcon(R.drawable.search67)
                .setAlphabeticShortcut(SearchManager.MENU_KEY);

        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case LOCAL_SEARCH_ID:
                onSearchRequested();
                return (true);

            case GLOBAL_SEARCH_ID:
                startSearch(null, false, null, true);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }
}