package com.example.ambulance_driver_app;

import android.content.Context;
import android.os.AsyncTask;

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

public class BackgroundEditUserWorker extends AsyncTask<String, Void, String> {

    Context context;

    public AsyncResponseString delegate = null;

    BackgroundEditUserWorker(Context cxt)
    {
        context=cxt;
    }

    @Override
    protected String doInBackground(String... strings) {

        String phone = strings[0];
        String name = strings[1];
        String email = strings[2];
        String address = strings[3];
        String password=strings[4];

        String  editDetail_url = context.getResources().getString(R.string.server_url)+"/editDriver.php";

        try {

            URL url = new URL(editDetail_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            String post_data = "";

                post_data = URLEncoder.encode("phone", "UTF-8")+"="+URLEncoder.encode(phone, "UTF-8")
                        + "&&" +URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode(name, "UTF-8")
                        + "&&" +URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(password, "UTF-8")
                        + "&&" +URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(email, "UTF-8")
                        + "&&" +URLEncoder.encode("address", "UTF-8")+"="+URLEncoder.encode(address, "UTF-8");

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
