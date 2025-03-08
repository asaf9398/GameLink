package com.example.gamelink.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import com.example.gamelink.R;
import com.example.gamelink.firebase.FirebaseDatabaseManager;

import java.util.List;

public class FavoriteGamesAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> games;
    private String userId;
    private FirebaseDatabaseManager databaseManager;

    public FavoriteGamesAdapter(Context context, List<String> games, String userId, FirebaseDatabaseManager databaseManager) {
        super(context, R.layout.custom_game_item, games);
        this.context = context;
        this.games = games;
        this.userId = userId;
        this.databaseManager = databaseManager;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_game_item, parent, false);
        }

        TextView gameNameText = convertView.findViewById(R.id.game_name_text);
        Button deleteGameButton = convertView.findViewById(R.id.delete_game_button);

        String gameName = games.get(position);
        gameNameText.setText(gameName);

        deleteGameButton.setOnClickListener(v -> {
            // מחיקת המשחק מרשימת המועדפים גם ב-Firebase וגם בתצוגה
            databaseManager.removeFavoriteGame(userId, gameName);
            games.remove(position);
            notifyDataSetChanged(); // עדכון התצוגה
        });

        return convertView;
    }
}
