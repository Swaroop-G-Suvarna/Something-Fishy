package info.accolade.fishing_master.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import info.accolade.fishing_master.R;
import info.accolade.fishing_master.adapter.RescueAdapter;
import info.accolade.fishing_master.modal.EmergencyModal;
import info.accolade.fishing_master.modal.RescueModal;
import info.accolade.fishing_master.utils.ApiClient;
import info.accolade.fishing_master.utils.ApiInterface;
import info.accolade.fishing_master.utils.LocationTrack;
import info.accolade.fishing_master.utils.SmsDeliveredReceiver;
import info.accolade.fishing_master.utils.SmsSentReceiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsFragment extends Fragment {
    private GoogleMap mMap;
    ProgressDialog dialog;
    LocationManager locationManager;
    LinearLayout layout;
    List<RescueModal> postResponse;
    List<EmergencyModal> postResponses;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            LatLng mangalore = new LatLng(12.87, 74.88);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(mangalore));

            dialog = new ProgressDialog(getContext(),R.style.AppCompatAlertDialogStyle);
            dialog.setCancelable(false);
            dialog.setTitle(R.string.waite);
            dialog.setIcon(R.drawable.boat);
            dialog.setMessage(getString(R.string.message));

            checkGpsEnabled();
            enableMyLocation();
            loadData();

        }
    };

    private void loadData() {
        dialog.show();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<RescueModal>> hospitalModalCall = apiInterface.getRescueLocationData();
        hospitalModalCall.enqueue(new Callback<List<RescueModal>>() {
            @Override
            public void onResponse(Call<List<RescueModal>> call, Response<List<RescueModal>> response) {
                dialog.dismiss();

                try {
                    if (response.isSuccessful()) {
                        postResponse = response.body();
                        if (postResponse != null) {
                            for (int i = 0; i < response.body().size(); i++) {
                                LatLng location = new LatLng(Double.parseDouble(response.body().get(i).getLatitude()), Double.parseDouble(response.body().get(i).getLongitude()));
                                mMap.addMarker(new MarkerOptions().position(location).title(response.body().get(i).getName()));
                            }
                        }
                        else
                        {
                            Snackbar.make(layout, "No data found", Snackbar.LENGTH_LONG)
                                    .show();
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
                                    loadData();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<List<RescueModal>> call, Throwable t) {
                dialog.dismiss();
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
            loadData();
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        layout = view.findViewById(R.id.mapLayout);
        TextView bname = view.findViewById(R.id.boat_name);
        TextView bno = view.findViewById(R.id.boat_number);
        TextView uname = view.findViewById(R.id.fisher_name);
        TextView uaddress = view.findViewById(R.id.fisher_address);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
        bname.setText(sharedPreferences.getString("BoatName",""));
        bno.setText(sharedPreferences.getString("BoatNumber",""));
        uname.setText(sharedPreferences.getString("UserName",""));
        uaddress.setText(sharedPreferences.getString("UserAddress",""));

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.SEND_SMS},
                    2);
        }

        AppCompatButton emergency = view.findViewById(R.id.buttonemecall);
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                loadEmergencyData();
            }
        });

        return view;
    }

    private void loadEmergencyData() {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<EmergencyModal>> magazineCall = apiInterface.getEmergencyResponse();
        magazineCall.enqueue(new Callback<List<EmergencyModal>>() {
            @Override
            public void onResponse(Call<List<EmergencyModal>> call, Response<List<EmergencyModal>> response) {
                dialog.dismiss();
                try {
                    if(response.isSuccessful()) {
                        if (response.body() != null) {
                            postResponses = response.body();
                            sendMessage();
                        }
                    }
                    else {
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

                }
                catch (Exception e)
                {
                    Snackbar.make(layout, "Unable to process your request..!", Snackbar.LENGTH_LONG)
                            .setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.show();
                                    loadData();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<List<EmergencyModal>> call, Throwable t) {
                dialog.dismiss();
                Log.e("Throwable",""+t);
                if(t instanceof SocketTimeoutException)
                {
                    Log.e("Throwable",""+t);
                    Snackbar.make(layout, "Unable to connect server, Try after some times.", Snackbar.LENGTH_LONG)
                            .show();
                }
                else if(t instanceof ConnectException)
                {
                    Log.e("internet",""+t);
                    checkInternet();
                }
                else
                {
                    Snackbar.make(layout, "Unable to login, Try after some times.", Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private void sendMessage() {
        LocationTrack locationTrack = new LocationTrack(getActivity());
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("loginDetails", Context.MODE_PRIVATE);

        String message = "Its an Emergency\nUser Details\nName: "+sharedPreferences.getString("UserName","")+"\nAddress: "+sharedPreferences.getString("UserAddress","")+"\nBoat Details\nBoat Name: "+sharedPreferences.getString("BoatName","")+"\nBoat Number: "+sharedPreferences.getString("BoatNumber","")+"\nLocation Details\nLatitude: "+locationTrack.getLatitude()+"\nLongitude: "+locationTrack.getLongitude();
        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();
        PendingIntent sentPI = PendingIntent.getBroadcast(getContext(), 0,
                new Intent(getContext(), SmsSentReceiver.class), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(getContext(), 0,
                new Intent(getContext(), SmsDeliveredReceiver.class), 0);
        try {
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> mSMSMessage = sms.divideMessage(message);
            for (int i = 0; i < mSMSMessage.size(); i++) {
                sentPendingIntents.add(i, sentPI);
                deliveredPendingIntents.add(i, deliveredPI);
            }
            for(int i=0;i<postResponses.size();i++)
            {
                Log.e("number",postResponses.get(i).getNumber());
                sms.sendMultipartTextMessage(postResponses.get(i).getNumber(), null, mSMSMessage,
                        sentPendingIntents, deliveredPendingIntents);
            }

            Toast.makeText(getContext(), "Message sent successfully..", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(getContext(), "SMS sending failed...",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                    break;
                }
        }
    }
    private void checkGpsEnabled() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
            builder.setTitle("Alert..!")
                    .setMessage("Kindly Enable GPS")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new  DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkGpsEnabled();
                        }
                    });

            final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}