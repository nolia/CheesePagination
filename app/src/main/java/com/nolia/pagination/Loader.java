package com.nolia.pagination;

import android.os.Handler;
import android.os.Looper;

/**Fake data loader
 *
 * @author Nikolay Soroka
 */
public class Loader {

    public static final int LOAD_DELAY = 2000;
    public static final int MAX_ITEMS = 1000;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public void loadNext(int start, int pageSize, final Data.Callback callback) {
        if (pageSize > MAX_ITEMS) {
            pageSize = MAX_ITEMS;
        }
        final Data data = new Data();

        data.firstItemIndex = start;
        data.totalCount = MAX_ITEMS;
        data.items = new String[pageSize];

        for (int i = 0; i < pageSize; i++) {
            data.items[i] = Data.CHEESES[ (start + i) % Data.CHEESES.length ];
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onGetData(data);
            }
        }, LOAD_DELAY);
    }
}
