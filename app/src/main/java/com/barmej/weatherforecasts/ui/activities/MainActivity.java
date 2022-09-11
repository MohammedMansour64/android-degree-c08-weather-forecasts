package com.barmej.weatherforecasts.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.barmej.weatherforecasts.R;
import com.barmej.weatherforecasts.data.entity.ForecastLists;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;
import com.barmej.weatherforecasts.ui.adapters.DaysForecastAdapter;
import com.barmej.weatherforecasts.ui.adapters.HoursForecastAdapter;
import com.barmej.weatherforecasts.ui.fragments.PrimaryWeatherInfoFragment;
import com.barmej.weatherforecasts.ui.fragments.SecondaryWeatherInfoFragment;
import com.barmej.weatherforecasts.utils.CustomDateUtils;
import com.barmej.weatherforecasts.utils.NotificationUtils;
import com.barmej.weatherforecasts.utils.SharedPreferencesHelper;
import com.barmej.weatherforecasts.viewmodel.MainViewModel;
import com.google.android.material.tabs.TabLayout;

/**
 * MainActivity that show current weather info, next hours & days forecasts
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * FragmentManager to be used in ViewPager FragmentAdapter
     */
    private FragmentManager mFragmentManager;

    /**
     * Container of Weather fragments ViewPager and TabLayout
     */
    private FrameLayout mHeaderLayout;

    /**
     * ViewPage & FragmentPagerAdapter to show the HeaderFragments
     */
    private HeaderFragmentAdapter mHeaderFragmentAdapter;
    private ViewPager mViewPager;

    /**
     * RecyclerViews & it's Adapters to show forecasts lists
     */
    private HoursForecastAdapter mHoursForecastAdapter;
    private DaysForecastAdapter mDaysForecastsAdapter;

    private RecyclerView mHoursForecastsRecyclerView;
    private RecyclerView mDaysForecastRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationUtils.createWeatherStatusNotificationChannel(this);

        // Get an instance of FragmentManager and assign it to mFragmentManager variable
        mFragmentManager = getSupportFragmentManager();

        // Get a handle on header FrameLayout
        mHeaderLayout = findViewById(R.id.header);

        // Get the ViewPager object
        mViewPager = findViewById(R.id.pager);

        // Create and attach HeaderFragmentAdapter to the ViewPager
        mHeaderFragmentAdapter = new HeaderFragmentAdapter(mFragmentManager);
        mViewPager.setAdapter(mHeaderFragmentAdapter);

        // Setup the TableLayout with the ViewPager
        TabLayout tabLayout = findViewById(R.id.indicator);
        tabLayout.setupWithViewPager(mViewPager, true);

        // Create new HoursForecastAdapter and set it to RecyclerView
        mHoursForecastAdapter = new HoursForecastAdapter(this);
        mHoursForecastsRecyclerView = findViewById(R.id.rv_hours_forecast);
        mHoursForecastsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mHoursForecastsRecyclerView.setAdapter(mHoursForecastAdapter);

        // Create new DaysForecastAdapter and set it to RecyclerView
        mDaysForecastsAdapter = new DaysForecastAdapter(this);
        mDaysForecastRecyclerView = findViewById(R.id.rv_days_forecast);
        mDaysForecastRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDaysForecastRecyclerView.setAdapter(mDaysForecastsAdapter);

        // Hide empty views until data become available to display
        mHeaderLayout.setVisibility(View.INVISIBLE);
        mHoursForecastsRecyclerView.setVisibility(View.INVISIBLE);
        mDaysForecastRecyclerView.setVisibility(View.INVISIBLE);

        // Request current weather data
        requestWeatherInfo();

        // Request forecasts data
        requestForecastsInfo();

        // Update window background based on hour of the day
        changeWindowBackground();

    }

    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater
        MenuInflater inflater = getMenuInflater();
        // Use the inflater's inflate method to inflate main_menu layout
        inflater.inflate(R.menu.main_menu, menu);
        // Return true to display the menu
        return true;
    }

    /**
     * Callback invoked when a menu item from this Activity's menu get selected.
     *
     * @param item The menu item that was selected by the user
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // Open SettingsActivity
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Request current weather data
     */
    private void requestWeatherInfo() {
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getWeatherInfoLiveData().observe(this, new Observer<WeatherInfo>() {
            @Override
            public void onChanged(WeatherInfo weatherInfo) {
                if (weatherInfo != null) {
                    mHeaderLayout.setVisibility(View.VISIBLE);
                    updateSunriseAndSunsetTimes(weatherInfo);
                    changeWindowBackground();
                }
            }
        });
    }

    /**
     * Request forecasts data
     */
    private void requestForecastsInfo() {
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getForecastListsLiveData().observe(this, new Observer<ForecastLists>() {
            @Override
            public void onChanged(ForecastLists forecastLists) {
                if (forecastLists != null) {
                    mHoursForecastAdapter.updateData(forecastLists.getHoursForecasts());
                    mDaysForecastsAdapter.updateData(forecastLists.getDaysForecasts());
                    mHoursForecastsRecyclerView.setVisibility(View.VISIBLE);
                    mDaysForecastRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    /**
     * Update sunrise hour and sunset hour saved in the SharedPreferences
     *
     * @param weatherInfo current weather info
     */
    private void updateSunriseAndSunsetTimes(WeatherInfo weatherInfo) {
        int sunriseHour = CustomDateUtils.getHourOfDayAsInteger(weatherInfo.getSys().getSunrise());
        SharedPreferencesHelper.setSunriseHour(MainActivity.this, sunriseHour);
        int sunsetHour = CustomDateUtils.getHourOfDayAsInteger(weatherInfo.getSys().getSunset());
        SharedPreferencesHelper.setSunsetHour(MainActivity.this, sunsetHour);
    }

    /**
     * Change window background depending on current hour in the day
     * The hour will be used to determine if it's morning, afternoon or evening
     */
    private void changeWindowBackground() {
        int sunriseHour = SharedPreferencesHelper.getSunriseHour(this);
        int sunsetHour = SharedPreferencesHelper.getSunsetHour(this);
        long middayHour = sunriseHour + (sunsetHour - sunriseHour) / 2;
        long currentHour = CustomDateUtils.getHourOfDayAsInteger(System.currentTimeMillis() / 1000);
        if (currentHour >= sunriseHour && currentHour < middayHour) {
            getWindow().getDecorView().setBackgroundResource(R.drawable.shape_main_morning_background);
        } else if (currentHour >= middayHour && currentHour <= sunsetHour) {
            getWindow().getDecorView().setBackgroundResource(R.drawable.shape_main_afternoon_background);
        } else {
            getWindow().getDecorView().setBackgroundResource(R.drawable.shape_main_evening_background);
        }
    }

    /**
     * FragmentPagerAdapter class to create and manage header fragments for the ViewPager
     */
    class HeaderFragmentAdapter extends FragmentPagerAdapter {

        HeaderFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new PrimaryWeatherInfoFragment();
                case 1:
                    return new SecondaryWeatherInfoFragment();
            }
            return null;
        }

    }


}
