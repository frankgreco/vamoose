package sourceCode;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import security.PasswordHash;

/**
 *
 * @author fbgrecojr
 *
 */
public class Query {

    /**
     * The following fields holds the names of the tables that are stored
     * one the server.  They represent the tables that will be used in
     * the methods in this class.
     */
    private static final String USERACCOUNTSTABLE = "user_accounts";
    private static final String ADMINSETTINGSTABLE = "admin_settings";
    private static final String SHIFTDATATABLE = "shift_data";

    /**
     * Takes the userName and password entered in by a user and validates it against a database
     * If the login attempt is successful, the 'lastLogin' column is updated
     * @param conn this is the connection to the database
     * @param userName the possible userName
     * @param password the possible password
     * @pre the connection is valid
     * @post either the information is validated or not
     * @return true if what was entered validates against the database
     */
    public static boolean loginAttempt(Connection conn, String userName, String password) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException{
        boolean toReturn = false;
        Statement s = conn.createStatement();
        String query = "SELECT password FROM " + USERACCOUNTSTABLE + " WHERE userName = '" + userName + "'";
        ResultSet rs = s.executeQuery(query);
        //rs will yield only one result because userName is a primary key
        rs.next();
        String correctHash = rs.getString("password");
        //'possible' contains the salted password
        toReturn = PasswordHash.validatePassword(password, correctHash) ? true : false;
        if(toReturn){
            query = "UPDATE " + USERACCOUNTSTABLE + " SET lastLogin = CURRENT_TIMESTAMP, isLoggedIn = 1 WHERE userName = '" + userName + "'";
            s.executeUpdate(query);
        }
        s.close();
        rs.close();
        return toReturn;
    }

    /**
     * Returns the last time a specific user has logged in.
     * NOTE: can be null if user has never logged in (occurs upon first login after user creation)
     * @param conn this is the connection to the database
     * @param userName
     * @pre the connection is valid
     * @post non - getter method
     * @return last login or null if no last login
     */
    public static String getLastLogin(Connection conn, String userName) throws SQLException{
        String toReturn = null;
        Statement s = conn.createStatement();
        String query = "SELECT lastLogin FROM " + USERACCOUNTSTABLE + " WHERE userName = '" + userName + "'";
        ResultSet rs = s.executeQuery(query);
        //rs will yield only one result because userName is a primary key
        rs.next();
        toReturn = rs.getTimestamp("lastLogin") == null ? userName + " has never logged in" : rs.getTimestamp("lastLogin").toString();
        return toReturn;
    }

    /**
     * Creates a random secure token (6 characters long containing AZaz09) and stores the salted version of that in the database
     * @param conn this is the connection to the database
     * @param userName
     * @pre the connection is valid
     * @post a salted version of the secure token has been stored in the database
     * @return the unsalted secure token
     * @throws SQLException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public static String createSecureToken(Connection conn, String userName) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException{
        String toReturn = getRandomString();
        Statement s = conn.createStatement();
        String query = "UPDATE " + USERACCOUNTSTABLE + " SET password = '" + PasswordHash.createHash(toReturn) + "' " + "WHERE userName = '" + userName + "'";
        s.executeUpdate(query);
        return toReturn;
    }

    /**
     * Helper method used by Query.createSecureToken(...)
     * @return a random String of 6 characters
     */
    public static String getRandomString(){
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        char[] salt = new char[6];
        Random rnd = new Random();
        for(int i = 0; i<salt.length; ++i){
            int index = (rnd.nextInt(SALTCHARS.length()));
            salt[i] = SALTCHARS.charAt(index);
        }
        String saltStr = new String(salt);
        return saltStr;
    }

    /**
     * Changes the password
     * @param conn this is the connection to the database
     * @param userName
     * @param password
     * @throws SQLException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @pre the connection is valid
     * @post the new, salted password has been updated in the database
     */
    public static void changePassword(Connection conn, String userName, String password) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException{
        Statement s = conn.createStatement();
        String query = "UPDATE " + USERACCOUNTSTABLE + " SET password = '" + PasswordHash.createHash(password) + "' " + "WHERE userName = '" + userName + "'";
        s.executeUpdate(query);
    }

