package com.projects.fbgrecojr.vamoose;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import security.PasswordHash;
import sourceCode.GMailSender;
import sourceCode.JSONParser;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import sourceCode.Query;
import sourceCode.Session;
import sourceCode.themeUtils;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener{

    private EditText userName;
    private EditText password;
    private Button login;
    private TextView title;

    // Progress Dialog
    private ProgressDialog pDialog;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    //php login script location:
    //localhost : testing on your device
    //put your local ip instead,  on windows, run CMD > ipconfig
    //or in mac's terminal type ifconfig and look for the ip under en0 or en1
    // private static final String LOGIN_URL = "http://xxx.xxx.x.x:1234/webservice/login.php";
    //testing on Emulator:
    //private static final String LOGIN_URL = "http://10.0.2.2:1234/webservice/login.php";
    //testing from a real server:
    private static final String LOGIN_URL = "http://www.frankgrecojr.com/webservice/getStoredPassword.php";
    private static final String LOGIN_SUCCESS_URL = "http://www.frankgrecojr.com/webservice/loginSuccessful.php";
    private static final String INSERT_TOKEN_URL = "http://www.frankgrecojr.com/webservice/insertSecureToken.php";
    private static final String NEW_LOGIN_URL = "http://www.frankgrecojr.com/webservice/newUserLogin.php";
    private static final String GET_BRK_BUFFER_URL = "http://www.frankgrecojr.com/webservice/getBreakBuffer.php";

    //JSON element ids from response of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_PASSWORD = "password";
    private static final String TAG_ISADMIN = "isAdmin";
    private static final String TAG_LASTLOGIN = "lastLogin";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_PHONE = "phone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = (EditText) findViewById(R.id.userName);
        userName.setHint("Username");
        userName.requestFocus();
        password = (EditText) findViewById(R.id.password);
        //initialize EditText fields
        password.setHint("Password");
        login = (Button) findViewById(R.id.login);
        title = (TextView) findViewById(R.id.title);

        title.setTextSize(36);

        login.setOnClickListener(this);
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.login:
                if(userName.getText().toString().equals("") || userName.getText().toString().equals(" ")){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                    alertDialogBuilder.setMessage("Enter Your User Name");
                    alertDialogBuilder.setPositiveButton("Got it", null);
                    alertDialogBuilder.create().show();
                }else{
                    //LoginActivity.user = userName.getText().toString();
                    new AttemptLogin(userName.getText().toString(), password.getText().toString()).execute();
                }
                break;
        }
    }

    //AsyncTask is a seperate thread than the thread that runs the GUI
    //Any type of networking should be done with asynctask.
    class AttemptLogin extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        String username;
        String password;
        AlertDialog.Builder builder;
        String phone;
        String email;
        String theme;
        String token;
        AlertDialog alert;
        boolean isAdmin;

        public AttemptLogin(String username, String password){
            this.username = username;
            this.password = (password.equals("") ? " " : password);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            builder = new AlertDialog.Builder(LoginActivity.this);
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            String messageToReturn;
            if(!isOnline()){
                noInternetConnection();
                return "no connection";
            }
            else{
                try {
                    // Building Parameters
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("username", username));
                    params.add(new BasicNameValuePair("password", password));
                    Log.d("request!", "starting");
                    // getting product details by making HTTP request
                    JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params);
                    // check your log for json response
                    Log.d("Login attempt", json.toString());
                    if (json.getInt(TAG_SUCCESS) == 1) {
                        //has the user/admin ever logged in before?
                        this.email = json.getString(TAG_EMAIL);
                        this.phone = json.getString(TAG_PHONE);
                        Session.set_theme(json.getString("theme"));
                        Session.set_username(username);
                        Session.set_notification(json.getString("notification").equals("1"));
                        if(json.getString(TAG_LASTLOGIN).equals("0000-00-00 00:00:00")){ //nope - this is their first time logging in
                            //get secure token
                            this.token = Query.getRandomString();
                            System.out.println(token);
                            //salt/encrypt secure token
                            try{
                                String saltedToken = PasswordHash.createHash(token);
                                //insert secure token
                                // Building Parameters
                                params = new ArrayList<NameValuePair>();
                                params.add(new BasicNameValuePair("password", saltedToken));
                                params.add(new BasicNameValuePair("username", username));
                                Log.d("request!", "starting");
                                // getting product details by making HTTP request
                                json = jsonParser.makeHttpRequest(INSERT_TOKEN_URL, "POST", params);
                            }catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
                                return "Connection Timeout";
                            }
                            messageToReturn = "FIRST TIME";
                            return messageToReturn;
                        }
                        //check password
                        try{
                            boolean toReturn = PasswordHash.validatePassword(password, json.getString(TAG_PASSWORD));
                            if (toReturn) {
                                Log.d("Login Successful!", json.toString());
                                //if password is correct, isAdmin just always be either a 1 or 0;
                                if(json.getString(TAG_ISADMIN).equals("1")){
                                    //get admin settings
                                    HttpClient httpclient = new DefaultHttpClient();
                                    HttpPost httppost = new HttpPost(GET_BRK_BUFFER_URL);
                                    try{
                                        HttpResponse response = httpclient.execute(httppost);
                                        String length = EntityUtils.toString(response.getEntity());
                                        JSONObject jsonObject = new JSONObject(length);
                                        Session.set_brkLength(jsonObject.getString("brk_length"));
                                        Session.set_brkBuffer(jsonObject.getString("buffer"));
                                        Session.set_grnZone(jsonObject.getString("grn_zone"));
                                        Session.set_ylwZone(jsonObject.getString("ylw_zone"));
                                        Session.set_redZone(jsonObject.getString("red_zone"));
                                    }catch (Exception e){}
                                    isAdmin = true;
                                    Intent j = new Intent(LoginActivity.this, AdminActivity.class);
                                    finish();
                                    startActivity(j);
                                }else{
                                    isAdmin = false;
                                    Intent i = new Intent(LoginActivity.this, UserActivity.class);
                                    finish();
                                    startActivity(i);
                                }
                                //set lastLogin column in database
                                // Building Parameters
                                params = new ArrayList<NameValuePair>();
                                params.add(new BasicNameValuePair("username", username));
                                Log.d("request!", "starting");
                                // getting product details by making HTTP request
                                JSONObject json2 = jsonParser.makeHttpRequest(LOGIN_SUCCESS_URL, "POST", params);
                                messageToReturn = "Login Successful";
                                return messageToReturn;
                            }else{ //password was incorrect
                                return "Password Incorrect";
                            }
                        }catch(NoSuchAlgorithmException | InvalidKeySpecException e){
                            Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                            return "Login Failed";
                        }
                    }else{ //nothing was returned from the database
                        return "User Name Not Found";
                    }
                } catch (JSONException e) { //user name incorrect
                    return "Connection Timeout";
                }
            }
        }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if(file_url.equals("FIRST TIME")){
                sendSecureToken();
                //build AlertDialog
                builder
                        .setTitle("Welcome to Vamoose")
                        .setCancelable(false)
                        .setMessage("Check your email or messages, locate your secure token, and select 'continue'. If you did not receive it, select 'resend'.")
                        .setNegativeButton("resend", null)
                    .setPositiveButton("continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // get prompts.xml view
                            LayoutInflater li = LayoutInflater.from(LoginActivity.this);
                            View promptsView = li.inflate(R.layout.enter_token, null);
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                            // set prompts.xml to alertdialog builder
                            alertDialogBuilder.setView(promptsView);
                            final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                            final TextView errorToken = (TextView) promptsView.findViewById(R.id.textView2);
                            errorToken.setVisibility(View.INVISIBLE);
                            // set dialog message
                            alertDialogBuilder
                                    .setCancelable(false)
                                    .setPositiveButton("Continue", null)
                                    .setNegativeButton("Go Back",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    alert.show();
                                                }
                                            });

                            // create alert dialog
                            final AlertDialog alertDialog = alertDialogBuilder.create();

                            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                                @Override
                                public void onShow(DialogInterface dialog) {

                                    Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                    b.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            // TODO Do something
                                            LayoutInflater li = LayoutInflater.from(LoginActivity.this);
                                            View promptsView = li.inflate(R.layout.reset_password, null);
                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                                            // set prompts.xml to alertdialog builder
                                            alertDialogBuilder.setView(promptsView);
                                            final TextView passwordError = (TextView) promptsView.findViewById(R.id.textView2);
                                            passwordError.setVisibility(View.INVISIBLE);
                                            final EditText pass1 = (EditText) promptsView.findViewById(R.id.enterPassword);
                                            pass1.requestFocus();
                                            final EditText pass2 = (EditText) promptsView.findViewById(R.id.reenterPassword);
                                            if (userInput.getText().toString().equals(token)) {
                                                alertDialogBuilder
                                                        .setCancelable(false)
                                                        .setPositiveButton("Login", null);
                                                // create alert dialog
                                                final AlertDialog alertDialog = alertDialogBuilder.create();

                                                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                                                    @Override
                                                    public void onShow(DialogInterface dialog) {

                                                        Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                                        b.setOnClickListener(new View.OnClickListener() {

                                                            @Override
                                                            public void onClick(View view) {
                                                                // TODO Do something
                                                                if(pass1.getText().toString().equals("") || pass2.getText().toString().equals("")){
                                                                    passwordError.setVisibility(View.VISIBLE);
                                                                }
                                                                else if ((pass1.getText().toString().equals(pass2.getText().toString()) && !pass1.equals(""))) {
                                                                    //write pass to database
                                                                    //TODO
                                                                    if (isAdmin) {
                                                                        try {
                                                                            new newUserLogin(username, PasswordHash.createHash(pass1.getText().toString())).execute();
                                                                        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                                                                            System.err.println(e);
                                                                        }
                                                                        Intent j = new Intent(LoginActivity.this, AdminActivity.class);
                                                                        finish();
                                                                        startActivity(j);
                                                                    } else {
                                                                        try {
                                                                            new newUserLogin(username, PasswordHash.createHash(pass1.getText().toString())).execute();
                                                                        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                                                                            System.err.println(e);
                                                                        }
                                                                        Intent j = new Intent(LoginActivity.this, UserActivity.class);
                                                                        finish();
                                                                        startActivity(j);
                                                                    }
                                                                } else{
                                                                    //do something (passwords don't match)
                                                                    passwordError.setVisibility(View.VISIBLE);
                                                                    pass1.setSelectAllOnFocus(true);
                                                                    pass2.setSelectAllOnFocus(true);
                                                                }
                                                            }
                                                        });
                                                    }
                                                });

                                                // show it
                                                alertDialog.show();
                                            } else {
                                                //do something (token is incorrect)
                                                userInput.setSelectAllOnFocus(true);
                                                errorToken.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                                }
                            });

                            // show it
                            alertDialog.show();
                        }
                    });
                alert = builder.create();
                alert.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendSecureToken();
                            }
                        });
                    }
                });
                alert.show();
            }
            else if(file_url.equals("Password Incorrect")){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                alertDialogBuilder.setMessage("Oops! Password Incorrect");
                alertDialogBuilder.setPositiveButton("Got it", null);
                alertDialogBuilder.create().show();
            }
            else if(file_url.equals("User Name Not Found") && userName.getText().toString().length() > 0){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                alertDialogBuilder.setMessage("Oops! User Name Not Found");
                alertDialogBuilder.setPositiveButton("Got it", null);
                alertDialogBuilder.create().show();
            }
            else if(file_url.equals("Connection Timeout")){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                alertDialogBuilder.setMessage("Oops! Connection Timeout. Try Again");
                alertDialogBuilder.setPositiveButton("Got it", null);
                alertDialogBuilder.create().show();
            }
        }

        protected void sendSecureToken(){
            //email/message secure token
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, "Your secure token is: " + token, null, null);
            try {
                GMailSender sender = new GMailSender("grecoruppinnovations@gmail.com", "vamoose123");
                sender.sendMail("Vamoose!!",
                        "Your secure token is: " + token,
                        "fbgrecojr@gmail.com",
                        "email");
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }
        }
    }

    class newUserLogin extends AsyncTask<String, String, String>{

        String username;
        String password;

        public newUserLogin(String username, String password){
            this.username = username;
            this.password = password;
        }
        @Override
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            Log.d("request!", "starting");
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(NEW_LOGIN_URL, "POST", params);
            return "";
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void noInternetConnection(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        alertDialogBuilder.setMessage("Connection Timeout. Try Again.");
        alertDialogBuilder.setPositiveButton("Got it", null);
        alertDialogBuilder.create().show();
    }

    /*public static String getUser() {
        return user;
    }*/
}