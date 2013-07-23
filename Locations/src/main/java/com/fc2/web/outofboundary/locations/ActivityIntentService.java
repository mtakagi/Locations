package com.fc2.web.outofboundary.locations;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by mtakagi on 13/07/21.
 */
public class ActivityIntentService extends IntentService {

    public static final String ACTIVITY_UPDATE_ACTION = "ACTIVITY_UPDATE_ACTION";

    public ActivityIntentService() {
        super("ActivityIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity detected = result.getMostProbableActivity();
//            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
//            intent.setAction(ACTIVITY_UPDATE_ACTION);
//            broadcastManager.sendBroadcast(intent);
            Uri uri = Uri.parse("content://" + LocationProvider.AUTHORITY + "/activity");
            ContentValues values = new ContentValues();
            values.put("type", detected.getType());
            values.put("confidence", detected.getConfidence());
            values.put("time", result.getTime());
            getContentResolver().insert(uri, values);
        }
    }
}
