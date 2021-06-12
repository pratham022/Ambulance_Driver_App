package com.example.ambulance_driver_app;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class BackgroundGetRideDetails extends AsyncTask<String, Void, String>{
    Context context;
    private String TAG = "BackgroundGetRideDetails";

    public AsyncResponseString delegate = null;

    BackgroundGetRideDetails(Context ctx) {
        context = ctx;
    }

    @Override
    protected String doInBackground(String... strings) {

        String customerPhone = strings[0];
        String driverPhone = strings[1];
        String login_url = "https://quickcare.000webhostapp.com/getCustomerRideDetails.php";
        try {
            URL url = new URL(login_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            String post_data = URLEncoder.encode("customerPhone", "UTF-8")+"="+URLEncoder.encode(customerPhone, "UTF-8")
                    + "&&" +URLEncoder.encode("driverPhone", "UTF-8")+"="+URLEncoder.encode(driverPhone, "UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            String result = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            Log.d(TAG, result);

            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        delegate.processStringFinish(s);
    }
}
