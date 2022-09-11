package com.barmej.weatherforecasts.data.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.barmej.weatherforecasts.data.WeatherDataRepository;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SyncJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        WeatherDataRepository repository = WeatherDataRepository.getInstance(this);
        repository.updateWeatherInfo();
        repository.updateForecastLists();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
