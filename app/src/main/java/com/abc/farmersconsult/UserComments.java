package com.abc.farmersconsult;

public class UserComments {

      private String Name,comment,date,time,Image;

      public UserComments() {

      }

      public UserComments(String name, String Comment, String Date, String Time, String image){
          this.Name=name;
          this.comment=Comment;
          this.date=Date;
          this.time=Time;
          this.Image=image;
      }

    public String getName() {
        return Name;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getImage() {
        return Image;
    }
}
