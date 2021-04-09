package info.accolade.fishing_master;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import info.accolade.fishing_master.utils.ApiClient;
import info.accolade.fishing_master.utils.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotActivity extends AppCompatActivity {
    EditText ed_email;
    String email;
    ProgressDialog dialog;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        ed_email = findViewById(R.id.forgot_email);
        layout = findViewById(R.id.forgotlayout);

        dialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        dialog.setCancelable(false);
        dialog.setTitle(R.string.waite);
        dialog.setIcon(R.drawable.boat);
        dialog.setMessage(getString(R.string.message));
    }

    public void btnForgot(View view) {
        dialog.show();
        email = ed_email.getText().toString().trim();

        checkValidation();
    }
    private void checkValidation() {
        sendData();
    }
    private void sendData() {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<DefaultResponceModal> defaultResponceModalCall = apiInterface.getforgotResponce(email);
        defaultResponceModalCall.enqueue(new Callback<DefaultResponceModal>() {
            @Override
            public void onResponse(Call<DefaultResponceModal> call, Response<DefaultResponceModal> response) {
                dialog.dismiss();
                try {
                    if(response.isSuccessful())
                    {
                        if(response.body()!=null)
                        {
                            if(response.body().getSuccess())
                            {
                                Log.e("successfully pswd sent", "");
                                Toast.makeText(ForgotActivity.this, "Password sent successfully to registered email..", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotActivity.this, LoginActivity.class));
                            }
                            else
                            {
                                Log.e("unable to send mail", "");
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
            public void onFailure(Call<DefaultResponceModal> call, Throwable t) {
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

    public void red_Login2(View view) {
        startActivity(new Intent(ForgotActivity.this, LoginActivity.class));
    }
}