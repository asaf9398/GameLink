package com.example.gamelink.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamelink.R;

import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder> {

    private final List<String> recommendations;

    public RecommendationAdapter(List<String> recommendations) {
        this.recommendations = recommendations;
    }

    @NonNull
    @Override
    public RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommendation, parent, false);
        return new RecommendationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationViewHolder holder, int position) {
        String recommendation = recommendations.get(position);
        holder.recommendationTextView.setText(recommendation);
    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }

    public static class RecommendationViewHolder extends RecyclerView.ViewHolder {
        TextView recommendationTextView;

        public RecommendationViewHolder(@NonNull View itemView) {
            super(itemView);
            recommendationTextView = itemView.findViewById(R.id.recommendation_text_view);
        }
    }
}
