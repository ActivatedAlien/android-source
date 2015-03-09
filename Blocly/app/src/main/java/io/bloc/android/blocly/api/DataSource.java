package io.bloc.android.blocly.api;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.bloc.android.blocly.BloclyApplication;
import io.bloc.android.blocly.R;
import io.bloc.android.blocly.api.model.RssFeed;
import io.bloc.android.blocly.api.model.RssItem;
import io.bloc.android.blocly.api.network.GetFeedsNetworkRequest;

/**
 * Created by theinnformaster on 3/2/15.
 */
public class DataSource {
    private List<RssFeed> feeds;
    private List<RssItem> items;

    public DataSource() {
        feeds = new ArrayList<RssFeed>();
        items = new ArrayList<RssItem>();
        //createFakeData();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<GetFeedsNetworkRequest.FeedResponse> responses =
                        new GetFeedsNetworkRequest("http://feeds.feedburner.com/androidcentral?format=xml").performRequest();
                for (GetFeedsNetworkRequest.FeedResponse feedResponse : responses) {
                    feeds.add(new RssFeed(feedResponse.channelTitle,
                            feedResponse.channelDescription,
                            feedResponse.channelURL,
                            feedResponse.channelFeedURL));
                    for (GetFeedsNetworkRequest.ItemResponse itemResponse : feedResponse.channelItems) {
                        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss z", Locale.ENGLISH);
                        long itemPubDate = System.currentTimeMillis();;
                        try {
                            itemPubDate = dateFormat.parse(itemResponse.itemPubDate).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        items.add(new RssItem(itemResponse.itemGUID,
                                itemResponse.itemTitle,
                                itemResponse.itemDescription,
                                itemResponse.itemURL,
                                itemResponse.itemEnclosureURL,
                                0,
                                itemPubDate,
                                false,
                                false
                                ));
                    }
                }
            }
        }).start();
    }

    public List<RssFeed> getFeeds() {
        return feeds;
    }

    public List<RssItem> getItems() {
        return items;
    }

    void createFakeData() {
        feeds.add(new RssFeed("My Favorite Feed",
                "This feed is just incredible, I can't even begin to tell you…",
                "http://favoritefeed.net", "http://feeds.feedburner.com/favorite_feed?format=xml"));
        for (int i = 0; i < 10; i++) {
            items.add(new RssItem(String.valueOf(i),
                    BloclyApplication.getSharedInstance().getString(R.string.placeholder_headline) + " " + i,
                    BloclyApplication.getSharedInstance().getString(R.string.placeholder_content),
                    "http://favoritefeed.net?story_id=an-incredible-news-story",
                    "http://rs1img.memecdn.com/silly-dog_o_511213.jpg",
                    0, System.currentTimeMillis(), false, false));
        }
    }
}
