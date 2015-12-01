package org.heaven7.demo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.extra.RoundedBitmapBuilder;

import org.heaven7.core.adapter.ISelectable;
import org.heaven7.core.adapter.QuickRecycleViewAdapter;
import org.heaven7.core.save_state.BundleSupportType;
import org.heaven7.core.save_state.SaveStateField;
import org.heaven7.core.viewhelper.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class QuickRecycleViewTestActivity extends BaseActivity {

    RecyclerView mRecyclerView;

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
        mItems = new ArrayList<>();
        addTestData(mItems);
        mRecyclerView.setAdapter(new QuickRecycleViewAdapter<Item>(R.layout.item_image, mItems) {

            @Override
            protected void onBindData(Context context, final int position, final Item item,
                                      int itemLayoutId, ViewHelper helper) {
                helper.setImageUrl(R.id.eniv, item.url, new RoundedBitmapBuilder()
                        .scaleType(ImageView.ScaleType.CENTER_CROP))
                        .view(R.id.tv)
                        .setTextColor(item.isSelected() ? Color.RED : Color.BLACK)
                        .setText(item.url).reverse(helper)
                        .setOnClickListener(R.id.ll, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showToast("select position is " + position);
                                mSelectUrl = item.url;
                                setSelected(position);
                            }
                        });
            }

            @Override
            protected int getItemLayoutId(int position, Item item) {
                return super.getItemLayoutId(position, item);
            }
        });
    }

    private void addTestData(List<Item> items) {
        for(int i=0 ,size = Test.URLS.length ; i<size ;i++){
            items.add(new Item(Test.URLS[i]));
        }
    }

    public static class Item implements ISelectable, Parcelable {

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.url);
            dest.writeByte(selected ? (byte) 1 : (byte) 0);
        }

        private Item(Parcel in) {
            this.url = in.readString();
            this.selected = in.readByte() != 0;
        }

        public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
            public Item createFromParcel(Parcel source) {
                return new Item(source);
            }

            public Item[] newArray(int size) {
                return new Item[size];
            }
        };
    }
}
