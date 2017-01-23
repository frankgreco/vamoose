package com.projects.fbgrecojr.vamoose;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;

import sourceCode.JSONParser;
import sourceCode.Query;

public class AddUser extends Activity {

    private EditText _first, _last, _phone, _email, _userName;
    private Button _submit;
    private RadioGroup _isAdmin;
    private RadioButton _yes, _no;
    // Progress Dialog
    private ProgressDialog pDialog;
    // JSON parser class
    private JSONParser jsonParser = new JSONParser();
    private String _oldUser;
    private ImageView iv;
    private String base64Image;

    private boolean adminChoise;

    private static final String ADD_USER_URL = "http://www.frankgrecojr.com/webservice/addUser.php";
    private static final String MODIFY_USER_URL = "http://www.frankgrecojr.com/webservice/modifyUser.php";


    public AddUser(){
        this.adminChoise = false;
        this._oldUser = "";
        this.base64Image = "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        _first = (EditText) findViewById(R.id.first);
        _last = (EditText) findViewById(R.id.last);
        _phone = (EditText) findViewById(R.id.number);
        _phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        _email = (EditText) findViewById(R.id.email);
        _userName = (EditText) findViewById(R.id.userName);
        _submit = (Button) findViewById(R.id.button);
        _isAdmin = (RadioGroup) findViewById(R.id.group);
        iv = (ImageView) findViewById(R.id.imageView1);

        _yes = (RadioButton) findViewById(R.id.yes);
        _no = (RadioButton) findViewById(R.id.no);

        //Bitmap bm = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.user);
        //this.base64Image = prepPictureForDb(bm);

        if(getIntent().getStringExtra("option").equals("edit")){
            _first.setText(getIntent().getStringExtra("first"));
            _last.setText(getIntent().getStringExtra("last"));
            _phone.setText(getIntent().getStringExtra("phone"));
            _email.setText(getIntent().getStringExtra("email"));
            _userName.setText(getIntent().getStringExtra("username"));
            base64Image = getIntent().getStringExtra("image");
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            iv.setImageBitmap(User.squareToCircle(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length)));
            this._oldUser = getIntent().getStringExtra("username");
            if(getIntent().getStringExtra("admin").equals("true")) {
                _yes.setChecked(true);
                _no.setChecked(false);
            }else{
                _yes.setChecked(false);
                _no.setChecked(true);
            }
            _submit.setText("Save");
        }

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                selectImage();
            }
        });

        _submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()){
                    new Add(_first.getText().toString().substring(0,1).toUpperCase() + _first.getText().toString().substring(1).toLowerCase(), _last.getText().toString().substring(0,1).toUpperCase() + _last.getText().toString().substring(1).toLowerCase(), _userName.getText().toString(), _email.getText().toString(), _phone.getText().toString(),
                            Query.getRandomString(), adminChoise == true ? "1" : "0", getIntent().getStringExtra("option").toString(), getIntent().getStringExtra("option").equals("edit") ? _oldUser : "", base64Image.equals("") ? getDefaultImage() : base64Image).execute();
                }else{
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddUser.this);
                    alertDialogBuilder.setMessage("One or more of the fields is incorrect!");
                    alertDialogBuilder.setPositiveButton("Got it", null);
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.create().show();
                }
            }
        });
    }

    private String getDefaultImage(){
        BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        return prepPictureForDb(bitmap);
    }

    public boolean validateInput(){
        String f = _first.getText().toString();
        String l = _last.getText().toString();
        String u = _userName.getText().toString();
        String e = _email.getText().toString();
        String p = _phone.getText().toString();
        return doValidate(new String[]{f,l,u,e,p});
    }

    private void selectImage(){
        final CharSequence[] options = {"Take Photo", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddUser.this);
        builder.setTitle("Select photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    iv.setImageBitmap(User.squareToCircle(bitmap));
                    this.base64Image = prepPictureForDb(bitmap);


                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                //Log.w("path of image from gallery......******************.........", picturePath+"");
                iv.setImageBitmap(thumbnail);
                this.base64Image = prepPictureForDb(User.squareToCircle(thumbnail));
            }
        }
    }

    private String prepPictureForDb(Bitmap bm){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private boolean doValidate(String[] x){
        boolean toReturn = true;
        for(int i = 0; i < x.length; ++i){
            if(x[i].equals("") || x[i].equals(" ")){
                toReturn = false;
                break;
            }
        }
        if(!(_yes.isChecked() || _no.isChecked())) toReturn = false;
        return toReturn;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.yes:
                if (checked)
                    this.adminChoise = true;
                    break;
            case R.id.no:
                if (checked)
                    this.adminChoise = false;
                    break;
        }
    }

    class Add extends AsyncTask{

        private String _first;
        private String _last;
        private String _username;
        private String _email;
        private String _phone;
        private String _password;
        private String _isAdmin;
        private String which;
        private String _oldUsername;
        private String _customPic;

        public Add(String first, String last, String username, String email, String phone, String password, String isAdmin, String which, String oldUsername, String customPic){
            this._first = first;
            this._last = last;
            this._username = username;
            this._email = email;
            this._phone = phone;
            this._password = password;
            this._isAdmin = isAdmin;
            this.which = which;
            this._oldUsername = oldUsername;
            this._customPic = customPic;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Intent i = new Intent(AddUser.this, ContactsActivity.class);
            startActivity(i);
            pDialog.dismiss();
        }

        @Override
        protected Object doInBackground(Object[] args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("first", this._first));
            params.add(new BasicNameValuePair("last", this._last));
            params.add(new BasicNameValuePair("username", this._username));
            params.add(new BasicNameValuePair("email", this._email));
            if(this.which.equals("new"))params.add(new BasicNameValuePair("password", this._password));
            params.add(new BasicNameValuePair("phone", this._phone));
            params.add(new BasicNameValuePair("isAdmin", this._isAdmin));
            if(this.which.equals("edit"))params.add(new BasicNameValuePair("oldUser", this._oldUsername));
            params.add(new BasicNameValuePair("image", this._customPic));
            Log.d("request!", "starting");
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(this.which.equals("edit") ? MODIFY_USER_URL : ADD_USER_URL, "POST", params);
            return "Complete";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddUser.this);
            pDialog.setMessage(this.which.equals("edit") ? "Saving info..." : "Adding User...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
    }

}
