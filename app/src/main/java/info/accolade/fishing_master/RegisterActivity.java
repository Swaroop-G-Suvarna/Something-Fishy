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
import android.widget.ScrollView;
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

public class RegisterActivity extends AppCompatActivity {
    EditText ed_name, ed_number, ed_email, ed_password, ed_cpassword;
    String name, number, email, password, cpassword;
    ProgressDialog dialog;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ed_name = findViewById(R.id.reg_name);
        ed_number = findViewById(R.id.reg_contact);
        ed_email = findViewById(R.id.reg_email);
        ed_password = findViewById(R.id.reg_password);
        ed_cpassword = findViewById(R.id.reg_cpassword);
        layout = findViewById(R.id.regLayout);

        dialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        dialog.setCancelable(false);
        dialog.setTitle(R.string.waite);
        dialog.setIcon(R.drawable.boat);
        dialog.setMessage(getString(R.string.message));
    }

    public void red_Login(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

    public void btnRegister(View view) {
        name = ed_name.getText().toString().trim();
        number = ed_number.getText().toString().trim();
        email = ed_email.getText().toString().trim();
        password = ed_password.getText().toString().trim();
        cpassword = ed_cpassword.getText().toString().trim();

        checkValidation();
    }

    private void checkValidation() {
        if(name.length()<3)
        {
            ed_name.setError("Name must be three or more character long..");
        }
        else if(number.length()!=10)
        {
            ed_number.setError("Kindly enter 10 digit number..");
        }
        else if(!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")||email.isEmpty())
        {
            ed_email.setError("Kindly enter valid email..");
        }
        else if(password.length()<5)
        {
            ed_password.setError("Password must be five or more character long..");
        }
        else if(!cpassword.matches(password))
        {
            ed_cpassword.setError("Password mismatch..");
        }
        else
        {
            dialog.show();
            sendData();
        }
    }

    private void sendData() {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<DefaultResponceModal> defaultResponceModalCall = apiInterface.getResponce(name, number, email, password);
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
                                Log.e("Insertion success", "");
                                Toast.makeText(RegisterActivity.this, "User Registered successfully..", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            }
                            else
                            {
                                Log.e("Insertion error", "unable to insert on database");
                                Snackbar.make(layout, "Unable to insert data..", Snackbar.LENGTH_LONG)
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
}