package info.accolade.fishing_master.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import info.accolade.fishing_master.R;
import info.accolade.fishing_master.adapter.RescueAdapter;
import info.accolade.fishing_master.modal.RescueModal;
import info.accolade.fishing_master.utils.ApiClient;
import info.accolade.fishing_master.utils.ApiInterface;
import info.accolade.fishing_master.utils.LocationTrack;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NearByFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearByFragment extends Fragment {
    ApiInterface apiInterface;
    ProgressDialog progressDialog;

    ArrayList<RescueModal> rescueModals = new ArrayList<>();
    private RescueAdapter adapter;
    RecyclerView recyclerView;
    RescueAdapter.RecyclerViewClickListener listener;
    LinearLayout layout;
    AppCompatButton btnfive, btnten, btntwofive;
    String radius = "50";
    LocationTrack locationTrack;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NearByFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NearByFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NearByFragment newInstance(String param1, String param2) {
        NearByFragment fragment = new NearByFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_near_by, container, false);

        btnfive = view.findViewById(R.id.five);
        btnten = view.findViewById(R.id.ten);
        btntwofive = view.findViewById(R.id.twofive);

        btnfive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radius = "50";
                btnfive.setBackgroundResource(R.drawable.send_sms_button);
                btnten.setBackgroundResource(R.drawable.call_button);
                btntwofive.setBackgroundResource(R.drawable.call_button);
                progressDialog.show();
                loadData();
            }
        });

        btnten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radius = "100";
                btnfive.setBackgroundResource(R.drawable.call_button);
                btnten.setBackgroundResource(R.drawable.send_sms_button);
                btntwofive.setBackgroundResource(R.drawable.call_button);
                progressDialog.show();
                loadData();
            }
        });

        btntwofive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radius = "All";
                btnfive.setBackgroundResource(R.drawable.call_button);
                btnten.setBackgroundResource(R.drawable.call_button);
                btntwofive.setBackgroundResource(R.drawable.send_sms_button);
                progressDialog.show();
                loadData();
            }
        });

        recyclerView = view.findViewById(R.id.escortsRecycle);
        layout = view.findViewById(R.id.hosLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //progress bar
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        loadData();

        return view;
    }
    private void loadData() {
        locationTrack = new LocationTrack(getActivity());
        String latitude=locationTrack.getLatitude()+"";
        String longitude=locationTrack.getLongitude()+"";

        Log.e("latitude is: ",latitude);
        Log.e("longitude is: ",longitude);

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<RescueModal>> hospitalModalCall = apiInterface.getRescueData(radius, latitude, longitude);
        hospitalModalCall.enqueue(new Callback<List<RescueModal>>() {
            @Override
            public void onResponse(Call<List<RescueModal>> call, Response<List<RescueModal>> response) {
                progressDialog.dismiss();

                try {
                    List<RescueModal> postResponses = response.body();
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            rescueModals = new ArrayList<>(response.body());

                            Log.e("hospitalModals", ""+rescueModals);
                            listener = new RescueAdapter.RecyclerViewClickListener() {
                                @Override
                                public void onClick(View v, int position) {
//                                    Intent i = new Intent(getContext(), DetailsBookActivity.class);
//                                    i.putExtra("book_uid", hospitalModals.get(position).getBookuid());
//                                    startActivity(i);
                                }
                            };
                            adapter = new RescueAdapter(getContext(), rescueModals, listener);
                            recyclerView.setAdapter(adapter);


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
            public void onFailure(Call<List<RescueModal>> call, Throwable t) {
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
        ConnectivityManager connectivityManager = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)) {

            new AlertDialog.Builder(getContext())
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
}