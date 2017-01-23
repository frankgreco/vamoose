package com.projects.fbgrecojr.vamoose;

import sourceCode.ContactsAdapter;
import sourceCode.JSONParser;
import sourceCode.MenuListener;
import sourceCode.NavDrawerItem;
import sourceCode.NavDrawerListAdapter;
import sourceCode.Session;
import sourceCode.SortingName;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends Activity {
    private ListView mUsers;
    private static final String GET_ALL_USERS_URL = "http://www.frankgrecojr.com/webservice/getAllUsers.php";
    private static final String GET_ALL_USERS_LAST_URL = "http://www.frankgrecojr.com/webservice/getAllLastName.php";
    private static final String GET_SORT_PREF_URL = "http://www.frankgrecojr.com/webservice/getSortPref.php";
    private ArrayList<String> _firstNames;
    private ArrayList<String> _lastNames;
    private ArrayList<String> _fullName;

    private ImageView _home, _users, _settings, _calendar, _history, _power;

    private Button addUser;

    private ArrayList<NavDrawerItem> usersList;
    private ContactsAdapter usersAdapter;

    private ProgressDialog pDialog;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    public ContactsActivity(){
        _firstNames = new ArrayList<String>();
        _lastNames = new ArrayList<String>();
        _fullName = new ArrayList<String>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        addUser = (Button) findViewById(R.id.newUser);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ContactsActivity.this, AddUser.class);
                i.putExtra("option", "new");
                startActivity(i);
            }
        });
        mUsers = (ListView) findViewById(R.id.contacts);

        usersList = new ArrayList<NavDrawerItem>();

        _home = (ImageView) findViewById(R.id.homeButton);
        _users = (ImageView) findViewById(R.id.usersButton);
        _settings = (ImageView) findViewById(R.id.settingsButton);
        _calendar = (ImageView) findViewById(R.id.calendarButton);
        _history = (ImageView) findViewById(R.id.speedButton);
        _power = (ImageView) findViewById(R.id.powerButton);
        initializeMenuButtons(new ImageView[]{_home, _users, _settings, _calendar, _history, _power});

        new GetAllUsers(_firstNames, _lastNames, _fullName).execute();

    }

    class GetAllUsers extends AsyncTask<String, String, String> {

        private ArrayList<String> _firstNames;
        private ArrayList<String> _lastNames;
        private ArrayList<String> _fullName;
        private int _abc;

        public GetAllUsers(ArrayList<String> f, ArrayList<String> l, ArrayList<String> fn){
            this._firstNames = f;
            this._lastNames = l;
            this._fullName = fn;
            this._abc = -1;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            this._fullName = SortingName.sortLast(_fullName, this._abc);
            for(int i = 0; i < _firstNames.size(); ++i){
                String toAdd = this._abc == 0 ? "<b>" + _fullName.get(i).toString().split(" ")[0] + "</b>" + " " + _fullName.get(i).toString().split(" ")[1]: _fullName.get(i).toString().split(" ")[0] + " " + "<b>" + _fullName.get(i).toString().split(" ")[1] + "</b>";
                usersList.add(new NavDrawerItem(toAdd, -1));
            }

            // setting list adapter for Navigation Drawer
            usersAdapter = new ContactsAdapter(ContactsActivity.this, usersList);
            mUsers.setAdapter(usersAdapter);

            mUsers.setOnItemClickListener(new ContactsClickListener());

            pDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", Session.get_username()));
            Log.d("request!", "starting");
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GET_SORT_PREF_URL, "POST", params);
            //1 == sort by last
            //0 == sort by first
            try{
                this._abc = json.getInt("sort");
            }catch (JSONException e){System.err.println(e);}

            String resultFirst = "";
            String resultLast = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(GET_ALL_USERS_URL);
            try{
                HttpResponse response = httpclient.execute(httppost);
                resultFirst = EntityUtils.toString(response.getEntity());

                httppost = new HttpPost(GET_ALL_USERS_LAST_URL);
                response = httpclient.execute(httppost);
                resultLast = EntityUtils.toString(response.getEntity());
            }catch (Exception e){}
            try{
                JSONArray jsonArray = new JSONArray(resultFirst);
                JSONArray jsonArrayLast = new JSONArray(resultLast);
                for(int i = 0; i<jsonArray.length(); ++i){
                    _firstNames.add(jsonArray.get(i).toString());
                    _lastNames.add(jsonArrayLast.get(i).toString());
                    _fullName.add(jsonArray.get(i).toString() + " " + jsonArrayLast.get(i).toString());
                }
            }catch (JSONException e){
            }
            return "Complete";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ContactsActivity.this);
            pDialog.setMessage("Getting Users...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
    }

    /**
     * Slider menu item click listener
     * */
    private class ContactsClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected item
            getUser(position);
        }
    }

    private void getUser(int position){
        String user = _fullName.get(position);
        String[] name = user.split(" ");
        String first = name[0];
        String last = name[1];
        Intent i = new Intent(ContactsActivity.this, User.class);
        i.putExtra("first", first);
        i.putExtra("last", last);
        startActivity(i);
    }

    public void initializeMenuButtons(ImageView[] iv){
        for(int i = 0; i<iv.length; ++i){
            iv[i].setOnClickListener(new MenuListener(i, ContactsActivity.this, ContactsActivity.class));
        }
    }

}
