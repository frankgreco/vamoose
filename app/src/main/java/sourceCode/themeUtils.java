package sourceCode;

import android.app.Activity;
import android.content.Intent;

import com.projects.fbgrecojr.vamoose.R;

/**
 * Created by fbgrecojr on 8/26/15.
 */
public class themeUtils {

    public static void setcTheme(int cTheme) {
        themeUtils.cTheme = cTheme;
    }

    private static int cTheme;

    public final static int EASTER= 0;
    public final static int SOFT = 1;
    public final static int GIRL = 2;
    public final static int SUMMER = 3;
    public final static int WARM = 4;

    public static void changeToTheme(Activity activity, int theme) {
        cTheme = theme;
        activity.finish();

        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        switch (cTheme) {
            default:
            case EASTER:
                activity.setTheme(R.style.easter_theme);
                break;
            case SOFT:
                activity.setTheme(R.style.soft_theme);
                break;
            case GIRL:
                activity.setTheme(R.style.girl_theme);
                break;
            case SUMMER:
                activity.setTheme(R.style.summer_theme);
                break;
            case WARM:
                activity.setTheme(R.style.warm_theme);
                break;

        }

    }
}
