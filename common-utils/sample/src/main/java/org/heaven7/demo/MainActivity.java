package org.heaven7.demo;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {

    public static final String[] options = {
            "QuickRecycleAdapterTest",
            "RecyclerViewDecorationTest",
            "PulltofreshSwipeTest",
            "QuickRecycleSwipeAdapterTest",
            "QuickListViewSwipeAdapterTest",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent;

        switch (position) {
            case 0:
                intent = new Intent(this, QuickRecycleViewTestActivity.class);
                break;
            case 1:
                intent = new Intent(this, RecyclerViewDecorationTestActivity.class);
                break;
            case 2:
                intent = new Intent(this, PulltorefreshSwipeTest.class);
                break;
            case 3:
                intent = new Intent(this, QuickSwipeRecycleAdapterTestActivity.class);
                break;
            case 4:
                intent = new Intent(this, QuickSwipeListViewAdapterTestActivity.class);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        startActivity(intent);
    }
}
