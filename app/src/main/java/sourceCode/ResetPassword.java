package sourceCode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.projects.fbgrecojr.vamoose.LoginActivity;
import com.projects.fbgrecojr.vamoose.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import security.PasswordHash;

/**
 * Created by fbgrecojr on 8/25/15.
 */
public class ResetPassword {

    private String _username;
    private Context _activity;
    AlertDialog.Builder builder;
    // Progress Dialog
    private ProgressDialog pDialog;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    private static final String CHANGE_PASS_URL = "http://www.frankgrecojr.com/webservice/changePassword.php";
    AlertDialog alertDialog;


    TextView error;
    EditText old, newPass, newConfirmPass;

    public void set_validateOldPass(boolean _validateOldPass) {
        this._validateOldPass = _validateOldPass;
    }

    public void set_username(String _username) {
        this._username = _username;
    }

    private boolean _validateOldPass;

    public ResetPassword(String username, Activity activity){
        this._username = username;
        this._activity = activity;
        this._validateOldPass = false;

        showDialog();
    }

    public void showDialog(){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(_activity);
        View promptsView = li.inflate(R.layout.reset_pass, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_activity);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        error = (TextView) promptsView.findViewById(R.id.error);
        old = (EditText) promptsView.findViewById(R.id.oldPass);
        newPass = (EditText) promptsView.findViewById(R.id.newPass);
        newConfirmPass = (EditText) promptsView.findViewById(R.id.confirmPass);
        error.setVisibility(View.INVISIBLE);
        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("ok change it", null);

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        if (old.getText().toString().equals("") || newPass.getText().toString().equals("") || newConfirmPass.getText().toString().equals("")) {
                            error.setText("populate all fields");
                            error.setVisibility(View.VISIBLE);
                        } else if (!newPass.getText().toString().equals(newConfirmPass.getText().toString())) {
                            error.setText("passwords don't match");
                            error.setVisibility(View.VISIBLE);
                        } else {
                            new Validate(Session.get_username(), old.getText().toString(), newPass.getText().toString()).execute();
                        }
                    }
                });
            }
        });

        alertDialog.show();
    }

    /*public boolean validateUser(String username, String password){
        return PasswordHash.validatePassword(password, json.getString("password")) ? true : false;
    }*/

    class Validate extends AsyncTask{

        private String _username;
        private String _oldPass;
        private String _newPass;
        private static final String LOGIN_URL = "http://www.frankgrecojr.com/webservice/getStoredPassword.php";

        public Validate(String username, String oldPass, String newPass){
            this._username = username;
            this._oldPass = oldPass;
            this._newPass = newPass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            builder = new AlertDialog.Builder(_activity);
            pDialog = new ProgressDialog(_activity);
            pDialog.setMessage("changing password...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Object[] args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", _username));
            Log.d("request!", "starting");
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params);
            boolean tryPass = false;
            try{
                tryPass = PasswordHash.validatePassword(_oldPass, json.getString("password")) ? true : false;
            }catch (JSONException | InvalidKeySpecException | NoSuchAlgorithmException e){System.err.println(e);}
            if(tryPass){
                //change password in db
                // Building Parameters
                params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", _username));
                try{
                    params.add(new BasicNameValuePair("password", PasswordHash.createHash(this._newPass)));
                }catch (InvalidKeySpecException | NoSuchAlgorithmException e){System.err.println(e);}
                Log.d("request!", "starting");
                // getting product details by making HTTP request
                json = jsonParser.makeHttpRequest(CHANGE_PASS_URL, "POST", params);
            }
            return tryPass ? "true" : "false";
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            String param = (String) o;
            if(param.equals("true")){
                //password correct and changed
                alertDialog.dismiss();
            }else{
                //old pass incorrect
                error.setText("password incorrect");
                error.setVisibility(View.VISIBLE);
            }
            pDialog.dismiss();
        }
    }

}
