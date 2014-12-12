package com.example.pranitha.instagram;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class MyActivity extends Activity {

    //Declaring the authorization URL, Token URL , Api URL and Callback URL
    private static String AUTHURL = "https://api.instagram.com/oauth/authorize/";
    private static final String TOKENURL = "https://api.instagram.com/oauth/access_token";
    public static final String APIURL = "https://api.instagram.com/v1";
    public static String CALLBACKURL = "https://success";
    private static String authURLString,tokenURLString,clientId,clientSecret,jsonresultdata;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        //The client Id and client Secret obtained when registered for app in instagram site
        clientId = getString(R.string.clientId);
        clientSecret = getString(R.string.clientSecret);
        //Adding the Client Id and client secret to auth URL and token URL
         authURLString = AUTHURL + "?client_id=" + clientId + "&redirect_uri=" + CALLBACKURL + "&response_type=code";
         tokenURLString = TOKENURL + "?client_id=" + clientId + "&client_secret=" + clientSecret + "&redirect_uri=" + CALLBACKURL + "&grant_type=authorization_code";
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new AuthWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
       // Loading the webview with the authorization URL, it redirects to instagram login page
        webView.loadUrl(authURLString);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class AuthWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            // once the callback is successful we get return message to this method. If it starts with Callback URL implies
            //the autorization is successful and this returns us a request token which we use further to obtain access token
            if (url.startsWith(CALLBACKURL))
            {
                System.out.println(url);
                String parts[] = url.split("=");
                String request_token = parts[1];
//after receiving request token, we are calling a asynchronous task with the request token as input where the data is loaded from server
                AsyncTaskRunner runner = new AsyncTaskRunner();
                runner.execute(request_token);

                return true;
            }
            return false;
        }
    }


/*This method is used to convert the input stream from HTTP url connection to string*/
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    /*This is the asynchronous task where data is loaded from server*/
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            String tagresponse = "";
            try
            {
                //we are requesting server for access token here using the clientid, client secret and request token obtained through authorization
                URL url = new URL(TOKENURL);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                outputStreamWriter.write("client_id="+clientId+"&client_secret="+ clientSecret +"&grant_type=authorization_code" +"&redirect_uri="+CALLBACKURL+"&code=" + params[0]);
                outputStreamWriter.flush();
                InputStream is1 = httpsURLConnection.getInputStream();
                String response = convertStreamToString(is1);

                //The response for token request is parsed here to get access token, user Id who has logged in and username
                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
                String accessTokenString = jsonObject.getString("access_token");
                String id = jsonObject.getJSONObject("user").getString("id");
                String username = jsonObject.getJSONObject("user").getString("username");
                //This is the URL to get images with tag #selfie obtained from Instagram API console
                String urlString = "https://api.instagram.com/v1/tags/selfie/media/recent?access_token="+accessTokenString;
                URL urlForImageAccess = new URL(urlString);
                InputStream inputStream = urlForImageAccess.openConnection().getInputStream();
                //Once we get the input stream data for the URL string, we convert it to string and store it in tagresponse
                //this is returned to on post execute method as input
                tagresponse = convertStreamToString(inputStream);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return tagresponse;
        }

        @Override
        protected void onPostExecute(String result) {
        // After loading data in background thread in do in background method, we pass this data to Listview activity to display it
            Intent intent = new Intent(getApplicationContext(), ListView.class);
            if(result!=null) {
                intent.putExtra("JSON_STRING", result);
            } else{
                intent.putExtra("JSON_STRING", "");
            }
            startActivity(intent);
        }
    }

}

