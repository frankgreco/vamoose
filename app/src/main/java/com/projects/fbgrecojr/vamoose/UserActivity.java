package com.projects.fbgrecojr.vamoose;

import sourceCode.JSONParser;
import sourceCode.NavDrawerItem;
import sourceCode.NavDrawerListAdapter;
import sourceCode.Session;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends Activity {
    //private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private static final String LOG_OFF_URL = "http://www.frankgrecojr.com/webservice/logoff.php";

    // slider menu items details
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // getting items of slider from array
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // getting Navigation drawer icons from res
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerList.setClickable(false);

        navDrawerItems = new ArrayList<NavDrawerItem>();


        // list item in slider at 1 Home Nasdaq details
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // list item in slider at 2 Facebook details
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // list item in slider at 3 Google details
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // list item in slider at 4 Apple details
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));


        // Recycle array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting list adapter for Navigation Drawer
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);
    }

    /**
     * Slider menu item click listener
     * */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected item
            displayView(position);
        }
    }

    private void displayView(int position) {
        // update the main content with called Fragment
        Intent i;
        switch (position) {
            case 0:
                i = new Intent(UserActivity.this, AdminActivity.class);
                startActivity(i);
                break;
            case 1:
                i = new Intent(UserActivity.this, ContactsActivity.class);
                startActivity(i);
                break;
            case 2:
                i = new Intent(UserActivity.this, AdminSettings.class);
                startActivity(i);
                break;
            case 3:
                new LogOff(Session.get_username()).execute();
                break;

            default:
                break;
        }

    }
    class LogOff extends AsyncTask<String, String, String> {

        private String userName;

        public LogOff(String userName){
            this.userName = userName;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            Intent i = new Intent(UserActivity.this, LoginActivity.class);
            startActivity(i);
        }

        @Override
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", userName));
            Log.d("request!", "starting");
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(LOG_OFF_URL, "POST", params);
            return "Complete";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserActivity.this);
            pDialog.setMessage("Logging Off...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
    }

}
