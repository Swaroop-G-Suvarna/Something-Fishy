package info.accolade.fishing_master.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import info.accolade.fishing_master.LoginActivity;
import info.accolade.fishing_master.MainActivity;
import info.accolade.fishing_master.R;
import info.accolade.fishing_master.modal.DefaultResponceModal;
import info.accolade.fishing_master.utils.ApiClient;
import info.accolade.fishing_master.utils.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedBackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedBackFragment extends Fragment {
    MaterialButtonToggleGroup btnrating;
    EditText ed_feedback;
    Button send;
    ProgressDialog dialog;
    int rating;
    String feedback, userid;
    LinearLayout layout;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FeedBackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedBackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedBackFragment newInstance(String param1, String param2) {
        FeedBackFragment fragment = new FeedBackFragment();
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
        View root = inflater.inflate(R.layout.fragment_feed_back, container, false);

        dialog = new ProgressDialog(getContext(),R.style.AppCompatAlertDialogStyle);
        dialog.setCancelable(false);
        dialog.setTitle(R.string.waite);
        dialog.setIcon(R.drawable.boat);
        dialog.setMessage(getString(R.string.message));

        ed_feedback = root.findViewById(R.id.feedback);
        send = root.findViewById(R.id.sendFeedback);
        btnrating = root.findViewById(R.id.toggle_button_group);
        layout = root.findViewById(R.id.feedbackLayout);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
        userid = sharedPreferences.getString("UserId","");
        Log.e("UserId",userid);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = btnrating.getCheckedButtonId();
                feedback = ed_feedback.getText().toString().trim();
                if(rating<=0)
                {
                    Toast.makeText(getContext(), "Kindly choose an emoji..!", Toast.LENGTH_SHORT).show();
                }
                else if(feedback.length()<15)
                {
                    ed_feedback.setError("Your thoughts must be 15 or more character long..");
                }
                else
                {
                    dialog.show();
                    sendData();
                }
            }
        });
        return root;
    }

    private void sendData() {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<DefaultResponceModal> defaultResponceModalCall = apiInterface.getFeedbackResponce(rating+"", feedback,userid);
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
                                Log.e("feedback success", "");
                                Snackbar.make(layout, "Feedback sent successfully..", Snackbar.LENGTH_SHORT).show();
                                ed_feedback.setText("");
                            }
                            else
                            {
                                Log.e("unsuccess feedback", "");
                                Snackbar.make(layout, "Unable to submit your feedback..", Snackbar.LENGTH_LONG)
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
                            Log.e("Empty body response", "");
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