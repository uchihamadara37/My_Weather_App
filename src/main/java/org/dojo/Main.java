package org.dojo;

import com.google.gson.Gson;
import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.bundled.CorsPluginConfig;
import io.javalin.rendering.template.JavalinFreemarker;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import kong.unirest.core.json.JSONObject;
import org.dojo.models.Metadata;
import org.dojo.models.MyResponse;
import org.dojo.models.WeatherStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static double latitude = -7.234234;
    static double longtitude = 110.987878;
    static String url = ("https://api.openweathermap.org/data/2.5/weather");

    static Gson gson = new Gson();

    public static void main(String[] args) {

        int port = 7070;
        if (args.length != 0){
            port = Integer.parseInt(args[0]);
        }
        var app = Javalin.create(config -> {
                config.fileRenderer(new JavalinFreemarker());

                config.staticFiles.add(staticFiles -> {
                    staticFiles.hostedPath = "/";
                    staticFiles.directory = "/public";
                    staticFiles.location = Location.CLASSPATH;
                });
            })
//                .before(ctx -> {
//                ctx.header("Access-Control-Allow-Origin", "*");
//                ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//                ctx.header("Access-Control-Allow-Headers", "*");
//                ctx.header("Access-Control-Max-Age", "86400");
//            })
            .get("/", ctx -> {
                Map<String, Object> attrib = new HashMap<String, Object>();
                attrib.put("user", "Kurniawan");
                attrib.put("weather", getWeather());

                ctx.render("/views/coba.html", attrib);
            })
            .get("/api/weather_now", ctx -> {
                ctx.json(getWeatherJSON().toString());
            })
            .get("/api/weather_now_mapping", ctx -> {
                Metadata metadata = new Metadata(200, "ok");
                WeatherStatus weatherStatus = getWeather();
                MyResponse<WeatherStatus> res = new MyResponse<WeatherStatus>(metadata, weatherStatus);
                ctx.json(res);
            })
            .get("/api/weather_api", ctx -> {
                //  ketika tidak ada params, akan langsung didefault lokasi jogja
                double latitude = ctx.queryParamAsClass("lat", Double.class).getOrDefault(-7.790595211984489);
                double longtitude = ctx.queryParamAsClass("long", Double.class).getOrDefault(110.37414550781251);
                String date = ctx.queryParamAsClass("date", String.class).getOrDefault(LocalDate.now().toString());

                int hour = ctx.queryParamAsClass("hour", Integer.class).getOrDefault(LocalTime.now().getHour()+1);

                ctx.json(getWeatherAPI(latitude, longtitude, date, hour).toString());
            })
            .get("/api/timezone", ctx -> {
                double latitude = ctx.queryParamAsClass("lat", Double.class).getOrDefault(-7.790595211984489);
                double longtitude = ctx.queryParamAsClass("long", Double.class).getOrDefault(110.37414550781251);
                ctx.json(getTimezoneAPI(latitude, longtitude).toString());
            })
            .get("/api/alamat", ctx -> {
                double latitude = ctx.queryParamAsClass("lat", Double.class).getOrDefault(-7.790595211984489);
                double longtitude = ctx.queryParamAsClass("long", Double.class).getOrDefault(110.37414550781251);
                ctx.json(
                    getReverseGeocodingData(latitude, longtitude).toString()
                );
            })
            .start(port);
    }


    public static JSONObject getWeatherJSON(){
        Dotenv dotenv = Dotenv.configure().directory(".").load();
        JSONObject response = Unirest.get(url)
                .queryString("lat", latitude)
                .queryString("lon", longtitude)
                .queryString("appid", dotenv.get("TOKEN_OPENWEATHER"))
                .asJson()
                .getBody()
                .getObject();
        return response.getJSONObject("main");
    }
    public static WeatherStatus getWeather(){
        Dotenv dotenv = Dotenv.configure().directory(".").load();
        JSONObject response = Unirest.get(url)
                .queryString("lat", latitude)
                .queryString("lon", longtitude)
                .queryString("appid", dotenv.get("TOKEN_OPENWEATHER"))
                .asJson()
                .getBody()
                .getObject()
                .getJSONObject("main");
        String mainString = response.toString();
        System.out.println(mainString);
        return gson.fromJson(mainString, WeatherStatus.class);
    }
    public static JSONObject getWeatherAPI(double latitude, double longtitude, String date, int hour){
        String url = "http://api.weatherapi.com/v1/forecast.json";
        Dotenv dotenv = Dotenv.configure().directory(".").load();
        JSONObject res = Unirest.get(url)
                .queryString("key", dotenv.get("TOKEN_WEATHER_API"))
                .queryString("q", latitude+","+longtitude)
                .queryString("days", 3)
                .queryString("aqi", "yes")
                .asJson()
                .getBody()
                .getObject();
        return res;
    }
    public static JSONObject getTimezoneAPI(double latitude, double longtitude){
        String apikey = "0XO0WV85UAVU";
        String url = "https://api.timezonedb.com/v2.1/get-time-zone";
        JSONObject res = Unirest.get(url)
                .queryString("key", apikey)
                .queryString("format", "json")
                .queryString("by", "position")
                .queryString("lat", latitude)
                .queryString("lng", longtitude)
                .asJson()
                .getBody()
                .getObject();
        return res;
    }
    public static JSONObject getReverseGeocodingData(double lat, double lng) {
        String url = "https://nominatim.openstreetmap.org/reverse";
        // get alamat
        try {
            HttpResponse<JsonNode> response = Unirest.get(url)
                    .queryString("format", "json")
                    .queryString("lat", lat)
                    .queryString("lon", lng)
                    .asJson();

            JSONObject responseBody = response.getBody().getObject();
            return responseBody;
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
    }
}