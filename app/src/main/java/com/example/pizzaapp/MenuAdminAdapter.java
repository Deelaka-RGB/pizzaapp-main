package com.example.pizzaapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MenuAdminAdapter extends RecyclerView.Adapter<MenuAdminAdapter.MenuViewHolder> {

    private final List<FoodItem> items;

    public MenuAdminAdapter(List<FoodItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_admin, parent, false);
        return new MenuViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        FoodItem m = items.get(position);

        // If your FoodItem uses getters, keep these:
        holder.tvMenuTitle.setText(m.getTitle());
        holder.tvMenuSubtitle.setText(m.getSubtitle());
        holder.tvMenuPrice.setText("Rs " + m.getPrice());
        holder.imgMenu.setImageResource(m.getImageRes());

        // If your FoodItem uses public fields instead of getters, use:
        // holder.tvMenuTitle.setText(m.title);
        // holder.tvMenuSubtitle.setText(m.subtitle);
        // holder.tvMenuPrice.setText("Rs " + m.price);
        // holder.imgMenu.setImageResource(m.imageRes);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView tvMenuTitle, tvMenuSubtitle, tvMenuPrice;
        ImageView imgMenu;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMenuTitle   = itemView.findViewById(R.id.tvMenuTitle);
            tvMenuSubtitle= itemView.findViewById(R.id.tvMenuSubtitle);
            tvMenuPrice   = itemView.findViewById(R.id.tvMenuPrice);
            imgMenu       = itemView.findViewById(R.id.imgMenu);
        }
    }
}
