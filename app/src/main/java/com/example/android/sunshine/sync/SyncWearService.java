package com.example.android.sunshine.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.sunshine.MainActivity;
import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;

import static com.example.android.sunshine.MainActivity.MAIN_FORECAST_PROJECTION;

public class SyncWearService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SyncWearService";

    GoogleApiClient mGoogleApiClient;

    public SyncWearService() {
        super("SyncWearService");
    }

    public static void startActionSyncWear(Context context) {
        Intent intent = new Intent(context, SyncWearService.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Get current weather values
        Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

        Cursor cursor = getContentResolver().query(
                forecastQueryUri,
                MAIN_FORECAST_PROJECTION,
                selection,
                null,
                sortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            int weatherId = cursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
            int weatherImageId = SunshineWeatherUtils
                    .getSmallArtResourceIdForWeatherCondition(weatherId);
            double maxInCelsius = cursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
            double minInCelsius = cursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);

            cursor.close();

            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/weather");
            putDataMapReq.getDataMap().putDouble("max", maxInCelsius);
            putDataMapReq.getDataMap().putDouble("min", minInCelsius);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), weatherImageId);
            Asset asset = createAssetFromBitmap(bitmap);
            putDataMapReq.getDataMap().putAsset("image", asset);

            // Put time to change the existing DataMap in wear
            putDataMapReq.getDataMap().putLong("time", System.currentTimeMillis());

            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult =
                    Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
        }
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended: " + cause);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.d(TAG, "onConnectionFailed: " + result);
    }
}
