package com.example.android.sunshine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class SunshineWatchFace extends CanvasWatchFaceService {

    private static final long INTERACTIVE_UPDATE_RATE_MS = 1000;
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {

        private final WeakReference<SunshineWatchFace.Engine> mWeakReference;

        EngineHandler(SunshineWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            SunshineWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine implements
            DataApi.DataListener,
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        private static final String TAG = "Engine";
        final Handler mUpdateTimeHandler = new EngineHandler(this);

        private GoogleApiClient mGoogleApiClient;

        private String maxTempString = " ";
        private String minTempString = " ";
        private Bitmap image;

        boolean mRegisteredTimeZoneReceiver = false;

        // Grid option for design purpose
        private static final boolean SHOW_GRID = false;

        // Paints for watch face
        Paint mBackgroundPaint;
        Paint mPrimaryTextPaint;
        Paint mSecondaryTextPaint;

        Calendar mCalendar;

        // For occupying 70% of actual screen
        Rect innerRect;

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };

        boolean mAmbient;
        // When true, we disable anti-aliasing in ambient mode.
        boolean mLowBitAmbient;

        @SuppressWarnings("deprecation")
        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(
                    new WatchFaceStyle.Builder(SunshineWatchFace.this)
                            .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                            .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                            .setShowSystemUiTime(false)
                            .setAcceptsTapEvents(true)
                            .build());

            Resources resources = SunshineWatchFace.this.getResources();

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(resources.getColor(R.color.background));

            // Initialize paints
            mPrimaryTextPaint = createTextPaint(resources.getColor(R.color.primary_text));
            mSecondaryTextPaint = createTextPaint(resources.getColor(R.color.secondary_text));

            mCalendar = Calendar.getInstance();

            mGoogleApiClient = new GoogleApiClient.Builder(SunshineWatchFace.this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            mGoogleApiClient.connect();
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
            super.onDestroy();
        }

        private Paint createTextPaint(int textColor) {
            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setAntiAlias(true);
            return paint;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                registerReceiver();
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            } else {
                unregisterReceiver();
            }
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            SunshineWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            SunshineWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                if (mLowBitAmbient) {
                    mPrimaryTextPaint.setAntiAlias(!inAmbientMode);
                }
                invalidate();
            }
            updateTimer();
        }

        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            switch (tapType) {
                case TAP_TYPE_TOUCH:
                    // The user has started touching the screen.
                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    // The user has started a different gesture or otherwise cancelled the tap.
                    break;
                case TAP_TYPE_TAP:
                    // The user has completed the tap gesture.
                    Toast.makeText(getApplicationContext(), R.string.message, Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            // Initialize the time
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);

            // Draw the background.
            if (isInAmbientMode()) {
                canvas.drawColor(Color.BLACK);
            } else {
                canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
            }

            // Occupy center 70% of the screen
            // Calculate the size of sides of inner Rectangle
            int innerRectLeft = Double.valueOf(bounds.left * 0.7).intValue();
            int innerRectTop = Double.valueOf(bounds.top * 0.7).intValue();
            int innerRectRight = Double.valueOf(bounds.right * 0.7).intValue();
            int innerRectBottom = Double.valueOf(bounds.bottom * 0.7).intValue();

            // This is offset for drawing on canvas
            int offset = (bounds.width() - (innerRectBottom - innerRectTop)) / 2;

            innerRect = new Rect(
                    innerRectLeft + offset,
                    innerRectTop + offset,
                    innerRectRight + offset,
                    innerRectBottom + offset);

            int oneUnitWidth = Double.valueOf(innerRect.width() / 16).intValue();
            int oneUnitHeight = Double.valueOf(innerRect.height() / 16).intValue();


            if (SHOW_GRID) {

                // Inner Rectangle, Only for debug
                canvas.drawRect(innerRect, createTextPaint(Color.parseColor("grey")));

                // Grid
                for (int i = 0; i < 17; i++) {
                    canvas.drawLine(
                            innerRect.left, innerRect.top + i * oneUnitHeight,
                            innerRect.right, innerRect.top + i * oneUnitHeight,
                            createTextPaint(Color.argb(125, 125, 125, 125)));
                }
                for (int i = 0; i < 17; i++) {
                    canvas.drawLine(
                            innerRect.left + i * oneUnitWidth, innerRect.top,
                            innerRect.left + i * oneUnitWidth, innerRect.bottom,
                            createTextPaint(Color.argb(125, 125, 125, 125)));
                }
            }

            // Draw Time
            String timeText = String.format(Locale.ENGLISH, "%d:%02d",
                    mCalendar.get(Calendar.HOUR_OF_DAY),
                    mCalendar.get(Calendar.MINUTE));

            mPrimaryTextPaint.setTextSize(oneUnitHeight * 5);
            float textWidth = mPrimaryTextPaint.measureText(timeText);
            canvas.drawText(timeText,
                    innerRect.left + (innerRect.width() - textWidth) / 2,
                    innerRect.top + oneUnitHeight * 5,
                    mPrimaryTextPaint);

            // The horizontal line in center
            // Width 32, height 1
            canvas.drawRect(new Rect(innerRect.centerX() - 16,
                            innerRect.top + Double.valueOf(9.5 * oneUnitHeight).intValue(),
                            innerRect.centerX() + 16,
                            innerRect.top + Double.valueOf(9.5 * oneUnitHeight).intValue() + 1),
                    mSecondaryTextPaint);

            // Draw date
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.ENGLISH);
            String dateText = dateFormat.format(mCalendar.getTime());

            mSecondaryTextPaint.setTextSize(oneUnitHeight * 2);
            float dateTextWidth = mSecondaryTextPaint.measureText(dateText);
            canvas.drawText(dateText,
                    innerRect.left + (innerRect.width() - dateTextWidth) / 2,
                    innerRect.top + oneUnitHeight * 8,
                    mSecondaryTextPaint);

            // Draw Temperature
            mPrimaryTextPaint.setTextSize(oneUnitHeight * 2);
            canvas.drawText(maxTempString,
                    innerRect.left + oneUnitWidth * 7,
                    innerRect.bottom - oneUnitHeight * 3,
                    mPrimaryTextPaint);

            mSecondaryTextPaint.setTextSize(oneUnitHeight * 2);
            canvas.drawText(minTempString,
                    innerRect.left + oneUnitWidth * 11,
                    innerRect.top + oneUnitHeight * 13,
                    mSecondaryTextPaint);

            // Draw Bitmap
            if (image != null) {
                Rect imageRect = new Rect(
                        innerRect.left + 2 * oneUnitWidth,// left + 2
                        innerRect.bottom - Double.valueOf(5.5 * oneUnitHeight).intValue(), //
                        innerRect.left + 6 * oneUnitWidth, //
                        innerRect.bottom - Double.valueOf(1.5 * oneUnitHeight).intValue() //
                );
                canvas.drawBitmap(image, null, imageRect, new Paint());
            }
        }

        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Wearable.DataApi.addListener(mGoogleApiClient, this);
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "onConnectionSuspended: ");
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d(TAG, "onConnectionFailed: ");
        }

        @Override
        public void onDataChanged(DataEventBuffer dataEvents) {
            for (DataEvent event : dataEvents) {
                if (event.getType() == DataEvent.TYPE_CHANGED) {
                    // DataItem changed
                    DataItem item = event.getDataItem();
                    if (item.getUri().getPath().compareTo("/weather") == 0) {
                        DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                        double maxTemp = dataMap.getDouble("max");
                        double minTemp = dataMap.getDouble("min");

                        maxTempString = String.format(getString(R.string.format_temperature), maxTemp);
                        minTempString = String.format(getString(R.string.format_temperature), minTemp);
                        invalidate();

                        final Asset profileAsset = dataMap.getAsset("image");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                image = loadBitmapFromAsset(profileAsset);
                            }
                        }).start();
                        Log.d(TAG, "onDataChanged: rk");
                    }
                }
            }
        }

        Bitmap loadBitmapFromAsset(Asset asset) {
            if (asset == null) {
                throw new IllegalArgumentException("Asset must be non-null");
            }
            ConnectionResult result =
                    mGoogleApiClient.blockingConnect(500, TimeUnit.MILLISECONDS);
            if (!result.isSuccess()) {
                return null;
            }
            // convert asset into a file descriptor and block until it's ready
            InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                    mGoogleApiClient, asset).await().getInputStream();
            mGoogleApiClient.disconnect();

            if (assetInputStream == null) {
                Log.w(TAG, "Requested an unknown Asset.");
                return null;
            }
            // decode the stream into a bitmap
            return BitmapFactory.decodeStream(assetInputStream);
        }
    }
}
