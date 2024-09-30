package com.example.halkaarzkarhesaplama;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GunAdapter extends RecyclerView.Adapter<GunAdapter.GunViewHolder> {

    private List<String> gunList;

    public GunAdapter(List<String> gunList) {
        this.gunList = gunList;
    }

    @NonNull
    @Override
    public GunViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gun_item, parent, false);
        return new GunViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GunViewHolder holder, int position) {
        String gunBilgisi = gunList.get(position);
        holder.tvGunBilgi.setText(gunBilgisi);

        // Eğer metin "Toplam Edilen Kar" içeriyorsa, rengi yeşil yap
        if (gunBilgisi.contains("Toplam Edilen Kar")) {
            holder.tvGunBilgi.setTextColor(Color.GREEN);  // Metnin rengini yeşil yap
        } else {
            holder.tvGunBilgi.setTextColor(Color.BLACK);  // Diğer metinler siyah olsun
        }
    }

    @Override
    public int getItemCount() {
        return gunList.size();
    }

    public static class GunViewHolder extends RecyclerView.ViewHolder {
        TextView tvGunBilgi;

        public GunViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGunBilgi = itemView.findViewById(R.id.tvGunBilgi);
        }
    }
}