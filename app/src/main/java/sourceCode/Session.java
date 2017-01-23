package sourceCode;

/**
 * Created by fbgrecojr on 8/30/15.
 */
public class Session {

    private static String _username;
    private static String _theme;
    private static boolean _notification;
    private static String _brkLength;
    private static String _brkBuffer;
    private static String _grnZone;
    private static String _ylwZone;
    private static String _redZone;

    public Session(String username, String theme, boolean notification, String brkLength, String brkBuffer, String grnZone, String ylwZone, String redZone){
        this._username = username;
        this._theme = theme;
        this._notification = notification;
        this._brkLength = brkLength;
        this._brkBuffer = brkBuffer;
        this._grnZone = grnZone;
        this._ylwZone = ylwZone;
        this._redZone = redZone;
    }

    public static String get_theme() {
        return _theme;
    }

    public static void set_theme(String _theme) {
        Session._theme = _theme;
    }

    public static String get_username() {
        return _username;
    }

    public static void set_username(String _username) {
        Session._username = _username;
    }

    public static boolean is_notification() {
        return _notification;
    }

    public static void set_notification(boolean _notification) {Session._notification = _notification;}

    public static String get_brkLength() {return _brkLength;}

    public static void set_brkLength(String _brkLength) {Session._brkLength = _brkLength;}

    public static int getAppThemeInt() {
        if(Session._theme.equals("easter")) return themeUtils.EASTER;
        else if(Session._theme.equals("soft")) return themeUtils.SOFT;
        else if(Session._theme.equals("girl")) return themeUtils.GIRL;
        else if(Session._theme.equals("summer")) return themeUtils.SUMMER;
        else return themeUtils.WARM;
    }

    public static String get_brkBuffer() {return _brkBuffer;}

    public static void set_brkBuffer(String _brkBuffer) {Session._brkBuffer = _brkBuffer;}

    public static String get_grnZone() {
        return _grnZone;
    }

    public static void set_grnZone(String _grnZone) {
        Session._grnZone = _grnZone;
    }

    public static String get_ylwZone() {
        return _ylwZone;
    }

    public static void set_ylwZone(String _ylwZone) {
        Session._ylwZone = _ylwZone;
    }

    public static String get_redZone() {
        return _redZone;
    }

    public static void set_redZone(String _redZone) {
        Session._redZone = _redZone;
    }
}
