package info.accolade.fishing_master;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.BarringInfo;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import info.accolade.fishing_master.modal.DefaultResponceModal;
import info.accolade.fishing_master.modal.LoginResponse;
import info.accolade.fishing_master.utils.ApiClient;
import info.accolade.fishing_master.utils.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {
    ImageView fimage;
    TextView desc, bname, bno, fname, aardate, postdate;
    ProgressDialog dialog;
    LinearLayout layout;
    AppCompatButton btnRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        fimage = findViewById(R.id.imagefish);
        desc = findViewById(R.id.bdesc);
        bname = findViewById(R.id.bname);
        bno = findViewById(R.id.bno);
        fname = findViewById(R.id.fname);
        aardate = findViewById(R.id.ardate);
        postdate = findViewById(R.id.pdate);
        layout = findViewById(R.id.detaillayout);
        btnRequest = findViewById(R.id.request);

        desc.setText(getIntent().getStringExtra("desc"));
        bname.setText("Boat Name : "+getIntent().getStringExtra("bname"));
        bno.setText("Boat Number : "+getIntent().getStringExtra("bno"));
        fname.setText("Fish Name : "+getIntent().getStringExtra("fish"));
        aardate.setText("Arriving on"+getIntent().getStringExtra("arrdate"));
        postdate.setText("Posted on : "+getIntent().getStringExtra("posted"));

        Picasso.get().load("http://192.168.43.124/fishing-master-php/img/"+getIntent().getStringExtra("image")).into(fimage);

        dialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        dialog.setCancelable(false);
        dialog.setTitle(R.string.waite);
        dialog.setIcon(R.drawable.boat);
        dialog.setMessage(getString(R.string.message));

    }

    public void reserve(View view) {
        sendData();
    }

    private void sendData() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("UserId","");

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<DefaultResponceModal> defaultResponceModalCall = apiInterface.getRequestResponce(userid, getIntent().getStringExtra("bno"), getIntent().getStringExtra("fish"));
        defaultResponceModalCall.enqueue(new Callback<DefaultResponceModal>() {
            @Override
            public void onResponse(Call<DefaultResponceModal> call, Response<DefaultResponceModal> response) {
                dialog.dismiss();
                try {
                    if(response.isSuccessful())
                    {
                        if(response.body()!=null)
                        {
                            if(response.body().getSuccess()) {
                                Toast.makeText(DetailsActivity.this, "Request sent successfully..", Toast.LENGTH_SHORT).show();
                                btnRequest.setEnabled(false);
                            }
                            else
                            {
                                Snackbar.make(layout, "Unable to send request..", Snackbar.LENGTH_LONG)
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