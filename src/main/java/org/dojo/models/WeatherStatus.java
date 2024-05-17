package org.dojo.models;

import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;

public class WeatherStatus {
    public double temp;
    public double feels_likep;
    public double temp_min;
    public double temp_max;
    public double pressure;
    public double humidity;
    public double sea_level;
    public double grnd_level;

    public WeatherStatus(){
    }

    public double getTemp() {
        return temp;
    }

    public double getFeels_likep() {
        return feels_likep;
    }

    public double getTemp_min() {
        return temp_min;
    }

    public double getTemp_max() {
        return temp_max;
    }

    public double getSea_level() {
        return sea_level;
    }

    public double getGrnd_level() {
        return grnd_level;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPressure() {
        return pressure;
    }
}

