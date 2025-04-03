package com.example.gamelink.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamelink.R;
import com.example.gamelink.adapters.NotificationAdapter;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.models.AppNotification;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView notificationsRecyclerView;
    private NotificationAdapter notificationAdapter;
    private List<AppNotification> notifications;
    private ImageButton markAllReadButton;

    private FirebaseDatabaseManager databaseManager;
    private String userId;

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

        databaseManager = new FirebaseDatabaseManager();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadNotificationsFromFirebase();
        enableSwipeToDelete();

        markAllReadButton.setOnClickListener(v -> {
            databaseManager.deleteAllNotifications(userId, new FirebaseDatabaseManager.OperationCallback() {
                @Override
                public void onSuccess() {
                    notifications.clear();
                    notificationAdapter.notifyDataSetChanged();
                    Snackbar.make(v, "All notifications marked as read!", Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "Failed to clear notifications", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }

    private void loadNotificationsFromFirebase() {
        databaseManager.getUserNotifications(userId, new FirebaseDatabaseManager.DataCallback<List<AppNotification>>() {
            @Override
            public void onSuccess(List<AppNotification> data) {
                notifications.clear();
                notifications.addAll(data);
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to load notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        AppNotification notif = notifications.get(position);

                        databaseManager.removeNotification(userId, notif.getId(), new FirebaseDatabaseManager.OperationCallback() {
                            @Override
                            public void onSuccess() {
                                notifications.remove(position);
                                notificationAdapter.notifyItemRemoved(position);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getContext(), "Failed to delete notification", Toast.LENGTH_SHORT).show();
                                notificationAdapter.notifyItemChanged(position);
                            }
                        });
                    }
                };

        new ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(notificationsRecyclerView);
    }
}
