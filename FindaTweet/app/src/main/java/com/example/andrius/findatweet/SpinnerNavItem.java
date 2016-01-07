package com.example.andrius.findatweet;

/**
 * Created by andrius on 2015-12-17.
 */
public class SpinnerNavItem {

    private String title;
    private int icon;
    private float longtitude;
    private float latitude;

    public SpinnerNavItem(String title, int icon){
        this.title = title;
        this.icon = icon;
        if(title == "New York") {
            longtitude = (float) -74.0059700;
            latitude = (float) 40.7142700;
        } else if (title == "Berlin") {
            longtitude = (float) 13.4105300;
            latitude = (float) 52.5243700;
        } else if (title == "Rio De Janeiro") {
            longtitude = (float) -43.207500;
            latitude = (float) -22.19278;
        } else if (title == "Stockholm") {
            longtitude = (float) 18.0649;
            latitude = (float) 59.33258;
        } else if (title == "Gothenburg") {
            longtitude = (float) 11.96679;
            latitude = (float) 57.70716;
        }else if (title == "London") {
            longtitude = (float) -0.12574;
            latitude = (float) 51.50853;
        }else if (title == "Helsinki") {
            longtitude = (float) 24.93545;
            latitude = (float) 60.16952;
        }else if (title == "Copenhagen") {
            longtitude = (float) 12.56553;
            latitude = (float) 55.67594;
        }else if (title == "Paris") {
            longtitude = (float) 2.3488;
            latitude = (float) 48.85341;

        }else if (title == "Amsterdam") {
            longtitude = (float) 4.88969;
            latitude = (float) 52.37403;
        }



    }

    public String getTitle(){
        return this.title;
    }

    public int getIcon(){
        return this.icon;
    }

    public float getLongtitude(){
        return longtitude;
    }

    public float getLatitude(){
        return latitude;
    }
}
