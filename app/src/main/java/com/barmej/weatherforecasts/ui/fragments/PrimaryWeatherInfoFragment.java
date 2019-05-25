package com.barmej.weatherforecasts.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.barmej.weatherforecasts.R;
import com.barmej.weatherforecasts.databinding.FragmentPrimaryWeatherInfoBinding;
import com.barmej.weatherforecasts.viewmodel.MainViewModel;

/**
 * A fragment that show primary weather information like high and low temperatures, weather icon,
 * weather description, today's date and current city.
 * You can create an instance of this fragment and embed or add to other activity or fragment!
 */
public class PrimaryWeatherInfoFragment extends Fragment {

    /**
     * An instance of auto generated data binding class
     */
    private FragmentPrimaryWeatherInfoBinding mBinding;

    /**
     * Required empty public constructor
     */
    public PrimaryWeatherInfoFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout using data binding class
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_primary_weather_info, container, false);

        // Specify the current fragment as the lifecycle owner.
        mBinding.setLifecycleOwner(this);

        // Return the inflated view object
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentActivity activity = getActivity();
        if (activity != null) {
            // Get a handle on the MainViewModel of the host Activity
            MainViewModel mainViewModel = ViewModelProviders.of(activity).get(MainViewModel.class);
            // Add LiveData object to data binding observe changes automatically and update UI
            mBinding.setWeatherInfo(mainViewModel.getWeatherInfoLiveData());
        }

    }


}
