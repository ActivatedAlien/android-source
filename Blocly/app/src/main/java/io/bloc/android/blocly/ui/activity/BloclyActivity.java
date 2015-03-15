package io.bloc.android.blocly.ui.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.bloc.android.blocly.BloclyApplication;
import io.bloc.android.blocly.R;
import io.bloc.android.blocly.api.DataSource;
import io.bloc.android.blocly.api.model.RssFeed;
import io.bloc.android.blocly.api.model.RssItem;
import io.bloc.android.blocly.ui.adapter.NavigationDrawerAdapter;
import io.bloc.android.blocly.ui.fragment.RssItemDetailFragment;
import io.bloc.android.blocly.ui.fragment.RssItemListFragment;

/**
 * Created by theinnformaster on 3/2/15.
 */
public class BloclyActivity extends ActionBarActivity
        implements
        NavigationDrawerAdapter.NavigationDrawerAdapterDelegate,
        NavigationDrawerAdapter.NavigationDrawerAdapterDataSource,
        RssItemListFragment.Delegate {

    private static final String BUNDLE_EXTRA_CURRENT_FEED = BloclyActivity.class.getCanonicalName().concat(".EXTRA_CURRENT_FEED");
    private static final String BUNDLE_EXTRA_CURRENT_ITEM = BloclyActivity.class.getCanonicalName().concat(".EXTRA_CURRENT_ITEM");

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationDrawerAdapter navigationDrawerAdapter;
    private Menu menu;
    private View overflowButton;
    private List<RssFeed> allFeeds = new ArrayList<RssFeed>();
    private RssItem expandedItem = null;
    private boolean onTablet;
    private boolean onTabletPortrait;

    private RssFeed currentFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocly);

        if (savedInstanceState != null) {
            currentFeed = savedInstanceState.getParcelable(BUNDLE_EXTRA_CURRENT_FEED);
            expandedItem = savedInstanceState.getParcelable(BUNDLE_EXTRA_CURRENT_ITEM);
        }

        onTablet = findViewById(R.id.fl_activity_blocly_right_pane) != null;
        onTabletPortrait = findViewById(R.id.fl_activity_blocly_bottom_pane) != null;

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_activity_blocly);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.dl_activity_blocly);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (overflowButton != null) {
                    overflowButton.setAlpha(1f);
                    overflowButton.setEnabled(true);
                }
                if (menu == null) {
                    return;
                }
                for (int i = 0; i < menu.size(); i++) {
                    MenuItem item = menu.getItem(i);
                    if (item.getItemId() == R.id.action_share
                            && expandedItem == null) {
                        continue;
                    }
                    Drawable icon = item.getIcon();
                    if (icon != null) {
                        icon.setAlpha(255);
                        if (item.getItemId() == R.id.action_share && onTablet) {
                            item.setEnabled(false);
                            item.setVisible(false);
                            icon.setAlpha(0);
                        }
                    }
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (overflowButton == null) {
                    ArrayList<View> foundViews = new ArrayList<View>();
                    getWindow().getDecorView().findViewsWithText(foundViews,
                            getString(R.string.abc_action_menu_overflow_description),
                            View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                    if (foundViews.size() > 0) {
                        overflowButton = foundViews.get(0);
                    }
                }
                if (overflowButton != null) {
                    overflowButton.setAlpha(1f - slideOffset);
                }
                if (menu == null) {
                    return;
                }
                for (int i = 0; i < menu.size(); i++) {
                    MenuItem item = menu.getItem(i);
                    if (item.getItemId() == R.id.action_share
                            && expandedItem == null) {
                        continue;
                    }
                    Drawable icon = item.getIcon();
                    if (icon != null) {
                        icon.setAlpha((int) ((1f - slideOffset) * 255));
                        if (item.getItemId() == R.id.action_share && onTablet) {
                            item.setEnabled(false);
                            item.setVisible(false);
                            icon.setAlpha(0);
                        }
                    }
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (overflowButton != null) {
                    overflowButton.setEnabled(false);
                }
                if (menu == null) {
                    return;
                }
                for (int i = 0; i < menu.size(); i++) {
                    menu.getItem(i).setEnabled(false);
                }
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        navigationDrawerAdapter = new NavigationDrawerAdapter();
        navigationDrawerAdapter.setDelegate(this);
        navigationDrawerAdapter.setDataSource(this);
        RecyclerView navigationRecyclerView = (RecyclerView) findViewById(R.id.rv_nav_activity_blocly);
        navigationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        navigationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        navigationRecyclerView.setAdapter(navigationDrawerAdapter);

        BloclyApplication.getSharedDataSource().fetchAllFeeds(new DataSource.Callback<List<RssFeed>>() {
            @Override
            public void onSuccess(List<RssFeed> rssFeeds) {
                allFeeds.addAll(rssFeeds);
                navigationDrawerAdapter.notifyDataSetChanged();
                if (currentFeed == null) {
                    currentFeed = rssFeeds.get(0);
                    getFragmentManager()
                            .beginTransaction()
                            .add(R.id.fl_activity_blocly, RssItemListFragment.fragmentForRssFeed(currentFeed))
                            .commit();
                }
            }

            @Override
            public void onError(String errorMessage) {
            }
        });

        if (expandedItem != null) {
            if (onTablet) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fl_activity_blocly_right_pane, RssItemDetailFragment.detailFragmentForRssItem(expandedItem))
                        .commit();
            } else if (onTabletPortrait) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fl_activity_blocly_bottom_pane, RssItemDetailFragment.detailFragmentForRssItem(expandedItem))
                        .commit();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(BUNDLE_EXTRA_CURRENT_FEED, currentFeed);
        savedInstanceState.putParcelable(BUNDLE_EXTRA_CURRENT_ITEM, expandedItem);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.action_share) {
            RssItem itemToShare = expandedItem;
            if (itemToShare == null) {
                return false;
            }
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    String.format("%s (%s)", itemToShare.getTitle(), itemToShare.getUrl()));
            shareIntent.setType("text/plain");
            Intent chooser = Intent.createChooser(shareIntent, getString(R.string.share_chooser_title));
            startActivity(chooser);
        } else {
            Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            return super.onCreateOptionsMenu(menu);
        }
        getMenuInflater().inflate(R.menu.blocly, menu);
        this.menu = menu;
        animateShareItem(expandedItem != null);
        return super.onCreateOptionsMenu(menu);
    }

    /*
     * NavigationDrawerAdapterDelegate
     */

    @Override
    public void didSelectNavigationOption(NavigationDrawerAdapter adapter, NavigationDrawerAdapter.NavigationOption navigationOption) {
        drawerLayout.closeDrawers();
        Toast.makeText(this, "Show the " + navigationOption.name(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void didSelectFeed(NavigationDrawerAdapter adapter, RssFeed rssFeed) {
        currentFeed = rssFeed;
        drawerLayout.closeDrawers();
        Toast.makeText(this, "Show RSS items from " + rssFeed.getTitle(), Toast.LENGTH_SHORT).show();
    }

    /*
      * NavigationDrawerAdapterDataSource
      */

    @Override
    public List<RssFeed> getFeeds(NavigationDrawerAdapter adapter) {
        return allFeeds;
    }

    /*
      * RssListFragment.Delegate
      */

    @Override
    public void onItemExpanded(RssItemListFragment rssItemListFragment, RssItem rssItem) {
        expandedItem = rssItem;
        if (onTablet) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fl_activity_blocly_right_pane, RssItemDetailFragment.detailFragmentForRssItem(rssItemListFragment, rssItem))
                    .commit();
            return;
        } else if (onTabletPortrait) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fl_activity_blocly_bottom_pane, RssItemDetailFragment.detailFragmentForRssItem(rssItemListFragment, rssItem))
                    .commit();
            return;
        }
        animateShareItem(expandedItem != null);
    }

     /*
      * ItemAdapter.Delegate
      */

    @Override
    public void onItemContracted(RssItemListFragment rssItemListFragment, RssItem rssItem) {
        if (expandedItem == rssItem) {
            expandedItem = null;
        }
        animateShareItem(expandedItem != null);
    }

    @Override
    public void onItemVisitClicked(RssItemListFragment rssItemListFragment, RssItem rssItem) {
        Intent visitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(rssItem.getUrl()));
        startActivity(visitIntent);
    }

    /*
      * Private methods
      */

    private void animateShareItem(final boolean isEnabled) {
        MenuItem shareItem = menu.findItem(R.id.action_share);
        boolean enabled = (onTablet || onTabletPortrait) ? false : isEnabled;
        if (shareItem.isEnabled() == enabled) {
            return;
        }
        shareItem.setEnabled(enabled);
        final Drawable shareIcon = shareItem.getIcon();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(enabled ? new int[]{0, 255} : new int[]{255, 0});
        valueAnimator.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                shareIcon.setAlpha((Integer) animation.getAnimatedValue());
            }
        });
        valueAnimator.start();
    }
}
