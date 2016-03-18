package com.elec2b.projectsunshine;

import android.annotation.TargetApi;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LENOVO on 3/17/2016.
 */



    /* A placeholder fragment containing a simple view*/
@TargetApi(Build.VERSION_CODES.HONEYCOMB)

public class ForecastFragment extends Fragment {


    private ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh ){
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute();
            return true;
        }
        return  super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        //Once the root view for the Fragment has been created, it's ....
        // the ListView with some dummy data.

        //Create some dummy data for the ListView. Here's a sample.....
        //represented as "day, whether, high/low"

        String[] forecastArray = {
                "Today - Sunny - 88/63",
                "Tomorrow - Foggy - 70/40",
                "Weds - Cloudy - 72/63",
                "Thurs - Asteroids - 75/65",
                "Fri - Heavy Rain - 65/56",
                "Sat - HELP TRAPPED IN WEATHER STATION- 60/51",
                "Sun - Sunny - 80/68",

                "Today - Sunny - 88/63",
                "Tomorrow - Foggy - 70/40",
                "Weds - Cloudy - 72/63",
                "Thurs - Asteroids - 75/65"

        };


        List<String> weekForecast = new ArrayList<String>(
                Arrays.asList(forecastArray));


        //Now that we have some dummy forecast data, create an ArrayAdapter.
        //The ARRAYADAPTER will take data from a source(like our dummy forecast data)


        //use it to populate the ListView it's attached to.

        mForecastAdapter =new ArrayAdapter<String>(
                getActivity(),//The current context (this fragment's parent activity
                R.layout.list_item_forecast,//ID of list Item Layout
                R.id.list_item_forecast_textview,//ID of the textview to populate
                weekForecast);//Forecast data

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Get a reference to the ListView, and attach this adapter to ListView
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<Void, Void, Void>{

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {

            //These two need to be declared outside the try/catch
            //so that they can be closed in the finally block.

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try{
                //Construct the URL for the OpenWeatherMap query
                //Possible parameters are available at OWM's  forecast API page, at
                // http://openweathermap.org/API#forecast

                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043%mode=json&units=metrics&cnt=7");

                //Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                //Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(inputStream==null){
                    //Nothing to do
                    return null;
                }


                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while((line = reader.readLine()) != null){
                    //Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    buffer.append(line+"\n");
                }

                if(buffer.length() == 0){
                    //Stream was empty. No point in parsing
                    return null;
                }
            } catch (java.io.IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }

                if (reader != null){
                    try{
                        reader.close();
                    } catch (final IOException e){
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }
}
