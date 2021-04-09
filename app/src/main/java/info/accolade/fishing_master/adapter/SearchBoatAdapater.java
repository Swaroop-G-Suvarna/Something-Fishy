package info.accolade.fishing_master.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.accolade.fishing_master.R;
import info.accolade.fishing_master.modal.SearchModal;

public class SearchBoatAdapater  extends RecyclerView.Adapter<SearchBoatAdapater.ViewHolder> {

    private ArrayList<SearchModal> searchModal = new ArrayList<>();
    private Context context;
    private SearchBoatAdapater.RecyclerViewClickListener listener;

    public SearchBoatAdapater(Context context, ArrayList<SearchModal> searchModal, SearchBoatAdapater.RecyclerViewClickListener listener) {
        this.searchModal = searchModal;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchBoatAdapater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.boat_recycler, parent, false);
        return new SearchBoatAdapater.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchBoatAdapater.ViewHolder holder, int position) {
        holder.boatnme2.setText("Boat : "+searchModal.get(position).getBoatname());
        holder.boatno2.setText("No : "+searchModal.get(position).getBoatno());
        holder.arrdate2.setText("Arriving on : "+searchModal.get(position).getArrivingdate());
        holder.fishname2.setText("Fish : "+searchModal.get(position).getFishname());
        holder.postdate2.setText("Posted on : "+searchModal.get(position).getCreateddate());

        try{
        Picasso.get().load("http://192.168.43.124/fishing-master-php/img/" + searchModal.get(position).getImage()).into(holder.bk_imga);
        }
        catch (Exception e){}
    }

    @Override
    public int getItemCount() {
        return searchModal.size();
    }
    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView boatnme2, boatno2, arrdate2, fishname2, postdate2;
        ImageView bk_imga;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            boatnme2 = itemView.findViewById(R.id.boatnme2);
            boatno2 = itemView.findViewById(R.id.boatno2);
            arrdate2 = itemView.findViewById(R.id.arrdate2);
            fishname2 = itemView.findViewById(R.id.fishname2);
            bk_imga = itemView.findViewById(R.id.bk_imgaa);
            postdate2 = itemView.findViewById(R.id.postdate2);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
    public void filterList(ArrayList<SearchModal> filteredList) {
        searchModal = filteredList;
        notifyDataSetChanged();
    }
}
