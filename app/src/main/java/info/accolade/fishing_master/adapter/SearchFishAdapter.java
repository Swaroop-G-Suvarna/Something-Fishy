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

public class SearchFishAdapter  extends RecyclerView.Adapter<SearchFishAdapter.ViewHolder> {

    private ArrayList<SearchModal> searchModal = new ArrayList<>();
    private Context context;
    private SearchFishAdapter.RecyclerViewClickListener listener;

    public SearchFishAdapter(Context context, ArrayList<SearchModal> searchModal, SearchFishAdapter.RecyclerViewClickListener listener) {
        this.searchModal = searchModal;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchFishAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fish_recycler, parent, false);
        return new SearchFishAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchFishAdapter.ViewHolder holder, int position) {
        holder.boatnme2.setText("Boat : "+searchModal.get(position).getBoatname());
        holder.boatno2.setText("No : "+searchModal.get(position).getBoatno());
        holder.arrdate2.setText("Arriving on : "+searchModal.get(position).getArrivingdate());
        holder.fishname2.setText("Fish : "+searchModal.get(position).getFishname());
        holder.postdate2.setText("Posted on : "+searchModal.get(position).getCreateddate());

        try {
            Picasso.get().load("http://192.168.43.124/fishing-master-php/img/" + searchModal.get(position).getImage()).into(holder.bk_imgss);
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
        ImageView bk_imgss;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            boatnme2 = itemView.findViewById(R.id.boatnme);
            boatno2 = itemView.findViewById(R.id.boatno);
            arrdate2 = itemView.findViewById(R.id.arrdate);
            fishname2 = itemView.findViewById(R.id.fishnme);
            bk_imgss = itemView.findViewById(R.id.bk_img2ss);
            postdate2 = itemView.findViewById(R.id.postdate);
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
