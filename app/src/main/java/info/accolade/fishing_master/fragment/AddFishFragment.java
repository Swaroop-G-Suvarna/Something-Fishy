package info.accolade.fishing_master.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Calendar;

import info.accolade.fishing_master.R;
import info.accolade.fishing_master.modal.DefaultResponceModal;
import info.accolade.fishing_master.utils.ApiClient;
import info.accolade.fishing_master.utils.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFishFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFishFragment extends Fragment {
    EditText edname, eddesc, eddate;
    String name, desc, date;
    ApiInterface apiInterface;
    ProgressDialog dialog;
    LinearLayout layout;
    ImageView imgLog;
    Boolean isImgCheck = false;
    Bitmap bitmap;
    Button btnsubmit;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddFishFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFishFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFishFragment newInstance(String param1, String param2) {
        AddFishFragment fragment = new AddFishFragment();
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
        View root = inflater.inflate(R.layout.fragment_add_fish, container, false);

        eddate = root.findViewById(R.id.fdate2);
        eddesc = root.findViewById(R.id.fdesc2);
        edname = root.findViewById(R.id.fname2);

        layout = root.findViewById(R.id.addFishLayout2);
        imgLog = root.findViewById(R.id.logImg);
        btnsubmit = root.findViewById(R.id.fsubmit2);

        //progress bar
        dialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
        dialog.setCancelable(false);
        dialog.setTitle(R.string.waite);
        dialog.setIcon(R.drawable.boat);
        dialog.setMessage(getString(R.string.message));

        imgLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnableRuntimePermission();
            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edname.getText().toString().isEmpty())
                {
                    edname.setError("Please enter Fishname");
                }
                else if (eddate.getText().toString().isEmpty())
                {
                    eddate.setError("Please enter Date");
                }
                else if (eddesc.getText().toString().isEmpty())
                {
                    eddesc.setError("Please enter Description");
                }
                else if (!isImgCheck)
                {
                    Snackbar.make(layout, "Kindly capture image..!", Snackbar.LENGTH_LONG)
                            .show();
                }
                else
                {
                    dialog.show();
                    name = edname.getText().toString().trim();
                    date = eddate.getText().toString().trim();
                    desc = eddesc.getText().toString().trim();

                    File file = savebitmap(bitmap);
                    sendData();
                }
            }
        });

        return root;
    }

    private void sendData() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100,byteArrayOutputStream);

        String img = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        String nameimg = String.valueOf(Calendar.getInstance().getTimeInMillis());

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
        String boatno = sharedPreferences.getString("BoatNumber","");
        String boatname = sharedPreferences.getString("BoatName","");

        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<DefaultResponceModal> loginModalCall =apiInterface.getFishResponce(name, desc, date, nameimg, img, boatname, boatno);
        loginModalCall.enqueue(new Callback<DefaultResponceModal>() {
            @Override
            public void onResponse(@NonNull Call<DefaultResponceModal> call, @NonNull Response<DefaultResponceModal> response) {
                dialog.dismiss();
                try {
                    if(response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getSuccess()) {
                                Toast.makeText(getContext(), "Record added successfully...", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Snackbar.make(layout, "Unable to add data..!", Snackbar.LENGTH_LONG)
                                        .show();
                            }
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
                                    sendData();
                                }
                            })
                            .show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<DefaultResponceModal> call, Throwable t) {
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

    private File savebitmap(Bitmap bmp) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        // String temp = null;
        File file = new File(extStorageDirectory, "temp.png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, "temp.png");
        }

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    public void EnableRuntimePermission(){
        Log.e("h","");
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            Snackbar.make(layout, "CAMERA permission require us to Proceed..!", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EnableRuntimePermission();
                        }
                    })
                    .show();
        } else {
            Log.e("hw","");
//            ActivityCompat.requestPermissions(getActivity(),new String[]{
//                    Manifest.permission.CAMERA}, 1);
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 7);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] result) {
        switch (requestCode) {
            case 1:
                if (result.length > 0 || result[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.e("ha","");
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 7);
                }
                else
                {
                    Log.e("has","");
                    Snackbar.make(layout, "Permission Canceled, CAMERA permission require us to Proceed..!", Snackbar.LENGTH_LONG)
                            .setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    EnableRuntimePermission();
                                }
                            })
                            .show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK) {
            Log.e("hsd","");
            bitmap = (Bitmap) data.getExtras().get("data");
            imgLog.setImageBitmap(bitmap);
            isImgCheck = true;
        }
        else
        {
            Log.e("ah","");
            isImgCheck = false;
        }
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
            sendData();
        }
    }
}