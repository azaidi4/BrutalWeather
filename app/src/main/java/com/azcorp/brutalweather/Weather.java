package com.azcorp.brutalweather;

import android.graphics.drawable.Drawable;

/**
 * Created by ahmad on 7/26/2017.
 */

class Weather {

    private double temperature;
    private String main;
    private String description;
    private String country;
    private String weatherPhrase;
    private String unit;
    private Boolean isParsed;
    private String imageResourceID;
    private Drawable weatherIcon;

    Weather(double temp, String main, String description, String country, String unit, String ID) {
        this.temperature = temp;
        this.main = main;
        this.description = description;
        this.country = country;
        this.unit = unit;
        this.weatherPhrase = null;
        this.isParsed = true;
        this.imageResourceID = ID;
    }

    double getTemperature() {
        return temperature;
    }

    String getMain() {
        return main;
    }

    String getDescription() {
        return description;
    }

    String getCountry() {
        return country;
    }

    Boolean isParsed() {
        return isParsed;
    }

    void setWeatherPhrase(String weatherPhrase) {
        this.weatherPhrase = weatherPhrase;
    }

    String getWeatherPhrase() {
        return weatherPhrase;
    }

    String getUnit() {
        return unit;
    }

    String getImageResourceID() {
        return imageResourceID;
    }

    public Drawable getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(Drawable weatherIcon) {
        this.weatherIcon = weatherIcon;
    }
}
