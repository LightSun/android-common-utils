package org.heaven7.demo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.extra.RoundedBitmapBuilder;

import org.heaven7.core.adapter.QuickSwipeAdapter;
import org.heaven7.core.save_state.BundleSupportType;
import org.heaven7.core.save_state.SaveStateField;
import org.heaven7.core.viewhelper.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import static org.heaven7.demo.QuickRecycleViewTestActivity.Item;

public class QuickSwipeListViewAdapterTestActivity extends BaseActivity {

    ListView mListView;

    /**
     * the follow types must assigned.
     BundleSupportType.INTEGER_ARRAY_lIST:
     BundleSupportType.STRING_ARRAY_LIST:
     BundleSupportType.PARCELABLE_ARRAY_LIST:
     BundleSupportType.PARCELABLE_LIST:
     BundleSupportType.CHAR_SEQUENCE_ARRAY_LIST:
     // SparseArray<? extends Parcelable>
      BundleSupportType.SPARSE_PARCELABLE_ARRAY:
     */
    @SaveStateField(value = "mItems",flag = BundleSupportType.PARCELABLE_LIST)
    List<Item> mItems;

    @SaveStateField("mSelectUrl")
    String mSelectUrl;

    QuickSwipeAdapter<Item> mAdapter;

    @Override
    protected int getlayoutId() {
        return R.layout.activity_list_view;
    }

    @Override
    protected void initView() {
        mListView = (ListView) findViewById(R.id.listView);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mItems = new ArrayList<>();
        addTestData(mItems);
        mListView.setAdapter(mAdapter = new QuickSwipeAdapter<Item>(
                R.layout.item_swipe, R.layout.item_menu, mItems) {

            @Override
            protected void onBindData(Context context, final int position, final Item item,
                                      int itemLayoutId, int menuLayoutId, ViewHelper helper) {
                helper.setImageUrl(R.id.eniv, item.url, new RoundedBitmapBuilder()
                        .scaleType(ImageView.ScaleType.CENTER_CROP)
                        .placeholder(R.drawable.ic_launcher))
                        .view(R.id.tv)
                        .setTextColor(item.isSelected() ? Color.RED : Color.BLACK)
                        .setText(item.url).reverse(helper)
                        .setOnClickListener(R.id.ll, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (shouldIgnoreTouchEvent()) {
                                    showToast("if have a item that opened the swipe , the item is closed swipe now.");
                                } else {
                                    showToast("select position is " + position);
                                    mSelectUrl = item.url;
                                    setSelected(position);
                                }
                            }
                        });
            }
            @Override
            protected int getTrackingEdge() {
                return super.getTrackingEdge();
            }
        });
    }

    private void addTestData(List<Item> items) {
        for(int i=0 ,size = Test.URLS.length ; i<size ;i++){
            items.add(new Item(Test.URLS[i]));
        }
    }


}
