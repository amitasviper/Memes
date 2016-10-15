package models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by viper on 17/09/16.
 */
public class User implements Parcelable{
    String name, profile_pic_url, email;

    public User() {
    }

    public User(String name, String profile_pic_url, String email) {
        this.name = name;
        this.profile_pic_url = profile_pic_url;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_pic_url() {
        return profile_pic_url;
    }

    public void setProfile_pic_url(String profile_pic_url) {
        this.profile_pic_url = profile_pic_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(profile_pic_url);
        dest.writeString(email);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private User(Parcel in) {
        name = in.readString();
        profile_pic_url = in.readString();
        email = in.readString();
    }
}
