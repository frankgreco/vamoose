package test;

import android.util.Log;

import sourceCode.GMailSender;

/**
 * Created by fbgrecojr on 8/8/15.
 */
public class TestEmail {


    public static void main(String[] args){
        try {
            GMailSender sender = new GMailSender("grecoruppinnovations@gmail.com", "vamoose123");
            sender.sendMail("Vamoose!!",
                    "test",
                    "fbgrecojr@gmail.com",
                    "mirmat13@gmail.com");
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }
}
