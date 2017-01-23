package com.projects.fbgrecojr.vamoose;

import sourceCode.JSONParser;
import sourceCode.MenuListener;
import sourceCode.NavDrawerItem;
import sourceCode.NavDrawerListAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends Activity {

	private ImageView _home, _users, _settings, _calendar, _history, _power;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

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
			iv[i].setOnClickListener(new MenuListener(i, AdminActivity.this, AdminActivity.class));
		}
	}

}
