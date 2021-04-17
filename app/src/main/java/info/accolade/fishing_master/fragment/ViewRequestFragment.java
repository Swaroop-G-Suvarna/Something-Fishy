package info.accolade.fishing_master.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

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

import info.accolade.fishing_master.R;
import info.accolade.fishing_master.adapter.RequestAdapter;
import info.accolade.fishing_master.adapter.ViewRequestAdapter;
import info.accolade.fishing_master.modal.RequestModal;
import info.accolade.fishing_master.utils.ApiClient;
import info.accolade.fishing_master.utils.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewRequestFragment extends Fragment {
    ProgressDialog dialog;
    ArrayList<RequestModal> modals = new ArrayList<>();
    private ViewRequestAdapter adapter;
    RecyclerView recyclerView;
    ViewRequestAdapter.RecyclerViewClickListener listener;
    LinearLayout layout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ViewRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewRequestFragment newInstance(String param1, String param2) {
        ViewRequestFragment fragment = new ViewRequestFragment();
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
        View root = inflater.inflate(R.layout.fragment_view_request, container, false);

        recyclerView = root.findViewById(R.id.viewrequestRecycle);
        layout = root.findViewById(R.id.viewrequestlayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //progress bar
        dialog = new ProgressDialog(getContext(),R.style.AppCompatAlertDialogStyle);
        dialog.setCancelable(false);
        dialog.setTitle(R.string.waite);
        dialog.setIcon(R.drawable.boat);
        dialog.setMessage(getString(R.string.message));
        dialog.show();

        loadData();

        return root;
    }
    private void loadData() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
        String uid = sharedPreferences.getString("UserId","");
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<RequestModal>> requestCall = apiInterface.getViewUserRequestData(uid);
        requestCall.enqueue(new Callback<List<RequestModal>>() {
            @Override
            public void onResponse(Call<List<RequestModal>> call, Response<List<RequestModal>> response) {
                dialog.dismiss();

                try {
                    List<RequestModal> postResponses = response.body();
                    if(response.isSuccessful()) {
                        if (response.body() != null) {
                            modals = new ArrayList<>(response.body());

                            listener = new ViewRequestAdapter.RecyclerViewClickListener() {
                                @Override
                                public void onClick(View v, int position) {

                                }
                            };
                            adapter = new ViewRequestAdapter(getContext(), modals, listener);
                            recyclerView.setAdapter(adapter);
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
            public void onFailure(Call<List<RequestModal>> call, Throwable t) {
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