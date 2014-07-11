package com.nolia.pagination;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

/**
 * @author Nikolay Soroka
 */
public class MainActivity extends ListActivity {

    public static final int PAGE_SIZE = 20;
    private View mProgressView;
    private Loader mLoader;
    private ArrayAdapter<String> mAdapter;

    private AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener() {

        public boolean needToLoadMore = false;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE) {
                if (needToLoadMore && mIsLoadingAvailable) {
                    needToLoadMore = false;
                    doLoad();
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            final int lastItem = firstVisibleItem + visibleItemCount;

            if (lastItem == totalItemCount) {// we reached last list item - loading footer
                if (mIsLoadingAvailable) {
                    needToLoadMore = true;
                } else {
                    needToLoadMore = false;
                }
            } else {
                needToLoadMore = false;
            }
        }
    };

    private Data.Callback mCallback = new Data.Callback() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onGetData(final Data data) {
            if (mAdapter == null) {
                mAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);

                mProgressView.animate().alpha(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mProgressView.setVisibility(View.GONE);
                        setListAdapter(mAdapter);
                        getListView().setOnScrollListener(mScrollListener);
                        if (canLoadMore(data)) {
                            getListView().addFooterView(mLoadingFooter);
                        }
                    }
                }).start();
            }

            mAdapter.addAll(data.items);
            // process data
            mIsLoadingAvailable = canLoadMore(data);
            mNextStart = data.firstItemIndex + data.items.length;

            if (!mIsLoadingAvailable && mLoadingFooter.getParent() != null) {
                Log.e("MainActivity", "Removing footer !");
                getListView().removeFooterView(mLoadingFooter);
            }

        }
    };
    private boolean mIsLoadingAvailable;
    private int mNextStart = 0;

    private boolean canLoadMore(Data data) {
        return data.items.length + data.firstItemIndex < data.totalCount;
    }

    private ProgressBar mLoadingFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressView = findViewById(R.id.progress);
        mLoadingFooter = new ProgressBar(this);
        mLoadingFooter.setIndeterminate(true);


        mLoader = new Loader();
        doLoad();
    }

    private void doLoad() {
        mLoader.loadNext(mNextStart, PAGE_SIZE, mCallback);
    }
}
