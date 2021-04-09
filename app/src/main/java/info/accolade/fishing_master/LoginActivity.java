package info.accolade.fishing_master;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import info.accolade.fishing_master.modal.DefaultResponceModal;
import info.accolade.fishing_master.modal.LoginResponse;
import info.accolade.fishing_master.utils.ApiClient;
import info.accolade.fishing_master.utils.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText ed_email, ed_password;
    String email, password;
    ProgressDialog dialog;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ed_email = findViewById(R.id.log_email);
        ed_password = findViewById(R.id.log_password);
        layout = findViewById(R.id.loginLayout);

        dialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        dialog.setCancelable(false);
        dialog.setTitle(R.string.waite);
        dialog.setIcon(R.drawable.boat);
        dialog.setMessage(getString(R.string.message));
    }

    public void red_forgot(View view) {
        startActivity(new Intent(LoginActivity.this, ForgotActivity.class));
    }

    public void red_register(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    public void btnLogin(View view) {
        email = ed_email.getText().toString().trim();
        password = ed_password.getText().toString().trim();

        checkValidation();
    }
    private void checkValidation() {
        dialog.show();
        sendData();
    }
    private void sendData() {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<LoginResponse> defaultResponceModalCall = apiInterface.getLoginResponce(email, password);
        defaultResponceModalCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                dialog.dismiss();
                try {
                    if(response.isSuccessful())
                    {
                        if(response.body()!=null)
                        {
                            if(response.body().getSuccess())
                            {
                                Log.e("Login success", "");
                                Toast.makeText(LoginActivity.this, "User Logged in successfully..", Toast.LENGTH_SHORT).show();
                                saveLoginData(response.body().getUserId(), response.body().getUserName(), response.body().getUserType(), response.body().getBoatName(), response.body().getBoatNumber(), response.body().getUserAddress());
                            }
                            else
                            {
                                Log.e("Invalid Credentials", "");
                                Snackbar.make(layout, "Invalid Credentials..", Snackbar.LENGTH_LONG)
                                        .setActionTextColor(Color.RED)
                                        .show();
                            }
                        }
                        else
                        {
                            Log.e("Empty body responce", "");
                            Snackbar.make(layout, "Unable to process your request..", Snackbar.LENGTH_LONG)
                                    .setAction("Retry", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.show();
                                            sendData();
                                        }
                                    })
                                    .setActionTextColor(Color.RED)
                                    .show();
                        }
                    }
                    else
                    {
                        Log.e("Unsuccess responce", "");
                        Snackbar.make(layout, "Unable to process your request..", Snackbar.LENGTH_LONG)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.show();
                                        sendData();
                                    }
                                })
                                .setActionTextColor(Color.RED)
                                .show();
                    }
                }
                catch (Exception e)
                {
                    Log.e("Exception", e.getMessage());
                    Snackbar.make(layout, "Unable to process your request..", Snackbar.LENGTH_LONG)
                            .setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.show();
                                    sendData();
                                }
                            })
                            .setActionTextColor(Color.RED)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                dialog.dismiss();
                Log.e("Throwable", "" + t);
                if (t instanceof SocketTimeoutException) {
                    Snackbar.make(layout, "Unable to connect server.", Snackbar.LENGTH_LONG)
                            .setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.show();
                                    sendData();
                                }
                            })
                            .setActionTextColor(Color.RED)
                            .show();
                } else if (t instanceof ConnectException) {
                    Snackbar.make(layout, "Unable to connect server, make sure that Wi-Fi or mobile data is turned on.", Snackbar.LENGTH_LONG)
                            .setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.show();
                                    sendData();
                                }
                            })
                            .setActionTextColor(Color.RED)
                            .show();
                } else {
                    Snackbar.make(layout, "Unable to process your request.", Snackbar.LENGTH_LONG)
                            .setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.show();
                                    sendData();
                                }
                            })
                            .setActionTextColor(Color.RED)
                            .show();
                }

            }
        });
    }

    private void saveLoginData(String userId, String userName, String userType, String boatname, String boatno, String useraddress) {
        Log.e("type",userType);
        if(userType.equals("User"))
        {
            SharedPreferences sharedPreferences = getSharedPreferences("loginDetails", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("UserId", userId);
            editor.putString("UserName", userName);
            editor.putString("UserType", userType);
            editor.putBoolean("IsLogin", true);
            editor.apply();

            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        else
        {
            SharedPreferences sharedPreferences = getSharedPreferences("loginDetails", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("UserId", userId);
            editor.putString("UserName", userName);
            editor.putString("UserType", userType);
            editor.putString("BoatName", boatname);
            editor.putString("BoatNumber", boatno);
            editor.putString("UserAddress", useraddress);
            editor.putBoolean("IsLogin", true);
            editor.apply();

            startActivity(new Intent(LoginActivity.this, FishersHomeActivity.class));
            finish();
        }
    }
}