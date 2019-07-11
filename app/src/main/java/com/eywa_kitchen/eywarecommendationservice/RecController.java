package com.eywa_kitchen.eywarecommendationservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.eywa_kitchen.eywarecommendationservice.RecommendationsGetTask.RecommendationDetail;

import java.util.List;

import static android.content.ContentValues.TAG;
import static android.support.v4.app.NotificationCompat.PRIORITY_HIGH;

class RecController {
    private static final String CHANNEL_ID = "CHANNEL_ID";

    static void pushRecommendations(List<RecommendationDetail> RecommendationsList, NotificationManager notificationManager, Context context){

        notificationManager.cancelAll();
        for(int VideoNumber = 0;VideoNumber<RecommendationsList.size();VideoNumber++) {

            Intent intent = new Intent(context.getApplicationContext(), MyAccessibilityService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + RecommendationsList.get(VideoNumber).VideoId));
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                            .setAutoCancel(false)
                            .setSmallIcon(R.drawable.eywa)
                            .setWhen(System.currentTimeMillis())
                            .setContentIntent(pendingIntent)
                            .setContentTitle("YouTube")
                            .setContentText(RecommendationsList.get(VideoNumber).author)
                            .setPriority(PRIORITY_HIGH)
                            .setVisibility(Notification.VISIBILITY_PUBLIC)
                            .setContentTitle(RecommendationsList.get(VideoNumber).title)
                            .setContentInfo("YouTube")
                            .setContentIntent(resultPendingIntent)
                            .setGroup("Area")
                            .setLargeIcon(RecommendationsList.get(VideoNumber).preview)
                            .setCategory(Notification.CATEGORY_RECOMMENDATION);
            notificationManager.notify(VideoNumber,notificationBuilder.build());
            Log.e(TAG, " NOTIFIED");
        }

    }
}
