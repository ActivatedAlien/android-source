package io.bloc.android.blocly.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import io.bloc.android.blocly.BloclyApplication;
import io.bloc.android.blocly.R;
import io.bloc.android.blocly.api.DataSource;
import io.bloc.android.blocly.api.model.RssItem;
import io.bloc.android.blocly.ui.adapter.ItemAdapter;

/**
 * Created by theinnformaster on 3/6/15.
 */
public class RssItemDetailFragment extends Fragment implements ImageLoadingListener {

    private static final String BUNDLE_EXTRA_RSS_ITEM = RssItemDetailFragment.class.getCanonicalName().concat(".EXTRA_RSS_ITEM");

    public static RssItemDetailFragment detailFragmentForRssItem(RssItemListFragment rssItemListFragment, RssItem rssItem) {
        Bundle arguments = new Bundle();
        arguments.putLong(BUNDLE_EXTRA_RSS_ITEM, rssItem.getRowId());
        RssItemDetailFragment rssItemDetailFragment = new RssItemDetailFragment();
        rssItemDetailFragment.setArguments(arguments);
        rssItemDetailFragment.itemAdapter = rssItemListFragment.getItemAdapterWithDelegate();
        return rssItemDetailFragment;
    }

    public static RssItemDetailFragment detailFragmentForRssItem(RssItem rssItem) {
        Bundle arguments = new Bundle();
        arguments.putLong(BUNDLE_EXTRA_RSS_ITEM, rssItem.getRowId());
        RssItemDetailFragment rssItemDetailFragment = new RssItemDetailFragment();
        rssItemDetailFragment.setArguments(arguments);
        return rssItemDetailFragment;
    }

    Menu menu;
    Toolbar toolbar;
    ItemAdapter itemAdapter;
    RssItem item;
    ImageView headerImage;
    TextView title;
    TextView content;
    ProgressBar progressBar;
    boolean onLoadingComplete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
            if (arguments != null || savedInstanceState != null) {
                long rssItemId;
                if (savedInstanceState != null) {
                    rssItemId = ((RssItem) savedInstanceState.getParcelable(BUNDLE_EXTRA_RSS_ITEM)).getRowId();
                } else {
                    rssItemId = arguments.getLong(BUNDLE_EXTRA_RSS_ITEM);
                }
                BloclyApplication.getSharedDataSource().fetchRSSItemWithId(rssItemId, new DataSource.Callback<RssItem>() {
                    @Override
                    public void onSuccess(RssItem rssItem) {
                        if (getActivity() == null) {
                            return;
                        }
                        item = rssItem;
                        title.setText(rssItem.getTitle());
                        content.setText(rssItem.getDescription());
                        ImageLoader.getInstance().loadImage(rssItem.getImageUrl(), RssItemDetailFragment.this);
                    }

                    @Override
                    public void onError(String errorMessage) {
                    }
                });
            }


        onLoadingComplete = false;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(BUNDLE_EXTRA_RSS_ITEM, item);
    }
/*
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v("INSTANCE IS NULL", "" + (savedInstanceState == null));
        if (savedInstanceState != null) {
            item = savedInstanceState.getParcelable(BUNDLE_EXTRA_RSS_ITEM);
        }

        Log.v("ITEM IS NULL", "" + (item == null));
        if (item != null) {
            Log.v("ITEM DEETS", item.getTitle() + " " + item.getDescription());
            title.setText(item.getTitle());
            content.setText(item.getDescription());
            ImageLoader.getInstance().loadImage(item.getImageUrl(), RssItemDetailFragment.this);
        }
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_rss_item_detail, container, false);

        toolbar = (Toolbar) inflate.findViewById(R.id.tb_fragment_rss_item_detail);
        toolbar.inflateMenu(R.menu.blocly_fragment);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_visit_page:
                        itemAdapter.getDelegate().onVisitClicked(itemAdapter, item);
                        break;
                    case R.id.action_share:
                        RssItem itemToShare = item;
                        if (itemToShare == null) {
                            return false;
                        }
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT,
                                String.format("%s (%s)", itemToShare.getTitle(), itemToShare.getUrl()));
                        shareIntent.setType("text/plain");
                        Intent chooser = Intent.createChooser(shareIntent, getString(R.string.share_chooser_title));
                        startActivity(chooser);
                        break;
                }
                return true;
            }
        });


        headerImage = (ImageView) inflate.findViewById(R.id.iv_fragment_rss_item_detail_header);
        progressBar = (ProgressBar) inflate.findViewById(R.id.pb_fragment_rss_item_detail_header);
        title = (TextView) inflate.findViewById(R.id.tv_fragment_rss_item_detail_title);
        content = (TextView) inflate.findViewById(R.id.tv_fragment_rss_item_detail_content);
        return inflate;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.blocly_fragment, menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu,inflater);
    }

    /*
      * ImageLoadingListener
      */

    @Override
    public void onLoadingStarted(String imageUri, View view) {
        progressBar.animate()
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();
        headerImage.animate()
                .alpha(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        progressBar.animate()
                .alpha(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        progressBar.animate()
                .alpha(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();
        headerImage.setImageBitmap(loadedImage);
        headerImage.animate()
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();
        onLoadingComplete = true;
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
/*            RssItem itemToShare = expandedItem;
            if (itemToShare == null) {
                return false;
            }
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    String.format("%s (%s)", itemToShare.getTitle(), itemToShare.getUrl()));
            shareIntent.setType("text/plain");
            Intent chooser = Intent.createChooser(shareIntent, getString(R.string.share_chooser_title));
            startActivity(chooser);*/
        } else {
            //Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
