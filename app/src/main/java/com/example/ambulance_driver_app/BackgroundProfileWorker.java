package com.example.ambulance_driver_app;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BackgroundProfileWorker extends AsyncTask<String, Void, String> {
    Context context;
    private String TAG = "BackgroundLoginWorker";

    public AsyncResponseAddressEmail delegate = null;

    public BackgroundProfileWorker(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        String phone = strings[0];
        String getDetailsUrl = "https://quickcare.000webhostapp.com/driverInfo.php/?phone=" + phone;
        String inputLine = "", result = "";
        try {
            URL url = new URL(getDetailsUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            //Create a new InputStreamReader
            InputStreamReader streamReader = new
                    InputStreamReader(httpURLConnection.getInputStream());

            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            //Check if the line we are reading is not null
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }

            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();

            //Set our result equal to our stringBuilder
            result = stringBuilder.toString();

            Log.e("Email-Address fetch", result);
            return result;


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("Hererre", "Returning NULL");
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        delegate.processStringEmailAddressFinish(s);
    }
}
