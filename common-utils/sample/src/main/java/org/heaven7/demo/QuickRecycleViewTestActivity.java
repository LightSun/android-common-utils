package org.heaven7.demo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.extra.ImageParam;

import org.heaven7.core.adapter.ISelectable;
import org.heaven7.core.adapter.QuickRecycleViewAdapter;
import org.heaven7.core.viewhelper.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class QuickRecycleViewTestActivity extends BaseActivity {

    RecyclerView mRecyclerView;

    @Override
    protected int getlayoutId() {
        return R.layout.activity_recycle;
    }

    @Override
    protected void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        List<Item> items = new ArrayList<>();
        addTestData(items);
        mRecyclerView.setAdapter(new QuickRecycleViewAdapter<Item>(R.layout.item_image, items) {
            @Override
            protected void onBindData(Context ctx, final int position, Item item, ViewHelper helper) {
                helper.setImageUrl(R.id.eniv, item.url, new ImageParam.Builder().create())
                        .view(R.id.tv)
                        .setTextColor(item.isSelected() ? Color.RED : Color.BLACK)
                        .setText(item.url).reverse(helper)
                        .setOnClickListener(R.id.ll, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showToast("select position is " + position);
                                setSelected(position);
                            }
                        });
            }
        });
    }

    private void addTestData(List<Item> items) {
        for(int i=0 ,size = Test.URLS.length ; i<size ;i++){
            items.add(new Item(Test.URLS[i]));
        }
    }

    public static class Item implements ISelectable{

        public String url;
        private boolean selected;

        public Item(String url) {
            this.url = url;
        }

        @Override
        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }
    }
}
