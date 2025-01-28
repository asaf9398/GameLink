package com.example.gamelink.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamelink.R;
import com.example.gamelink.adapters.RecommendationAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecommendationsActivity extends AppCompatActivity {

    private RecyclerView recommendationsRecyclerView;
    private RecommendationAdapter recommendationAdapter;
    private List<String> recommendations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        recommendationsRecyclerView = findViewById(R.id.recommendations_recycler_view);
        recommendations = new ArrayList<>();
        recommendationAdapter = new RecommendationAdapter(recommendations);

        recommendationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recommendationsRecyclerView.setAdapter(recommendationAdapter);

        // לדוגמה: הוספת המלצות
        recommendations.add("Recommended game: Chess");
        recommendations.add("Recommended player: John");
        recommendationAdapter.notifyDataSetChanged();
    }
}
