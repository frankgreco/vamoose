package test;

import sourceCode.Query;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestQuery {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://greco-rupp-innovations.cb7gmuw49vhs.us-east-1.rds.amazonaws.com:3306/vamoose",
                    "fbgrecojr", "C3$nzt48");
            System.out.println("Connection Successufull");

            Query.modifyUser(conn, "Frank", "Greco Jr", "fbgrecojr", "4144779013", "fbgrecojr@me.com", (short) 1);
            Query.modifyUser(conn, "Miranda", "Tarlton", "mirmat13", "7158899258", "mirmat13@gmail.com", (short) 0);

            System.out.println("Query Successfull");

            Query.changePassword(conn, "fbgrecojr", "C3$nzt48");

            System.out.println("password changed");

            boolean loggedIn = Query.loginAttempt(conn, "fbgrecojr", "C3$nzt48");

            System.out.println(loggedIn);

            System.out.println(Query.getLastLogin(conn, "mirmat13").toString());

            System.out.println(Query.createSecureToken(conn, "fbgrecojr"));

            loggedIn = Query.loginAttempt(conn, "fbgrecojr", "C3$nzt48");

            System.out.println(loggedIn);

            System.out.println(Query.createSecureToken(conn, "mirmat13"));

            Query.changePassword(conn, "fbgrecojr", "Lincecum55");

            System.out.println(Query.getUserName(conn, "fbgrecojr@me.com"));

            Query.modifyUser(conn, "Francis", "Basil", "fbgrecojr", "911", "fbgreco@uwm.edu", (short) 0);

            Query.logOff(conn, "fbgrecojr");

            Query.setZone(conn, "red");

            System.out.println("zone is set");

            Query.changeZone(conn, "red", 10);

            System.out.println("zone is changed");

            Query.setBreakLength(conn, 15);

            Query.setBreakBuffer(conn, 5);

            Query.changePassword(conn, "fbgrecojr", "C3$nzt48");
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            System.err.println(e);
        }
    }

}
