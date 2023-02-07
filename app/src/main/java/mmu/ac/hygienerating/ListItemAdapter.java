package mmu.ac.hygienerating;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//This class takes the location details passed to it and stores the information in a list item for the recycler view
public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ViewHolder> {

    private List<LocationDetails> locationDetails;
    private Context context;

    ListItemAdapter(List<LocationDetails> locationDetails, Context context) {
        this.locationDetails = locationDetails;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationDetails locationDetails = this.locationDetails.get(position);

        holder.header.setText(locationDetails.getHeader());
        holder.desc.setText(locationDetails.getDesc());

        int ratingValue;

        //Assign the correct hygiene rating image based on the rating of the food place
        switch (locationDetails.getRating()) {
            case "0":
                ratingValue = R.drawable.rating0;
                break;
            case "1":
                ratingValue = R.drawable.rating1;
                break;
            case "2":
                ratingValue = R.drawable.rating2;
                break;
            case "3":
                ratingValue = R.drawable.rating3;
                break;
            case "4":
                ratingValue = R.drawable.rating4;
                break;
            case "5":
                ratingValue = R.drawable.rating5;
                break;
            default:
                ratingValue = R.drawable.rating_exempt;
                break;
        }

        holder.rating.setImageResource(ratingValue);

    }

    @Override
    public int getItemCount() {
        return locationDetails.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView header;
        TextView desc;
        ImageView rating;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            header = itemView.findViewById(R.id.header);
            desc = itemView.findViewById(R.id.desc);
            rating = itemView.findViewById(R.id.rating);
        }
    }
}