    /**
     * Retrieves the userName from the database if it exists
     * @param conn this is the connection to the database
     * @param email what is used to validate the existence of a userName
     * @pre the connection is valid
     * @post none - getter method
     * @return the user name of associated with the email OR null if user doesn't exist
     * @throws SQLException
     */
    public static String getUserName(Connection conn, String email) throws SQLException{
        Statement s = conn.createStatement();
        String query = "SELECT userName FROM " + USERACCOUNTSTABLE + " WHERE email = '" + email + "'";
        ResultSet rs = s.executeQuery(query);
        if(rs.next()) return rs.getString("userName");
        else return null;

    }

    /**
     * If userName exists, update information with new info; else, create a new user with the information
     * @param conn this is the connection to the database
     * @param firstName
     * @param lastName
     * @param userName
     * @param mobile
     * @param email
     * @param isAdmin
     * @throws SQLException
     * @pre the connection is valid
     * @post modifies user if exists; else, creates new user
     */
    public static void modifyUser(Connection conn, String firstName, String lastName, String userName, String mobile, String email, short isAdmin) throws SQLException{
        //does userName exist?
        Statement s = conn.createStatement();
        String query = "SELECT userName FROM " + USERACCOUNTSTABLE + " WHERE userName = '" + userName + "'";
        ResultSet rs = s.executeQuery(query);
        if(rs.next()){
            //user exists
            query = "UPDATE " + USERACCOUNTSTABLE + " SET first_name = '" + firstName + "', last_name = '" + lastName + "', phone = '" + mobile + "', email = '" + email + "', isAdmin = '" + isAdmin + "' WHERE userName = '" + userName + "'";
            s.executeUpdate(query);
        }else{
            //user does not exists. Create new user
            query = "INSERT INTO " + USERACCOUNTSTABLE + " (userName, password, email, first_name, last_name, phone, isAdmin) "
                    + "VALUES ('" + userName + "', '" + getRandomString() + "', '" + email + "', '" + firstName + "', '" + lastName + "', '" + mobile + "', '" + isAdmin + "')";
            s.executeUpdate(query);
        }
    }

    /**
     * Tell the database that the user is no long logged on
     * @param conn the connection to the database
     * @param userName
     * @pre the connection is valid
     * @post the database as been updated reflecting that the user has been logged off
     * @throws SQLException
     */
    public static void logOff(Connection conn, String userName) throws SQLException{
        Statement s = conn.createStatement();
        String query = "UPDATE " + USERACCOUNTSTABLE + " SET isLoggedIn = '0' WHERE userName = '" + userName + "'";
        s.executeUpdate(query);
    }

    /**
     * Change the zone in the database to one of three options: GREEN, YELLOW, RED
     * @param conn the connection to the database
     * @param zone the name of the zone to be set to
     * @throws SQLException
     * @pre the connection is valid
     * @post the zone has been changed in the database
     */
    public static void setZone(Connection conn, String zone) throws SQLException{
        //param check
        zone = zone.toUpperCase();
        if(!(zone.equals("GREEN") || zone.equals("YELLOW") || zone.equals("RED"))) throw new IllegalArgumentException("Value for \"zone\" is incorrect");
        Statement s = conn.createStatement();
        String query = "UPDATE " + ADMINSETTINGSTABLE + " SET zone = '" + zone + "' WHERE id = '1'";
        s.executeUpdate(query);
    }

