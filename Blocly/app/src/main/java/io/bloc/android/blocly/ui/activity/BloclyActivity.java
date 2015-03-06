package io.bloc.android.blocly.ui.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.util.Log;
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
import io.bloc.android.blocly.api.model.RssFeed;
import io.bloc.android.blocly.api.model.RssItem;
import io.bloc.android.blocly.ui.adapter.ItemAdapter;
import io.bloc.android.blocly.ui.adapter.NavigationDrawerAdapter;

/**
 * Created by theinnformaster on 3/2/15.
 */
public class BloclyActivity extends ActionBarActivity
        implements
        NavigationDrawerAdapter.NavigationDrawerAdapterDelegate,
        ItemAdapter.DataSource,
        ItemAdapter.Delegate {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationDrawerAdapter navigationDrawerAdapter;
    private Menu menu;
    private View overflowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocly);

        /* assignment */
        // you can make this more readable by printing the load label of each ResolveInfo, I'm just lazy
        List<ResolveInfo> openWebPage =
                getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_VIEW), PackageManager.MATCH_DEFAULT_ONLY);
        List<ResolveInfo> dialPhoneNumber =
                getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_CALL), PackageManager.MATCH_DEFAULT_ONLY);
        List<ResolveInfo> composeEmail =
                getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_SENDTO), PackageManager.MATCH_DEFAULT_ONLY);
        Log.v("OPEN WEB PAGE", openWebPage.toString());
        Log.v("DIAL PHONE NUMBER", dialPhoneNumber.toString());
        Log.v("COMPOSE EMAIL", composeEmail.toString());

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_activity_blocly);
        setSupportActionBar(toolbar);

        itemAdapter = new ItemAdapter();
        itemAdapter.setDataSource(this);
        itemAdapter.setDelegate(this);

        recyclerView = (RecyclerView) findViewById(R.id.rv_activity_blocly);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemAdapter);

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
                            && itemAdapter.getExpandedItem() == null) {
                        continue;
                    }
                    item.setEnabled(true);
                    Drawable icon = item.getIcon();
                    if (icon != null) {
                        icon.setAlpha(255);
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
                            && itemAdapter.getExpandedItem() == null) {
                        continue;
                    }
                    Drawable icon = item.getIcon();
                    if (icon != null) {
                        icon.setAlpha((int) ((1f - slideOffset) * 255));
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
        RecyclerView navigationRecyclerView = (RecyclerView) findViewById(R.id.rv_nav_activity_blocly);
        navigationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        navigationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        navigationRecyclerView.setAdapter(navigationDrawerAdapter);
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
            RssItem itemToShare = itemAdapter.getExpandedItem();
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
        animateShareItem(itemAdapter.getExpandedItem() != null);
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
        drawerLayout.closeDrawers();
        Toast.makeText(this, "Show RSS items from " + rssFeed.getTitle(), Toast.LENGTH_SHORT).show();
    }

    /*
      * ItemAdapter.DataSource
      */

    @Override
    public RssItem getRssItem(ItemAdapter itemAdapter, int position) {
        return BloclyApplication.getSharedDataSource().getItems().get(position);
    }

    @Override
    public RssFeed getRssFeed(ItemAdapter itemAdapter, int position) {
        return BloclyApplication.getSharedDataSource().getFeeds().get(0);
    }

    @Override
    public int getItemCount(ItemAdapter itemAdapter) {
        return BloclyApplication.getSharedDataSource().getItems().size();
    }

     /*
      * ItemAdapter.Delegate
      */

    @Override
    public void onItemClicked(ItemAdapter itemAdapter, RssItem rssItem) {
        int positionToExpand = -1;
        int positionToContract = -1;

        if (itemAdapter.getExpandedItem() != null) {
            positionToContract = BloclyApplication.getSharedDataSource().getItems().indexOf(itemAdapter.getExpandedItem());

            View viewToContract = recyclerView.getLayoutManager().findViewByPosition(positionToContract);
            if (viewToContract == null) {
                positionToContract = -1;
            }
        }

        if (itemAdapter.getExpandedItem() != rssItem) {
            positionToExpand = BloclyApplication.getSharedDataSource().getItems().indexOf(rssItem);
            itemAdapter.setExpandedItem(rssItem);
        } else {
            itemAdapter.setExpandedItem(null);
        }
        if (positionToContract > -1) {
            itemAdapter.notifyItemChanged(positionToContract);
        }
        if (positionToExpand > -1) {
            animateShareItem(true);
            itemAdapter.notifyItemChanged(positionToExpand);
        } else {
            animateShareItem(false);
            return;
        }

        int lessToScroll = 0;
        if (positionToContract > -1 && positionToContract < positionToExpand) {
            lessToScroll = itemAdapter.getExpandedItemHeight() - itemAdapter.getCollapsedItemHeight();
        }

        View viewToExpand = recyclerView.getLayoutManager().findViewByPosition(positionToExpand);
        recyclerView.smoothScrollBy(0, viewToExpand.getTop() - lessToScroll);
    }

    @Override
    public void onVisitClicked(ItemAdapter itemAdapter, RssItem rssItem) {
        Intent visitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(rssItem.getUrl()));
        startActivity(visitIntent);
    }

     /*
      * Private methods
      */

    private void animateShareItem(final boolean enabled) {
        MenuItem shareItem = menu.findItem(R.id.action_share);
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
