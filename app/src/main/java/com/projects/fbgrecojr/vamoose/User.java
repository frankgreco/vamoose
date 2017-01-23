package com.projects.fbgrecojr.vamoose;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import sourceCode.JSONParser;

public class User extends Activity {

    private String _first;
    private String _last;
    private String _username;
    private String _phone;
    private String _email;
    private boolean _isAdmin;
    private String _lastLogin;
    private Button _delete, _edit;
    private ImageView _image;
    private String image;

    private static final String GET_CONTACT_INFO_URL = "http://www.frankgrecojr.com/webservice/getContactInfo.php";
    private static final String DELETE_USER_URL = "http://www.frankgrecojr.com/webservice/deleteUser.php";



    private TextView userView, nameView, phoneView, emailView, adminView, loginView;

    private ProgressDialog pDialog;
    private JSONParser jsonParser = new JSONParser();

    public User(){
        this._first = "";
        this._last = "";
        this._username = "";
        this._phone = "";
        this._email = "";
        this._isAdmin = false;
        this._lastLogin = "";
        this.image = "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user2);

        _image = (ImageView) findViewById(R.id.imageView1);
        userView = (TextView) findViewById(R.id.contactUsername);
        nameView = (TextView) findViewById(R.id.contactName);
        phoneView = (TextView) findViewById(R.id.contactPhone);
        emailView = (TextView) findViewById(R.id.contactEmail);
        adminView = (TextView) findViewById(R.id.contactAdmin);
        loginView = (TextView) findViewById(R.id.contactLastLogin);
        _delete = (Button) findViewById(R.id.delete);
        _delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(User.this);
                alertDialogBuilder.setMessage("Do you wish to delete this user?");
                alertDialogBuilder.setNegativeButton("Go Back", null);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] userSplit = userView.getText().toString().split(" ");
                        new DeleteUser(userSplit[1].toString()).execute();
                    }
                });
                alertDialogBuilder.create().show();
            }
        });
        _edit = (Button) findViewById(R.id.edit);
        _edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] name = nameView.getText().toString().split(" ");
                String[] user = userView.getText().toString().split(" ");
                Intent i = new Intent(User.this, AddUser.class);
                i.putExtra("option", "edit");
                i.putExtra("first", name[1].toString());
                i.putExtra("last", name[2].toString());
                i.putExtra("username", user[1].toString());
                i.putExtra("email", emailView.getText().toString().split(" ")[1].toString());
                i.putExtra("phone", phoneView.getText().toString().split(" ")[1].toString() + " " + phoneView.getText().toString().split(" ")[2].toString());
                i.putExtra("admin", adminView.getText().toString().split(" ")[2].equals("not") ? "false" : "true");
                i.putExtra("image", image);
                startActivity(i);
            }
        });

        Intent i = getIntent();
        this._first = i.getStringExtra("first");
        this._last = i.getStringExtra("last");

        new GetUserInfo(_first, _last, _username, _phone, _email, _isAdmin, _lastLogin).execute();
    }

    public static Bitmap squareToCircle(Bitmap bitmap){
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    class GetUserInfo extends AsyncTask{

        private String first;
        private String last;
        private String username;
        private String phone;
        private String email;
        private boolean admin;
        private String login;
        private Bitmap bm;

        public GetUserInfo(String first, String last, String username, String phone, String email, boolean admin, String login){
            this.first = first;
            this.last = last;
            this.username = username;
            this.phone = phone;
            this.email = email;
            this.admin = admin;
            this.login = login;
            this.bm = null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(User.this);
            pDialog.setMessage("Getting " + first + "'s Info...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("first", this.first));
            params.add(new BasicNameValuePair("last", this.last));
            Log.d("request!", "starting");
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GET_CONTACT_INFO_URL, "POST", params);
            try{
                this.username = json.getString("username").toString();
                this.phone = json.getString("phone").toString();
                this.email = json.getString("email").toString();
                this.login = json.getString("lastLogin").toString();
                this.admin = json.getString("isAdmin").toString().equals("1") ? true : false;
                image = json.getString("image").toString();
                byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                this.bm = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            }catch(JSONException e){System.err.println(e);}
            return "Complete";
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            nameView.setText(Html.fromHtml("<b>Name </b>" + this.first + " " + this.last));
            userView.setText(Html.fromHtml("<b>Username </b>" + this.username));
            phoneView.setText(Html.fromHtml("<b>Phone </b>" + this.phone));
            emailView.setText(Html.fromHtml("<b>Email </b>" + this.email));
            adminView.setText(this.first + " is" + (this.admin == true ? " " : " not ") + "an administrator");
            String date = this.login.equals("0000-00-00 00:00:00") ? this.first + " has not logged in yet" : formatDate(this.login);
            loginView.setText(Html.fromHtml("<b>Last Login </b>" + date));
            _image.setImageBitmap(User.squareToCircle(bm));
            pDialog.dismiss();
        }

        private String formatDate(String x){
            String[] split1 = x.split(" ");
            String[] date = split1[0].split("-");
            String monthString = "";
            switch (Integer.parseInt(date[1])) {
                case 1:  monthString = "January";       break;
                case 2:  monthString = "February";      break;
                case 3:  monthString = "March";         break;
                case 4:  monthString = "April";         break;
                case 5:  monthString = "May";           break;
                case 6:  monthString = "June";          break;
                case 7:  monthString = "July";          break;
                case 8:  monthString = "August";        break;
                case 9:  monthString = "September";     break;
                case 10: monthString = "October";       break;
                case 11: monthString = "November";      break;
                case 12: monthString = "December";      break;
                default: monthString = "Invalid month"; break;
            }
            return monthString + " " + date[2] + "th, " + date[0] + " at " + split1[1];
        }
    }

    class DeleteUser extends AsyncTask{

        private String username;

        public DeleteUser(String username){
            this.username = username;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(User.this);
            pDialog.setMessage("Deleting User...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", this.username));
            Log.d("request!", "starting");
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(DELETE_USER_URL, "POST", params);
            return "Complete";
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Intent i = new Intent(User.this, ContactsActivity.class);
            startActivity(i);
            pDialog.dismiss();
        }
    }
}