    /**
     * Sets the value for a particular zone
     * @param conn the connection to the database
     * @param zone the zone to be changed
     * @param value the value to associate with the zone
     * @pre the connection is valid
     * @post the zone has been updated in the database with the passed in value
     * @throws SQLException
     */
    public static void changeZone(Connection conn, String zone, int value) throws SQLException{
        //param check
        zone = zone.toUpperCase();
        if(!(zone.equals("GREEN") || zone.equals("YELLOW") || zone.equals("RED")) || (value < 0) || value > 100) throw new IllegalArgumentException("Value for \"zone\" is incorrect");
        String query = "";
        if(zone.equals("GREEN")) query = "UPDATE " + ADMINSETTINGSTABLE + " SET grn_zone = '" + value + "' WHERE id = '1'";
        else if(zone.equals("YELLOW")) query = "UPDATE " + ADMINSETTINGSTABLE + " SET ylw_zone = '" + value + "' WHERE id = '1'";
        else query = "UPDATE " + ADMINSETTINGSTABLE + " SET red_zone = '" + value + "' WHERE id = '1'";
        Statement s = conn.createStatement();
        s.executeUpdate(query);
    }

    /**
     *
     * @param conn
     * @param value
     * @throws SQLException
     * @pre
     * @post
     */
    public static void setBreakLength(Connection conn, int value) throws SQLException{
        if(value < 0) throw new IllegalArgumentException("break length cannot be a negative number");
        String query = "UPDATE " + ADMINSETTINGSTABLE + " SET brk_length = '" + value + "' WHERE id = '1'";
        Statement s = conn.createStatement();
        s.executeUpdate(query);
    }

    /**
     *
     * @param conn
     * @param value
     * @pre
     * @post
     * @return
     * @throws SQLException
     */
    public static void setBreakBuffer(Connection conn, int value) throws SQLException{
        if(value < 0) throw new IllegalArgumentException("break buffer cannot be a negative number");
        String query = "UPDATE " + ADMINSETTINGSTABLE + " SET buffer = '" + value + "' WHERE id = '1'";
        Statement s = conn.createStatement();
        s.executeUpdate(query);
    }

    /**
     * Takes a shift data table which has already been placed in a 2D array (using a .csv file) following a specific format and inserts it
     * into the shift_data table.
     * @param conn the connection to the database
     * @param shiftTable the 2D array containing the metadata.
     * @throws SQLException
     * @pre the connection is valid
     * @pre the metadata in the 2D array is in the correct format
     * @post the metadata has been correctly added into the database.
     */
    public static void uploadShift(Connection conn, String[][] shiftTable) throws SQLException{
        String date = ""; String userName = ""; String shiftStart = ""; String shiftEnd = "";
        for(int i = 0; i < shiftTable.length; i++){
            for(int j = 0; j < shiftTable[i].length; j++){
                switch(j){
                    case 0:date = shiftTable[i][j];break;
                    case 1:userName = shiftTable[i][j];break;
                    case 2:shiftStart = shiftTable[i][j];break;
                    case 3:shiftEnd = shiftTable[i][j];break;
                }
            }
            doUploadShift(conn, date, userName, shiftStart, shiftEnd);
        }
    }

    /**
     * Assists Query.uploadSift(...) in adding the metadata to the database.
     * @param date must be in MySQL DATE format (e.g. YYYY-MM-DD HH:MM:SS).
     * @param userName
     * @param shiftStart must be in MySQL DATETIME format (e.g. YYYY-MM-DD HH:MM:SS).
     * @param shiftEnd must be in MySQL DATETIME format (e.g. YYYY-MM-DD HH:MM:SS).
     * @throws SQLException
     */
    private static void doUploadShift(Connection conn, String date, String userName, String shiftStart, String shiftEnd) throws SQLException{
        Statement s = conn.createStatement();
        String query = "INSERT INTO " + SHIFTDATATABLE + " ('date', 'user_name', 'shift_start', 'shift_end') "
                + "VALUES ('" + date + "', '" + userName + "', '" + shiftStart + "', '" + shiftEnd + "')";
        s.executeUpdate(query);
    }

    /**
     *
     * @param conn1
     * @param conn2
     * @param userName
     * @pre
     * @post
     * @return
     */
    public static boolean requestBreak(Connection conn1, Connection conn2, String userName){return true;}
}
