package com.abc.farmersconsult;



import android.provider.ContactsContract;

import com.google.firebase.database.Exclude;

public class Posts {

    private String userid, Name, time, description, Image, date, postimage,places,Occupation;
    private String mkey;

    public Posts() {

    }


    public Posts( String Name, String time, String description, String postimage, String date, String Image,String userid,String Places,String occupation) {
        this.date = date;
        this.Image = Image;
        this.Name = Name;
        this.description = description;
        this.postimage = postimage;
        this.time = time;
        this.userid=userid;
        this.places=Places;
        this.Occupation=occupation;

    }

    public String getOccupation() {
        return Occupation;
    }

    @Exclude
    public String getKey(){
        return mkey;
    }





    @Exclude
    public void setkey(String key) {
        mkey = key;
    }

    public String getPlaces() {
        return places;
    }

    public String getName() {
        return Name;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return Image;
    }

    public String getDate() {
        return date;
    }

    public String getPostimage() {
        return postimage;
    }
}