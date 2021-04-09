package info.accolade.fishing_master.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import info.accolade.fishing_master.R;
import info.accolade.fishing_master.modal.EmergencyModal;
import info.accolade.fishing_master.utils.LocationTrack;
import info.accolade.fishing_master.utils.SmsDeliveredReceiver;
import info.accolade.fishing_master.utils.ApiClient;
import info.accolade.fishing_master.utils.ApiInterface;
import info.accolade.fishing_master.utils.SmsSentReceiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FishersHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FishersHomeFragment extends Fragment {
    ProgressDialog dialog;
    List<EmergencyModal> postResponses;
    LinearLayout layout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FishersHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FishersHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FishersHomeFragment newInstance(String param1, String param2) {
        FishersHomeFragment fragment = new FishersHomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_fishers_home, container, false);

        //progress bar
        dialog = new ProgressDialog(getContext(),R.style.AppCompatAlertDialogStyle);
        dialog.setCancelable(false);
        dialog.setTitle(R.string.waite);
        dialog.setIcon(R.drawable.boat);
        dialog.setMessage(getString(R.string.message));

        layout = view.findViewById(R.id.fishrehomelayout);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.SEND_SMS},
                    2);
        }

        AppCompatButton emergency = view.findViewById(R.id.buttonemergencycall);
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                loadData();
            }
        });
        return view;
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

    private void loadData() {
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
    private void checkInternet() {
        //inetrnet state
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(!(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
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
        }
        else
        {
            dialog.show();
            loadData();
        }
    }
}