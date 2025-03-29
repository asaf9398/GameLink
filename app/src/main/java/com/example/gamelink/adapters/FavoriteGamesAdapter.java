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
import com.example.gamelink.models.Game;

import java.util.List;

public class FavoriteGamesAdapter extends ArrayAdapter<Game> {

    private Context context;
    private List<Game> games;
    private String userId;
    private FirebaseDatabaseManager databaseManager;

    public FavoriteGamesAdapter(Context context,
                                List<Game> games,
                                String userId,
                                FirebaseDatabaseManager databaseManager) {
        super(context, R.layout.custom_game_item, games);
        this.context = context;
        this.games = games;
        this.userId = userId;
        this.databaseManager = databaseManager;
    }

    @NonNull
    @Override
    public View getView(int position,
                        @Nullable View convertView,
                        @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.custom_game_item, parent, false);
        }

        TextView gameNameText   = convertView.findViewById(R.id.game_name_text);
        Button   deleteGameButton = convertView.findViewById(R.id.delete_game_button);

        Game game = games.get(position);
        if(game != null) {
            gameNameText.setText(game.getGameName());

            deleteGameButton.setOnClickListener(v -> {
                databaseManager.removeFavoriteGameObject(userId, game.getGameId());
                games.remove(position);
                notifyDataSetChanged();
            });
        }

        return convertView;
    }
}

