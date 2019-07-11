package com.eywa_kitchen.eywarecommendationservice;
import android.accessibilityservice.AccessibilityService;
import android.app.NotificationManager;
import android.content.Context;
import android.view.accessibility.AccessibilityEvent;

import com.eywa_kitchen.eywarecommendationservice.RecommendationsGetTask.GetRecommendationsList;
import com.eywa_kitchen.eywarecommendationservice.RecommendationsGetTask.RecommendationDetail;
import com.eywa_kitchen.eywarecommendationservice.RecommendationsGetTask.RecommendationsCallback;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;
import static xdroid.toaster.Toaster.toast;

public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "RecAccess";
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        getRecommendations();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    private void getRecommendations(){
        final GetRecommendationsList getRecommendations = new GetRecommendationsList(new RecommendationsCallback() {
            @Override
            public void Received(List<RecommendationDetail> RecommendationsList) {
                RecController.pushRecommendations(RecommendationsList, notificationManager, getApplicationContext());
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getRecommendations();
                    }
                    }, 1800000);
            }

            @Override
            public void Error() {
                getRecommendations();
            }
        });
        getRecommendations.execute();
    }

}
