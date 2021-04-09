package info.accolade.fishing_master.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import info.accolade.fishing_master.R;
import info.accolade.fishing_master.modal.RescueModal;

public class RescueAdapter extends RecyclerView.Adapter<RescueAdapter.ViewHolder> {

    private ArrayList<RescueModal> rescueModal = new ArrayList<>();
    private Context context;
    private RecyclerViewClickListener listener;

    public RescueAdapter(Context context, ArrayList<RescueModal> rescueModal, RecyclerViewClickListener listener) {
        this.rescueModal = rescueModal;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RescueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_recycler, parent, false);
        return new RescueAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RescueAdapter.ViewHolder holder, int position) {
        holder.name.setText(rescueModal.get(position).getName());
        holder.address.setText(rescueModal.get(position).getAddress());
        holder.phone.setText("  "+rescueModal.get(position).getContact());

        holder.direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitude = rescueModal.get(position).getLatitude();
                String longitude = rescueModal.get(position).getLongitude();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr="+latitude+","+longitude));
                Log.e("location","http://maps.google.com/maps?daddr="+latitude+","+longitude);
                context.startActivity(intent);
            }
        });

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE},1);
                }
                else
                {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + rescueModal.get(position).getContact()));
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rescueModal.size();
    }
    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name, address, phone;
        FloatingActionButton direction, call;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.hname);
            address = itemView.findViewById(R.id.haddress);
            phone = itemView.findViewById(R.id.hphone);
            direction = itemView.findViewById(R.id.location);
            call = itemView.findViewById(R.id.call);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
}
