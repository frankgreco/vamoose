package com.projects.fbgrecojr.vamoose;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import sourceCode.MenuListener;

public class HistoryActivity extends Activity {

    private ImageView _home, _users, _settings, _calendar, _history, _power;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

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
            iv[i].setOnClickListener(new MenuListener(i, HistoryActivity.this, HistoryActivity.class));
        }
    }
}
