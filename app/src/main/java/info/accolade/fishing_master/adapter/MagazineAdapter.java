package info.accolade.fishing_master.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import info.accolade.fishing_master.R;
import info.accolade.fishing_master.modal.MagazineModal;

public class MagazineAdapter extends RecyclerView.Adapter<MagazineAdapter.ViewHolder> {

    private ArrayList<MagazineModal> magazineModal = new ArrayList<>();
    private Context context;
    private RecyclerViewClickListener listener;

    public MagazineAdapter(Context context, ArrayList<MagazineModal> magazineModal, RecyclerViewClickListener listener) {
        this.magazineModal = magazineModal;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MagazineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.magazine_recycler, parent, false);
        return new MagazineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MagazineAdapter.ViewHolder holder, int position) {
        holder.name.setText(magazineModal.get(position).getName());
        holder.date.setText(magazineModal.get(position).getDate());
        holder.desc.setText(magazineModal.get(position).getDesc());
    }

    @Override
    public int getItemCount() {
        return magazineModal.size();
    }
    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name, date, desc;
        CardView cv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.ename);
            date = itemView.findViewById(R.id.edate);
            desc = itemView.findViewById(R.id.edesc);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
}
