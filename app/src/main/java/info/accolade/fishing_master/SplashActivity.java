package info.accolade.fishing_master;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences sharedPreferences = getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
        boolean isLogin = sharedPreferences.getBoolean("IsLogin",false);
        String usertype = sharedPreferences.getString("UserType","");
        Log.e("isLogin",""+isLogin);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i;
                if(isLogin)
                {
                    if(usertype.equals("User"))
                    {
                        i = new Intent(SplashActivity.this, MainActivity.class);
                    }
                    else
                    {
                        i = new Intent(SplashActivity.this, FishersHomeActivity.class);
                    }
                }
                else {
                    i = new Intent(SplashActivity.this, WelcomeActivity.class);
                }
                startActivity(i);
                finish();
            }
        }, 3000);
    }
}