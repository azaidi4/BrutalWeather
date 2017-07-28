package com.azcorp.brutalweather;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    final static String LOG_TAG = MainActivity.class.getSimpleName();
    final static String OPENWEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=muscat&appid=80bc327138765b4192f811176b725ebe";
    final static String WEATHER_ICON_URL = "http://openweathermap.org/img/w/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WeatherAsyncTask task = new WeatherAsyncTask();
        task.execute();
    }

    private Weather parseJSONFromString(String weatherString) {

        if (weatherString != null) {
            try {

                JSONObject root = new JSONObject(weatherString);
                JSONArray weatherJson = root.getJSONArray("weather");
                JSONObject mainJson = root.getJSONObject("main");

                double weatherTemp = (mainJson.getDouble("temp") - 273.15);
                String weatherTMain = weatherJson.getJSONObject(0).getString("main");
                String weatherDesc = weatherJson.getJSONObject(0).getString("description");
                String weatherCountry = root.getString("name");
                String iconID = weatherJson.getJSONObject(0).getString("icon");

                return new Weather(weatherTemp, weatherTMain, weatherDesc,
                        weatherCountry, "C", iconID);

            } catch (JSONException e) {
                Log.i(LOG_TAG, "parseJSONFromString Error in parsing JSON: ", e);
            }
        }
        return null;
    }


    private void updateWeatherAttributes(Weather weather) {

        TextView temp = (TextView) findViewById(R.id.temp_text_view);
        TextView main = (TextView) findViewById(R.id.main_text_view);
        TextView desc = (TextView) findViewById(R.id.description_text_view);
        TextView country = (TextView) findViewById(R.id.country_text_view);
        TextView quote = (TextView) findViewById(R.id.quote);

        temp.setText(String.valueOf((int) Math.floor(weather.getTemperature()))
                + (char) 0x00B0 + weather.getUnit());

        //Setting appropriate icon for weather
        weather.getWeatherIcon().setBounds(0, 0, 200, 200);
        temp.setCompoundDrawables(weather.getWeatherIcon(), null, null, null);

        main.setText(weather.getMain());
        desc.setText("With " + weather.getDescription());
        country.setText(weather.getCountry());
        quote.setText(weather.getWeatherPhrase());

        SharedPreferences sharedPref = this.getSharedPreferences("myprefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("phrase", weather.getWeatherPhrase());
        editor.commit();

    }

    private void populateArrayList(Weather weather) {
        int floorTemperature = (int) Math.floor(weather.getTemperature());
        Resources resources = getResources();
        Random rand = new Random();
        String[] tempArray = null;

        if (isBetween(floorTemperature, 30, 40)) {
            tempArray = resources.getStringArray(R.array.above30to40);
            weather.setWeatherPhrase(tempArray[rand.nextInt(tempArray.length)]);

        } else if (isBetween(floorTemperature, 40, 50)) {
//            tempArray = resources.getStringArray(R.array.above40to50);
//            weather.setWeatherPhrase(tempArray[rand.nextInt(tempArray.length)]);
            weather.setWeatherPhrase("Well You're SOL");
        } else {
//            weather.setWeatherPhrase("Well You're SOL");
            tempArray = resources.getStringArray(R.array.above40to50);
            weather.setWeatherPhrase(tempArray[rand.nextInt(tempArray.length)]);
           // Log.i("@populateArrayList", "Temperature out of range");
        }
    }

    boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    private class WeatherAsyncTask extends AsyncTask<URL, Void, Weather> {

        @Override
        protected Weather doInBackground(URL... params) {
            URL url = null;
            try {
                url = new URL(OPENWEATHER_URL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            String jsonString = makeHttpRequest(url);
            Weather weather = parseJSONFromString(jsonString);

            if (weather != null)
                weather.setWeatherIcon(generateWeatherIcon(weather.getImageResourceID()));

            return weather;
        }

        private Drawable generateWeatherIcon(String imageResourseID) {
            StringBuilder output = new StringBuilder(WEATHER_ICON_URL);
            output.append(imageResourseID).append(".png");

            try {
                URL url = new URL(output.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                if (urlConnection.getResponseCode() == 200) {
                    urlConnection.connect();
                    InputStream input = urlConnection.getInputStream();
                    Bitmap weatherIcon = BitmapFactory.decodeStream(input);
                    Drawable drawable = new BitmapDrawable(getResources(), weatherIcon);

                    return drawable;
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            if (weather == null)
                return;

            populateArrayList(weather);
            updateWeatherAttributes(weather);

        }

        private String makeHttpRequest(URL url) {
            String jsonResponse = "";

            if (url == null)
                return jsonResponse;

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {

            StringBuilder output = new StringBuilder();

            if (inputStream != null) {
                InputStreamReader inputStreamReader =
                        new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();

                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

    }

    public void updateWeatherWidget() {
        Intent intent = new Intent(this, WeatherWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = {R.id.appwidget_text};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);

        intent.putExtra("phrase", "hiii");
    }
}