package com.example.pranitha.instagram;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pranitha.instagram.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListView extends Activity {
    String jsonString =null;
    Bitmap bmp;
    CustomGridAdapter adapter;
    ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    GridView gv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //We have set a grid view as the layout for this activity and fetched the json data passed from main activity into jsonstring
        setContentView(R.layout.activity_list_view);
        Intent intent = getIntent();
        jsonString = intent.getStringExtra("JSON_STRING");
        gv= (GridView) findViewById(R.id.list1);
        //here we have set onclicklistener to the grid view, so that when ever an item in grid view is clicked, it shows the image in a dialog box
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id)
            {

                HashMap<String, String> m = new HashMap<String, String>();
                m=list.get(position);
                int width= Integer.valueOf((String) m.get("width"));
                int height= Integer.valueOf((String) m.get("height"));
                //the loadphoto method displays the alert dialog
                loadPhoto((ImageView)findViewById(R.id.image),width,height);
            }
        });

        //here we are loading the jsonstring data into the grid view
        parseJsonString();
    }

    private void loadPhoto(ImageView imageView,int width,int height) {

        ImageView tempImageView = imageView;
        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_fullimage_dialog,
                (ViewGroup) findViewById(R.id.layout_root));
        ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
        image.setImageDrawable(tempImageView.getDrawable());
        imageDialog.setView(layout);
        imageDialog.setPositiveButton("OK", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });
        AlertDialog alertDialog = imageDialog.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(width*2, height*2);
    }

    //this method loads the json data into grid view
    public void parseJsonString(){
        try {
           // The json data is parsed here to get image url from images object
            JSONObject jsonObj = new JSONObject(jsonString);
            JSONArray Jlist = jsonObj.getJSONArray("data");
            for(int k=0;k<Jlist.length();k++)
            {
                JSONObject JObj = Jlist.getJSONObject(k);
                JSONObject jImages= JObj.getJSONObject("images");
                //the update map method adds the image URL to the array list which is updated in grid view
                updateMap(jImages);
            }
            //finally we define a custom adapter with listrow as layout for each item in the grid and list as input and set it to the grid view
            adapter = new CustomGridAdapter(getApplicationContext(), list,
                    R.layout.list_row, new String[]{}, new int[]{});
            gv.setAdapter(adapter);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateMap(JSONObject mJsonObj) {
        try {
            //There are 3 different URL for each image based on resolution, we are parsing those  data here and adding them to list
            HashMap<String, String> map = new HashMap<String, String>();
            JSONObject big = mJsonObj.getJSONObject("standard_resolution");
            String width = big.getString("width");
            String height = big.getString("height");
            String Url = big.getString("url");
            map.put("width", width);
            map.put("height", height);
            map.put("url", Url);
            list.add(map);

            HashMap<String, String> mapMedium = new HashMap<String, String>();
            JSONObject medium = mJsonObj.getJSONObject("low_resolution");
            String mWidth = medium.getString("width");
            String mHeight = medium.getString("height");
            String mUrl = medium.getString("url");
            mapMedium.put("width", mWidth);
            mapMedium.put("height", mHeight);
            mapMedium.put("url", mUrl);
            list.add(mapMedium);
            HashMap<String, String> mapSmall = new HashMap<String, String>();
            JSONObject small = mJsonObj.getJSONObject("thumbnail");
            String sWidth = small.getString("width");
            String sHeight = small.getString("height");
            String sUrl = small.getString("url");
            mapSmall.put("width", sWidth);
            mapSmall.put("height", sHeight);
            mapSmall.put("url", sUrl);
            list.add(mapSmall);

        }  catch(Exception e){
            Log.i("GetConnection", "Error in doInBackground" + e);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_view, menu);
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
}
