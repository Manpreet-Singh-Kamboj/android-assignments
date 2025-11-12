package com.manpreet.androidassignments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class WeatherForecast extends AppCompatActivity {
    private static final String ACTIVITY_NAME = "WeatherForecastActivity";
    private ProgressBar progressBar;
    private TextView currentTemp;
    private TextView maxTemp;
    private TextView minTemp;
    private ImageView weatherImage;
    private ArrayList<String> cities = new ArrayList<>();

    private class ForecastQuery extends AsyncTask<String, Integer, String> {
        private String min;
        private String max;
        private String current;
        private Bitmap image;
        private String city;

        public ForecastQuery(String city) {
            this.city = city;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q="
                        + this.city + "&APPID=79cecf493cb6e52d25bb7b7050ff723c&mode=xml&units=metric");

                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                InputStream in = conn.getInputStream();
                try {
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(in, null);
                    int type;

                    while ((type = parser.getEventType()) != XmlPullParser.END_DOCUMENT) {
                        if (type == XmlPullParser.START_TAG) {
                            if (parser.getName().equals("temperature")) {
                                current = parser.getAttributeValue(null, "value");
                                publishProgress(25);
                                min = parser.getAttributeValue(null, "min");
                                publishProgress(50);
                                max = parser.getAttributeValue(null, "max");
                                publishProgress(75);
                            } else if (parser.getName().equals("weather")) {
                                String iconName = parser.getAttributeValue(null, "icon");
                                String fileName = iconName + ".png";
                                Log.i("ForecastQuery", "Looking for file: " + fileName);

                                if (fileExistence(fileName)) {
                                    FileInputStream fis = openFileInput(fileName);
                                    Log.i("ForecastQuery", "Found the file locally");
                                    image = BitmapFactory.decodeStream(fis);
                                } else {
                                    String iconUrl = "https://openweathermap.org/img/w/" + fileName;
                                    image = getImage(new URL(iconUrl));
                                    FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                                    image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                    outputStream.flush();
                                    outputStream.close();
                                    Log.i("ForecastQuery", "Downloaded image from the web");
                                }
                                publishProgress(100);
                            }
                        }
                        parser.next();
                    }
                } finally {
                    in.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPreExecute(){
            progressBar.setVisibility(View.VISIBLE);
            currentTemp.setText("");
            minTemp.setText("");
            maxTemp.setText("");
            weatherImage.setImageBitmap(image);
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            currentTemp.setText(String.format("Current: %s°C", current));
            minTemp.setText(String.format("Min: %s°C", min));
            maxTemp.setText(String.format("Max: %s°C", max));
            weatherImage.setImageBitmap(image);
        }

        private boolean fileExistence(String fileName) {
            File file = getBaseContext().getFileStreamPath(fileName);
            return file.exists();
        }

        private Bitmap getImage(URL url) {
            try {
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weather_forecast);
        cities = loadCitiesFromJSON();
        Spinner spinner = findViewById(R.id.citiesDropdown);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, cities
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progress_bar);
        currentTemp = findViewById(R.id.current_temp);
        maxTemp = findViewById(R.id.max_temp);
        minTemp = findViewById(R.id.min_temp);
        weatherImage = findViewById(R.id.weather_image);
        progressBar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        new ForecastQuery("kitchener").execute();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = cities.get(position);
                Log.i("SELECTED: ",selectedCity);
                new ForecastQuery(selectedCity).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private ArrayList<String> loadCitiesFromJSON(){
        ArrayList<String> cities = new ArrayList<>();
        try {
            InputStream is = getAssets().open("cities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject cityObj = jsonArray.getJSONObject(i);
                String cityName = cityObj.getString("name");
                cities.add(cityName);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading cities", Toast.LENGTH_SHORT).show();
        }

        return cities;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}