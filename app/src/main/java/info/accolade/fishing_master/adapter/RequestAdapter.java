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
import info.accolade.fishing_master.modal.RequestModal;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private ArrayList<RequestModal> requestModal = new ArrayList<>();
    private Context context;
    private RequestAdapter.RecyclerViewClickListener listener;

    public RequestAdapter(Context context, ArrayList<RequestModal> requestModal, RequestAdapter.RecyclerViewClickListener listener) {
        this.requestModal = requestModal;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requestrecycler, parent, false);
        return new RequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder holder, int position) {
        holder.name.setText("Name : "+requestModal.get(position).getUserName());
        holder.date.setText("Date : "+requestModal.get(position).getRequestDate());
        holder.email.setText(requestModal.get(position).getUserEmail());
        holder.fish.setText("Fish : "+requestModal.get(position).getFishName());
        holder.number.setText(requestModal.get(position).getUserNumber());
    }

    @Override
    public int getItemCount() {
        return requestModal.size();
    }
    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name, date, number, email, fish;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.uname);
            date = itemView.findViewById(R.id.udate2);
            email = itemView.findViewById(R.id.uemail);
            number = itemView.findViewById(R.id.uphone);
            fish = itemView.findViewById(R.id.ufish);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
}
