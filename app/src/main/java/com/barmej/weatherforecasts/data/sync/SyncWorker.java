package com.barmej.weatherforecasts.data.sync;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.barmej.weatherforecasts.data.WeatherDataRepository;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;
import com.barmej.weatherforecasts.ui.activities.MainActivity;
import com.barmej.weatherforecasts.utils.AppExecutor;
import com.barmej.weatherforecasts.utils.NotificationUtils;

public class SyncWorker extends Worker {
    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public Result doWork() {
        WeatherDataRepository repository = WeatherDataRepository.getInstance(getApplicationContext());
        repository.updateForecastLists();
        repository.updateWeatherInfo();
        AppExecutor.getInstance().getMainThread().execute(new Runnable() {
            @Override
            public void run() {
                repository.getWeatherInfo().observeForever(new Observer<WeatherInfo>() {
                    @Override
                    public void onChanged(WeatherInfo weatherInfo) {
                        NotificationUtils.showWeatherStatusNotification(getApplicationContext() , weatherInfo);

                    }
                });
            }
        });
        return Result.success();
    }
}
