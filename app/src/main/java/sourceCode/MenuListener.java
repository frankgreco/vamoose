package sourceCode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.projects.fbgrecojr.vamoose.AdminActivity;
import com.projects.fbgrecojr.vamoose.AdminSettings;
import com.projects.fbgrecojr.vamoose.CalendarActivity;
import com.projects.fbgrecojr.vamoose.ContactsActivity;
import com.projects.fbgrecojr.vamoose.HistoryActivity;
import com.projects.fbgrecojr.vamoose.LoginActivity;

/**
 * Created by fbgrecojr on 8/23/15.
 */
public class MenuListener implements View.OnClickListener {

    private int _position;
    private Context _from;
    private Class _fromClass;

    public MenuListener(int position, Context from, Class fromClass){
        this._position = position;
        this._from = from;
        this._fromClass = fromClass;
    }

    @Override
    public void onClick(View v) {
        Class _to = null;
        switch (_position){
            case 0:
                _to = AdminActivity.class;
                break;
            case 1:
                _to = ContactsActivity.class;
                break;
            case 2:
                _to = AdminSettings.class;
                break;
            case 3:
                _to = CalendarActivity.class;
                break;
            case 4:
                _to = HistoryActivity.class;
                break;
            case 5:
                new LogOff(Session.get_username(), _from).execute();
                break;
            default:
                _to = _fromClass;
                break;
        }
        if(_position != 5)_from.startActivity(new Intent(_from, _to));
    }
}
