package com.barmej.streetissues.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

public class StreetIssueItem implements Parcelable {
    private String title;
    private String description;
    private String photo;
    private GeoPoint location;

    public StreetIssueItem(){
    }

    public StreetIssueItem(String title, String description, String photo, GeoPoint location) {
        this.title = title;
        this.description = description;
        this.photo = photo;
        this.location = location;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }



    protected StreetIssueItem(Parcel in) {
        title = in.readString();
        description = in.readString();
        photo = in.readString();
    }

    public static final Creator<StreetIssueItem> CREATOR = new Creator<StreetIssueItem>() {
        @Override
        public StreetIssueItem createFromParcel(Parcel in) {
            return new StreetIssueItem(in);
        }

        @Override
        public StreetIssueItem[] newArray(int size) {
            return new StreetIssueItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(photo);
    }
}
