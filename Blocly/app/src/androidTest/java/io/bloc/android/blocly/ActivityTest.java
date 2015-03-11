package io.bloc.android.blocly;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.View;
import android.widget.CheckBox;

import io.bloc.android.blocly.ui.activity.BloclyActivity;

/**
 * Created by theinnformaster on 3/11/15.
 */
public class ActivityTest extends ActivityInstrumentationTestCase2<BloclyActivity> {
    BloclyActivity bloclyActivity;
    CheckBox favoriteCheckBox;

    public ActivityTest(Class<BloclyActivity> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(true);
        bloclyActivity = getActivity();
        favoriteCheckBox = (CheckBox) bloclyActivity.findViewById(R.id.cb_rss_item_check_mark);
    }

    public void testFavoriteCheckboxPresent() {
        final View decorView = bloclyActivity.getWindow().getDecorView();

        // how do I select one items and then select that item's checkbox?
        ViewAsserts.assertOnScreen(decorView, favoriteCheckBox);
    }

    public void testFavoriteCheckboxWorking() {
    }
}
