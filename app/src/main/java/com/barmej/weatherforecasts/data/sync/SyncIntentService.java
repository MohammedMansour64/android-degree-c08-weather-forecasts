package com.barmej.weatherforecasts.data.sync;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;

import com.barmej.weatherforecasts.data.WeatherDataRepository;
import com.barmej.weatherforecasts.data.entity.ForecastLists;
import com.barmej.weatherforecasts.utils.AppExecutor;
import com.barmej.weatherforecasts.utils.NotificationUtils;

public class SyncIntentService extends IntentService {


    public SyncIntentService() {
        super(SyncIntentService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = NotificationUtils.getSyncNotification(this);
        startForeground(NotificationUtils.SYNC_SERVICE_NOTIFICATION_ID , notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        WeatherDataRepository repository = WeatherDataRepository.getInstance(this);
        repository.updateWeatherInfo();
        repository.updateForecastLists();
        AppExecutor.getInstance().getMainThread().execute(new Runnable() {
            @Override
            public void run() {
                repository.getForecastsInfo().observeForever(new Observer<ForecastLists>() {
                    @Override
                    public void onChanged(ForecastLists forecastLists) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                stopSelf();
                            }
                        } , 5000);
                    }
                });
            }
        });
        return START_STICKY;
    }
}
