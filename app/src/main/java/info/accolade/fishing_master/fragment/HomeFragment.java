package info.accolade.fishing_master.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.DefaultSliderView;
import com.glide.slider.library.tricks.ViewPagerEx;
import com.google.android.material.snackbar.Snackbar;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import info.accolade.fishing_master.BoatSearchActivity;
import info.accolade.fishing_master.DetailsActivity;
import info.accolade.fishing_master.R;
import info.accolade.fishing_master.adapter.SearchBoatAdapater;
import info.accolade.fishing_master.adapter.SearchFishAdapter;
import info.accolade.fishing_master.modal.SearchModal;
import info.accolade.fishing_master.utils.ApiClient;
import info.accolade.fishing_master.utils.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener{
    private SliderLayout mDemoSlider;
    ApiInterface apiIterface;
    ProgressDialog progressDialog;

    ArrayList<SearchModal> modals = new ArrayList<>();
    private SearchFishAdapter adapter;
    RecyclerView recycle;
    SearchFishAdapter.RecyclerViewClickListener listener;
    LinearLayout layout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //        slider start
        mDemoSlider = view.findViewById(R.id.slider);
        ArrayList<Integer> listUrl = new ArrayList<>();

        listUrl.add(R.drawable.ban1);
        listUrl.add(R.drawable.ban2);
        listUrl.add(R.drawable.ban3);
        listUrl.add(R.drawable.ban4);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();

        for (int i = 0; i < listUrl.size(); i++) {
            DefaultSliderView sliderView = new DefaultSliderView(getContext());

            sliderView
                    .image(listUrl.get(i))
                    .setRequestOption(requestOptions)
                    .setProgressBarVisible(true)
                    .setOnSliderClickListener(this);

            mDemoSlider.addSlider(sliderView);
        }

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
        mDemoSlider.stopCyclingWhenTouch(false);
//        slider end

        recycle = view.findViewById(R.id.home_menu_recycler1);
        layout = view.findViewById(R.id.homeLayout);
        recycle.setLayoutManager(new LinearLayoutManager(getContext()));

        //progress bar
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        loadData();

        return view;
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
                                    Intent i = new Intent(getActivity(), DetailsActivity.class);
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
                            adapter = new SearchFishAdapter(getContext(), modals, listener);
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
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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