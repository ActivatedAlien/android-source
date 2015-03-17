package io.bloc.android.blocly.ui.activity;

import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.CheckBox;

import io.bloc.android.blocly.BloclyApplication;
import io.bloc.android.blocly.R;
import io.bloc.android.blocly.api.model.RssItem;
import io.bloc.android.blocly.ui.adapter.ItemAdapter;

/**
 * Created by theinnformaster on 3/11/15.
 */
public class ActivityTest extends ActivityInstrumentationTestCase2<BloclyActivity> {
    BloclyActivity bloclyActivity;
    RecyclerView recylerView;

    public ActivityTest() {
        super(BloclyActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(true);
        bloclyActivity = getActivity();
        recylerView = (RecyclerView) bloclyActivity.findViewById(R.id.rv_fragment_rss_list);
    }

    public void testFavoriteCheckboxPresent() {
        int N = recylerView.getChildCount();
        for (int i = 0; i < N; i++) {
            assertNotNull(recylerView.getChildAt(i).findViewById(R.id.cb_rss_item_favorite_star));
        }
    }

    public void testFavoriteCheckboxWorking() {
        int N = recylerView.getChildCount();
        ItemAdapter itemAdapter = (ItemAdapter) recylerView.getAdapter();
        assertNotNull(itemAdapter);

        for (int i = 0; i < N; i++) {
            final CheckBox cb = (CheckBox) recylerView.getChildAt(i).findViewById(R.id.cb_rss_item_favorite_star);

            RssItem rssItem = itemAdapter.getDataSource().getRssItem(itemAdapter, i);
            assertNotNull(rssItem);

            assertFalse(rssItem.isFavorite());
            assertFalse(BloclyApplication.getSharedDataSource().getFavoriteColumn(rssItem.getRowId()));

            try {
                runTestOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cb.performClick();
                    }
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            assertTrue(rssItem.isFavorite());
            assertTrue(BloclyApplication.getSharedDataSource().getFavoriteColumn(rssItem.getRowId()));

            try {
                runTestOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cb.performClick();
                    }
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            assertFalse(rssItem.isFavorite());
            assertFalse(BloclyApplication.getSharedDataSource().getFavoriteColumn(rssItem.getRowId()));
        }
    }
}