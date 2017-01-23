package com.projects.fbgrecojr.vamoose;

import sourceCode.JSONParser;
import sourceCode.Session;
import sourceCode.themeUtils;
import sourceCode.MenuListener;
import sourceCode.ResetPassword;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminSettings extends Activity {

    private ImageView _home, _users, _settings, _calendar, _history, _power;
    private Switch _switch;
    private NumberPicker _numberPicker, _np1, _np2, _np3;
    private LinearLayout _item1, _item2, _item3, _item4, _item5, _item6, _item8;
    private View.OnClickListener _itemClick;
    private static final String SORT_SETTING_URL = "http://www.frankgrecojr.com/webservice/changeSortSetting.php";
    private static final String THEME_SETTING_URL = "http://www.frankgrecojr.com/webservice/changeTheme.php";
    private static final String NOTIFICATION_SETTING_URL = "http://www.frankgrecojr.com/webservice/changeNotificationSetting.php";
    private static final String BRK_LGTH_SETTING_URL = "http://www.frankgrecojr.com/webservice/changeBreakLength.php";
    private static final String BRK_BUFFER_SETTING_URL = "http://www.frankgrecojr.com/webservice/setBreakBuffer.php";
    private static final String ZONE_SETTING_URL = "http://www.frankgrecojr.com/webservice/changeZoneSettings.php";



    private JSONParser _jsonParser = new JSONParser();
    private AlertDialog.Builder _builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeUtils.setcTheme(Session.getAppThemeInt());
        themeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_admin_settings);

        _itemClick = new Click();

        _item1 = (LinearLayout) findViewById(R.id.list_item_1);
        _item1.setOnClickListener(_itemClick);
        _item2 = (LinearLayout) findViewById(R.id.list_item_2);
        _item2.setOnClickListener(_itemClick);
        _item3 = (LinearLayout) findViewById(R.id.list_item_3);
        _item3.setOnClickListener(_itemClick);
        _item4 = (LinearLayout) findViewById(R.id.list_item_4);
        _item4.setOnClickListener(_itemClick);
        _item5 = (LinearLayout) findViewById(R.id.list_item_5);
        _item5.setOnClickListener(_itemClick);
        _item6 = (LinearLayout) findViewById(R.id.list_item_6);
        _item6.setOnClickListener(_itemClick);
        _item8 = (LinearLayout) findViewById(R.id.list_item_8);
        _item8.setOnClickListener(_itemClick);

        _switch = (Switch) findViewById(R.id.switch1);
        _switch.setChecked(Session.is_notification());
        _switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String toPass = "0";
                if(b) toPass = "1";
                new ChangeSetting(3, NOTIFICATION_SETTING_URL, Session.get_username(), toPass, null, null, null).execute();
            }
        });

        _home = (ImageView) findViewById(R.id.homeButton);
        _users = (ImageView) findViewById(R.id.usersButton);
        _settings = (ImageView) findViewById(R.id.settingsButton);
        _calendar = (ImageView) findViewById(R.id.calendarButton);
        _history = (ImageView) findViewById(R.id.speedButton);
        _power = (ImageView) findViewById(R.id.powerButton);
        initializeMenuButtons(new ImageView[]{_home, _users, _settings, _calendar, _history, _power});
    }

    public void initializeMenuButtons(ImageView[] iv){
        for(int i = 0; i<iv.length; ++i){
            iv[i].setOnClickListener(new MenuListener(i, AdminSettings.this, AdminSettings.class));
        }
    }

    class Click implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.list_item_1:
                    String first = "First name (e.g. <b>John</b> Doe)";
                    String last = "Last name (e.g. John <b>Doe</b>)";
                    final CharSequence[] options = {Html.fromHtml(first), Html.fromHtml(last)};
                    _builder = new AlertDialog.Builder(AdminSettings.this);
                    _builder.setTitle("Sort Users");
                    _builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (item == 0)
                            {
                                new ChangeSetting(0, SORT_SETTING_URL, Session.get_username(), "0", null, null, null).execute();
                            }
                            else if (item == 1)
                            {
                                new ChangeSetting(0, SORT_SETTING_URL, Session.get_username(), "1", null, null, null).execute();
                            }
                        }
                    });
                    _builder.show();
                    break;
                case R.id.list_item_2:
                    new ResetPassword(Session.get_username(), AdminSettings.this);
                    break;
                case R.id.list_item_3:
                    final CharSequence[] options2 = {"easter","soft", "girl", "summer", "warm"};
                    _builder = new AlertDialog.Builder(AdminSettings.this);
                    _builder.setTitle("Pick a theme");
                    _builder.setItems(options2, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            String toPassTheme = "";
                            switch (item) {
                                case 0:
                                    themeUtils.changeToTheme(AdminSettings.this, themeUtils.EASTER);
                                    Session.set_theme("easter");
                                    toPassTheme = "easter";
                                    break;
                                case 1:
                                    themeUtils.changeToTheme(AdminSettings.this, themeUtils.SOFT);
                                    Session.set_theme("soft");
                                    toPassTheme = "soft";
                                    break;
                                case 2:
                                    themeUtils.changeToTheme(AdminSettings.this, themeUtils.GIRL);
                                    Session.set_theme("girl");
                                    toPassTheme = "girl";
                                    break;
                                case 3:
                                    themeUtils.changeToTheme(AdminSettings.this, themeUtils.SUMMER);
                                    Session.set_theme("summer");
                                    toPassTheme = "summer";
                                    break;
                                case 4:
                                    themeUtils.changeToTheme(AdminSettings.this, themeUtils.WARM);
                                    Session.set_theme("warm");
                                    toPassTheme = "warm";
                                    break;
                            }
                            new ChangeSetting(2, THEME_SETTING_URL, Session.get_username(), toPassTheme, null, null, null).execute();
                        }
                    });
                    _builder.show();
                    break;
                case R.id.list_item_4:
                    LayoutInflater li = LayoutInflater.from(AdminSettings.this);
                    View promptsView = li.inflate(R.layout.break_length, null);

                    _numberPicker = (NumberPicker) promptsView.findViewById(R.id.breakLength);
                    _numberPicker.setMinValue(1);
                    _numberPicker.setMaxValue(60);
                    _numberPicker.setValue(Integer.valueOf(Session.get_brkLength()));

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AdminSettings.this);
                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    alertDialogBuilder.setPositiveButton("OK, CHANGE IT!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new ChangeSetting(4, BRK_LGTH_SETTING_URL, Session.get_username(), Integer.toString(_numberPicker.getValue()), null, null, null).execute();
                            Session.set_brkLength(Integer.toString(_numberPicker.getValue()));
                        }
                    });

                    final AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                    break;
                case R.id.list_item_5:
                    li = LayoutInflater.from(AdminSettings.this);
                    promptsView = li.inflate(R.layout.zone_settings, null);

                    _np1 = (NumberPicker) promptsView.findViewById(R.id.zoneRed);
                    _np1.setMinValue(1);
                    _np1.setMaxValue(100);
                    _np1.setValue(Integer.parseInt(Session.get_redZone()));

                    _np2 = (NumberPicker) promptsView.findViewById(R.id.zoneYellow);
                    _np2.setMinValue(1);
                    _np2.setMaxValue(100);
                    _np2.setValue(Integer.parseInt(Session.get_ylwZone()));

                    _np3 = (NumberPicker) promptsView.findViewById(R.id.zoneGreen);
                    _np3.setMinValue(1);
                    _np3.setMaxValue(100);
                    _np3.setValue(Integer.parseInt(Session.get_grnZone()));

                    alertDialogBuilder = new AlertDialog.Builder(AdminSettings.this);
                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    alertDialogBuilder.setPositiveButton("OK, CHANGE IT!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new ChangeSetting(5, ZONE_SETTING_URL, Session.get_username(), Integer.toString(_np1.getValue()), Integer.toString(_np2.getValue()), Integer.toString(_np3.getValue()), null).execute();
                            Session.set_redZone(Integer.toString(_np1.getValue()));
                            Session.set_ylwZone(Integer.toString(_np2.getValue()));
                            Session.set_grnZone(Integer.toString(_np3.getValue()));
                        }
                    });

                    final AlertDialog alertDialogZone = alertDialogBuilder.create();

                    // show it
                    alertDialogZone.show();
                    break;
                case R.id.list_item_6:
                    li = LayoutInflater.from(AdminSettings.this);
                    promptsView = li.inflate(R.layout.break_buffer, null);

                    _numberPicker = (NumberPicker) promptsView.findViewById(R.id.breakLength);
                    _numberPicker.setMinValue(1);
                    _numberPicker.setMaxValue(60);
                    _numberPicker.setValue(Integer.valueOf(Session.get_brkBuffer()));

                    alertDialogBuilder = new AlertDialog.Builder(AdminSettings.this);
                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    alertDialogBuilder.setPositiveButton("OK, CHANGE IT!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new ChangeSetting(6, BRK_BUFFER_SETTING_URL, Session.get_brkBuffer(), Integer.toString(_numberPicker.getValue()), null, null, null).execute();
                            Session.set_brkBuffer(Integer.toString(_numberPicker.getValue()));
                        }
                    });

                    final AlertDialog alertDialogBuffer = alertDialogBuilder.create();

                    // show it
                    alertDialogBuffer.show();
                    break;
                case R.id.list_item_8:
                    startActivity(new Intent(AdminSettings.this, AboutActivity.class));
                    break;
            }
        }
    }

    class ChangeSetting extends AsyncTask{

        private int _position;
        private String _URL;
        private String _param1;
        private String _param2;
        private String _param3;
        private String _param4;
        private String _param5;
        private AlertDialog.Builder _builder;
        private ProgressDialog _pDialog;

        public ChangeSetting(int position, String URL, String param1, String param2, String param3, String param4, String param5){
            this._position = position;
            this._URL = URL;
            this._param1 = param1;
            this._param2 = param2;
            this._param3 = param3;
            this._param4 = param4;
            this._param5 = param5;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _builder = new AlertDialog.Builder(AdminSettings.this);
            _pDialog = new ProgressDialog(AdminSettings.this);
            _pDialog.setMessage("Updating...");
            _pDialog.setIndeterminate(false);
            _pDialog.setCancelable(false);
            //_pDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] args) {
            switch (this._position){
                case 0:
                    // Building Parameters
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("sort", this._param2));
                    params.add(new BasicNameValuePair("username", this._param1));
                    Log.d("request!", "starting");
                    // getting product details by making HTTP request
                    JSONObject json = _jsonParser.makeHttpRequest(this._URL, "POST", params);
                    // check your log for json response
                    Log.d("change setting attempt", json.toString());
                    break;
                case 1:
                    break;
                case 2:
                    params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("theme", this._param2));
                    params.add(new BasicNameValuePair("username", this._param1));
                    Log.d("request!", "starting");
                    // getting product details by making HTTP request
                    json = _jsonParser.makeHttpRequest(this._URL, "POST", params);
                    // check your log for json response
                    Log.d("change setting attempt", json.toString());
                    break;
                case 3:
                    params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("notification", this._param2));
                    params.add(new BasicNameValuePair("username", this._param1));
                    Log.d("request!", "starting");
                    // getting product details by making HTTP request
                    json = _jsonParser.makeHttpRequest(this._URL, "POST", params);
                    // check your log for json response
                    Log.d("change setting attempt", json.toString());
                    break;
                case 4:
                    params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("length", this._param2));
                    params.add(new BasicNameValuePair("username", this._param1));
                    Log.d("request!", "starting");
                    // getting product details by making HTTP request
                    json = _jsonParser.makeHttpRequest(this._URL, "POST", params);
                    // check your log for json response
                    Log.d("change setting attempt", json.toString());
                    break;
                case 5:
                    params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("green", this._param4));
                    params.add(new BasicNameValuePair("yellow", this._param3));
                    params.add(new BasicNameValuePair("red", this._param2));
                    Log.d("request!", "starting");
                    // getting product details by making HTTP request
                    json = _jsonParser.makeHttpRequest(this._URL, "POST", params);
                    // check your log for json response
                    Log.d("change setting attempt", json.toString());
                    break;
                case 6:
                    params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("buffer", this._param2));
                    params.add(new BasicNameValuePair("username", this._param1));
                    Log.d("request!", "starting");
                    // getting product details by making HTTP request
                    json = _jsonParser.makeHttpRequest(this._URL, "POST", params);
                    // check your log for json response
                    Log.d("change setting attempt", json.toString());
                    break;
                case 7:
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            //_pDialog.dismiss();
        }
    }


}
