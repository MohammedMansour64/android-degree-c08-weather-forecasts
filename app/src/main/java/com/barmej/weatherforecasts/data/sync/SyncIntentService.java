package com.barmej.weatherforecasts.data.sync;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.barmej.weatherforecasts.data.WeatherDataRepository;

public class SyncIntentService extends IntentService {


    public SyncIntentService() {
        super(SyncIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        WeatherDataRepository repository = WeatherDataRepository.getInstance(this);
        repository.updateWeatherInfo();
        repository.updateForecastLists();
    }
}
