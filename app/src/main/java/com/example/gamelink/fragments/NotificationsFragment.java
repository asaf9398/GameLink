package com.example.gamelink.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamelink.R;
import com.example.gamelink.adapters.NotificationAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView notificationsRecyclerView;
    private NotificationAdapter notificationAdapter;
    private List<String> notifications;
    private ImageButton markAllReadButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        notificationsRecyclerView = view.findViewById(R.id.notifications_recycler_view);
        markAllReadButton = view.findViewById(R.id.notifications_mark_all_read_button);

        notifications = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notifications);

        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationsRecyclerView.setAdapter(notificationAdapter);

        // לדוגמה: הוספת התראות
        notifications.add("New message from John");
        notifications.add("Reminder: Play session at 6 PM");
        notifications.add("Update: New version of GameLink is available");
        notificationAdapter.notifyDataSetChanged();

        // מאפשר מחיקת התראה בהחלקה (Swipe)
        enableSwipeToDelete();

        // כפתור "סמן הכל כנקרא"
        markAllReadButton.setOnClickListener(v -> {
            // כאן אפשר לממש לוגיקה של "לסמן כל ההתראות כנקראו"
            // לדוגמה ננקה רשימה
            notifications.clear();
            notificationAdapter.notifyDataSetChanged();
            Snackbar.make(v, "All notifications marked as read!", Snackbar.LENGTH_SHORT).show();
        });

        return view;
    }

    private void enableSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        // לא דורש גרירה להחלפת מיקומים
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        // מחיקת התראה מהרשימה
                        int position = viewHolder.getAdapterPosition();
                        notifications.remove(position);
                        notificationAdapter.notifyItemRemoved(position);
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(notificationsRecyclerView);
    }
}
