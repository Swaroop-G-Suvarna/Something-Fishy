package info.accolade.fishing_master;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import info.accolade.fishing_master.adapter.SearchFishAdapter;
import info.accolade.fishing_master.modal.SearchModal;
import info.accolade.fishing_master.utils.ApiClient;
import info.accolade.fishing_master.utils.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FishSearchActivity extends AppCompatActivity {
    ApiInterface apiIterface;
    ProgressDialog progressDialog;

    ArrayList<SearchModal> modals = new ArrayList<>();
    private SearchFishAdapter adapter;
    RecyclerView recycle;
    SearchFishAdapter.RecyclerViewClickListener listener;
    LinearLayout layout;
    TextInputEditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_search);

        editText = findViewById(R.id.etfishSearch);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        recycle = findViewById(R.id.fishRecycle);
        layout = findViewById(R.id.fishlayoyt);
        recycle.setLayoutManager(new LinearLayoutManager(this));

        //progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        loadData();
    }
    private void loadData() {
        apiIterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<SearchModal>> searchModalCall = apiIterface.getSearchResponse();
        searchModalCall.enqueue(new Callback<List<SearchModal>>() {
            @Override
            public void onResponse(Call<List<SearchModal>> call, Response<List<SearchModal>> response) {
                progressDialog.dismiss();
                try {
                    List<SearchModal> postResponses = response.body();
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            modals = new ArrayList<>(response.body());

                            listener = new SearchFishAdapter.RecyclerViewClickListener() {
                                @Override
                                public void onClick(View v, int position) {
                                    Intent i = new Intent(FishSearchActivity.this, DetailsActivity.class);
                                    i.putExtra("bname", modals.get(position).getBoatname());
                                    i.putExtra("bno", modals.get(position).getBoatno());
                                    i.putExtra("posted", modals.get(position).getCreateddate());
                                    i.putExtra("arrdate", modals.get(position).getArrivingdate());
                                    i.putExtra("fish", modals.get(position).getFishname());
                                    i.putExtra("image", modals.get(position).getImage());
                                    i.putExtra("desc", modals.get(position).getDescription());
                                    startActivity(i);
                                }
                            };
                            adapter = new SearchFishAdapter(getApplicationContext(), modals, listener);
                            recycle.setAdapter(adapter);
                        }
                    } else {
                        // error case
                        switch (response.code()) {
                            case 404:
                                //page not found
                                Snackbar.make(layout, "Unable to process, Try after some times", Snackbar.LENGTH_LONG)
                                        .show();
                                break;
                            case 500:
                                Snackbar.make(layout, "Unable to connect server, Try after some times.", Snackbar.LENGTH_LONG)
                                        .show();
                                break;
                            default:
                                Snackbar.make(layout, "Unknown error, Try after some times.", Snackbar.LENGTH_LONG)
                                        .show();
                                break;
                        }
                    }
                } catch (Exception e) {
                    Snackbar.make(layout, "Unable to process your request..!", Snackbar.LENGTH_LONG)
                            .setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    progressDialog.show();
                                    loadData();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<List<SearchModal>> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("Throwable", "" + t);
                if (t instanceof SocketTimeoutException) {
                    Log.e("Throwable", "" + t);
                    Snackbar.make(layout, "Unable to connect server, Try after some times.", Snackbar.LENGTH_LONG)
                            .show();
                } else if (t instanceof ConnectException) {
                    Log.e("internet", "" + t);
                    checkInternet();
                } else {
                    Snackbar.make(layout, "Unable to process, Try after some times.", Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
    }
    private void checkInternet() {
        //inetrnet state
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)) {

            new AlertDialog.Builder(this)
                    .setTitle("Alert..!!")
                    .setMessage("No internet connection. Make sure that Wi-Fi or mobile data is turned on.")
                    .setCancelable(false)
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkInternet();
                        }
                    }).show();
        } else {
            progressDialog.show();
            loadData();
        }
    }
    private void filter(String text) {
        ArrayList<SearchModal> filteredList = new ArrayList<>();
        for (SearchModal item : modals) {
            if (item.getFishname().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }
}