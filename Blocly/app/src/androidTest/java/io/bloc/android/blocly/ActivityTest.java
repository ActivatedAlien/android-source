package io.bloc.android.blocly;

import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.CheckBox;

import io.bloc.android.blocly.ui.activity.BloclyActivity;

/**
 * Created by theinnformaster on 3/11/15.
 */
public class ActivityTest extends ActivityInstrumentationTestCase2<BloclyActivity> {
    BloclyActivity bloclyActivity;
    RecyclerView recylerView;
    CheckBox favoriteCheckBox;

    public ActivityTest() {
        super(BloclyActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(true);
        bloclyActivity = getActivity();
        favoriteCheckBox = (CheckBox) bloclyActivity.findViewById(R.id.cb_rss_item_favorite_star);
        recylerView = (RecyclerView) bloclyActivity.findViewById(R.id.rv_fragment_rss_list);
    }

    public void testFavoriteCheckboxPresent() {
        int N = recylerView.getChildCount();
        for (int i = 0; i < N; i++) {
            assertNotNull(recylerView.getChildAt(i).findViewById(R.id.cb_rss_item_favorite_star));
        }
    }

    public void testFavoriteCheckboxWorking() {
    }
}
