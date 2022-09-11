package com.barmej.weatherforecasts.viewmodel;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.barmej.weatherforecasts.data.WeatherDataRepository;
import com.barmej.weatherforecasts.data.entity.ForecastLists;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;
import com.barmej.weatherforecasts.data.sync.SyncUtils;

/**
 * ViewModel class that hold data requests and temporary that survive configuration changes
 */
public class MainViewModel extends AndroidViewModel {

    /**
     * An instance of WeatherDataRepository for all data related operations
     */
    private WeatherDataRepository mRepository;

    /**
     * LiveData object to wrap WeatherInfo  data
     */
    private LiveData<WeatherInfo> mWeatherInfoLiveData;

    /**
     * Forecasts object to wrap Forecasts data
     */
    private LiveData<ForecastLists> mForecastListsLiveData;

    private Context mContext;

    private BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mWeatherInfoLiveData.observeForever(new Observer<WeatherInfo>() {
                @Override
                public void onChanged(WeatherInfo weatherInfo) {
                    if (weatherInfo == null){
                        SyncUtils.startSync(mContext);
                    }
                }
            });
        }
    };

    /**
     * ViewModel Constructor
     *
     * @param application An instance of application class
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MainViewModel(@NonNull Application application) {
        super(application);

        // Get instance of WeatherDataRepository
        mRepository = WeatherDataRepository.getInstance(getApplication());

        // Request weather info data from the repository class
        mWeatherInfoLiveData = mRepository.getWeatherInfo();

        // Request forecasts lists  from the repository class
        mForecastListsLiveData = mRepository.getForecastsInfo();

        mContext = application.getApplicationContext();
        IntentFilter connectivityFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mConnectivityReceiver , connectivityFilter);


    }

    /**
     * Get a handle of WeatherInfo LiveData object
     *
     * @return A wrapper LiveData object contains the weather info
     */
    public LiveData<WeatherInfo> getWeatherInfoLiveData() {
        return mWeatherInfoLiveData;
    }

    /**
     * Get a handle of ForecastsList LiveData object
     *
     * @return A wrapper LiveData object contains forecasts lists
     */
    public LiveData<ForecastLists> getForecastListsLiveData() {
        return mForecastListsLiveData;
    }

    @Override
    protected void onCleared() {
        // Cancel all ongoing requests
        mRepository.cancelDataRequests();
        mContext.unregisterReceiver(mConnectivityReceiver);
    }

}
