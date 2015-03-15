package io.bloc.android.blocly.api.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by theinnformaster on 3/2/15.
 */
public class RssItem extends Model implements Parcelable {
    private String guid;
    private String title;
    private String description;
    private String url;
    private String imageUrl;
    private long rssFeedId;
    private long datePublished;
    private boolean favorite;
    private boolean archived;

    public RssItem(long rowId, String guid, String title, String description, String url, String imageUrl, long rssFeedId, long datePublished, boolean favorite, boolean archived) {
        super(rowId);
        this.guid = guid;
        this.title = title;
        this.description = description;
        this.url = url;
        this.imageUrl = imageUrl;
        this.rssFeedId = rssFeedId;
        this.datePublished = datePublished;
        this.favorite = favorite;
        this.archived = archived;
    }

    public String getGuid() {
        return guid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getRssFeedId() {
        return rssFeedId;
    }

    public long getDatePublished() {
        return datePublished;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public boolean isArchived() {
        return archived;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(super.getRowId());
        dest.writeString(guid);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(imageUrl);
        dest.writeLong(rssFeedId);
        dest.writeLong(datePublished);
        dest.writeInt(favorite ? 0 : 1);
        dest.writeInt(archived ? 0 : 1);
    }

    public static final Parcelable.Creator<RssItem> CREATOR
            = new Parcelable.Creator<RssItem>() {
        public RssItem createFromParcel(Parcel in) {
            return new RssItem(in);
        }

        public RssItem[] newArray(int size) {
            return new RssItem[size];
        }
    };

    private RssItem(Parcel in) {
        super(in.readLong());
        guid = in.readString();
        title = in.readString();
        description = in.readString();
        url = in.readString();
        imageUrl = in.readString();
        rssFeedId = in.readLong();
        datePublished = in.readLong();
        favorite = in.readInt() == 1;
        archived = in.readInt() == 1;
    }
}
