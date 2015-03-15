package io.bloc.android.blocly.api.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by theinnformaster on 3/2/15.
 */
public class RssFeed extends Model implements Parcelable {
    private String title;
    private String description;
    private String siteUrl;
    private String feedUrl;

    public RssFeed(long rowId, String title, String description, String siteUrl, String feedUrl) {
        super(rowId);
        this.title = title;
        this.description = description;
        this.siteUrl = siteUrl;
        this.feedUrl = feedUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(super.getRowId());
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(siteUrl);
        dest.writeString(feedUrl);
    }

    public static final Parcelable.Creator<RssFeed> CREATOR
            = new Parcelable.Creator<RssFeed>() {
        public RssFeed createFromParcel(Parcel in) {
            return new RssFeed(in);
        }

        public RssFeed[] newArray(int size) {
            return new RssFeed[size];
        }
    };

    private RssFeed(Parcel in) {
        super(in.readLong());
        this.title = in.readString();
        this.description = in.readString();
        this.siteUrl = in.readString();
        this.feedUrl = in.readString();
    }
}
