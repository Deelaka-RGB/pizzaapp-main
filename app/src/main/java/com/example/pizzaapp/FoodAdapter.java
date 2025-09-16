package com.example.pizzaapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for displaying FoodItem list in the customer screens.
 * Item layout must be R.layout.item_food_card with ids:
 *   imgFood, tvTitle, tvSubtitle, tvRating, tvPrice
 */
public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private final List<FoodItem> items;
    private final OnFoodClickListener listener;

    /** Listener signature expected by MainActivity (do NOT change). */
    public interface OnFoodClickListener {
        void onItemClick(FoodItem item);   // open detail
        void onLikeClick(FoodItem item);   // toggle favorite/like
    }

    /** Constructor that MainActivity uses (list + listener). */
    public FoodAdapter(@NonNull List<FoodItem> items,
                       @NonNull OnFoodClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    /** Optional extra constructor (if you ever need adapter without clicks). */
    public FoodAdapter(@NonNull List<FoodItem> items) {
        this(items, new OnFoodClickListener() {
            @Override public void onItemClick(FoodItem item) {}
            @Override public void onLikeClick(FoodItem item) {}
        });
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_card, parent, false);
        return new FoodViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem m = items.get(position);

        // Use getters; if your FoodItem uses public fields, replace with m.title etc.
        holder.tvTitle.setText(m.getTitle());
        holder.tvSubtitle.setText(m.getSubtitle());
        holder.tvRating.setText("★ " + m.getRating());
        holder.tvPrice.setText("Rs " + m.getPrice());
        holder.imgFood.setImageResource(m.getImageRes());

        // Click opens details
        holder.itemView.setOnClickListener(v -> listener.onItemClick(m));

        // Long-press toggles like/favorite (since we don't have a separate like button id here)
        holder.itemView.setOnLongClickListener(v -> {
            listener.onLikeClick(m);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return (items == null) ? 0 : items.size();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        final ImageView imgFood;
        final TextView tvTitle, tvSubtitle, tvRating, tvPrice;

        FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood   = itemView.findViewById(R.id.imgFood);
            tvTitle   = itemView.findViewById(R.id.tvTitle);
            tvSubtitle= itemView.findViewById(R.id.tvSubtitle);
            tvRating  = itemView.findViewById(R.id.tvRating);
            tvPrice   = itemView.findViewById(R.id.tvPrice);
        }
    }
}
