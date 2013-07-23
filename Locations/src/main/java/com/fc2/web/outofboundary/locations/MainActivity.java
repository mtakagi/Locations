package com.fc2.web.outofboundary.locations;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.DetectedActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private ActivityRecognitionClient mClient;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mClient = new ActivityRecognitionClient(this, this, this);
        mClient.connect();
        mListView = (ListView) findViewById(R.id.list_view);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClient.unregisterConnectionCallbacks(this);
        mClient.unregisterConnectionFailedListener(this);
        mClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Intent intent = new Intent(this, ActivityIntentService.class);
        PendingIntent callbackIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mClient.requestActivityUpdates(300000, callbackIntent);
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        long current = System.currentTimeMillis();
        long start = current - 3600000;
        return new CursorLoader(this, LocationProvider.ACTIVITY_URI, null, null, null, "time DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> objectLoader, Cursor cursor) {
        mListView.setAdapter(new LocationCursorAdapter(this, cursor, false));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> objectLoader) {

    }

    private static class LocationCursorAdapter extends CursorAdapter {

        private static final DateFormat sFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPAN);

        public LocationCursorAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return View.inflate(context, android.R.layout.simple_list_item_2, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView view1 = (TextView) view.findViewById(android.R.id.text1);
            int typeColumn = cursor.getColumnIndexOrThrow("type");
            int type = cursor.getInt(typeColumn);

            switch (type) {
                case DetectedActivity.IN_VEHICLE:
                    view1.setText("In vehicle");
                    break;
                case DetectedActivity.ON_BICYCLE:
                    view1.setText("On Bicycle");
                    break;
                case DetectedActivity.ON_FOOT:
                    view1.setText("On foot");
                    break;
                case DetectedActivity.STILL:
                    view1.setText("Still");
                    break;
                case DetectedActivity.TILTING:
                    view1.setText("Tilting");
                    break;
                case DetectedActivity.UNKNOWN:
                    view1.setText("Unknown");
                    break;
            }

            TextView view2 = (TextView) view.findViewById(android.R.id.text2);
            int timeColumn = cursor.getColumnIndexOrThrow("time");
            long time = cursor.getLong(timeColumn);
            Date date = new Date(time);

            view2.setText(sFormat.format(date));
        }
    }
}
