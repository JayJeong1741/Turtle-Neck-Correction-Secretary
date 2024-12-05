package com.example.ui.ui.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.ui.R;

public class NotificationWorker extends Worker {

    private static final String CHANNEL_ID = "posture_notification_channel";
    private static final String CHANNEL_NAME = "Posture Notifications";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String title = getInputData().getString("title");
        String message = getInputData().getString("message");

        Log.d("NotificationWorker", "Received data: " + title + " - " + message);

        if (title != null && message != null) {
            sendNotification(title, message);
        } else {
            Log.e("NotificationWorker", "Title or message is null");
        }

        return Result.success();
    }

    private void sendNotification(String title, String message) {
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            notificationManager.notify(1, builder.build());
        } else {
            Log.e("NotificationWorker", "NotificationManager is null");
        }
    }
}
