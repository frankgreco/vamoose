package sourceCode;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.projects.fbgrecojr.vamoose.LoginActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fbgrecojr on 8/23/15.
 */
public class LogOff extends AsyncTask<String, String, String> {

    private String userName;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private Context _from;
    private static final String LOG_OFF_URL = "http://www.frankgrecojr.com/webservice/logoff.php";


    public LogOff(String userName, Context from){
        this.userName = userName;
        this._from = from;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Intent i = new Intent(_from, LoginActivity.class);
        _from.startActivity(i);
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
        pDialog = new ProgressDialog(_from);
        pDialog.setMessage("Logging Off...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }
}
