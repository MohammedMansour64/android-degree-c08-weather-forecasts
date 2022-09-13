package com.barmej.weatherforecasts;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.barmej.weatherforecasts.data.WeatherDataRepository;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;
import com.barmej.weatherforecasts.ui.activities.MainActivity;

/**
 * Implementation of App Widget functionality.
 */

   //TODO: DISPLAY MORE DATA IF WIDGET SIZE HAS CHANGED BY MAKING DIFFERENT LAYOUTS FOR DIFFERENT SIZES.
    //TODO: ALSO CHANGE THE WIDGET DISPLAY IMAGE.
public class WeatherWidgetProvider extends AppWidgetProvider {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget_small);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        WeatherDataRepository repository = WeatherDataRepository.getInstance(context);
        LiveData<WeatherInfo> weatherInfoLiveData = repository.getWeatherInfo();
        weatherInfoLiveData.observeForever(new Observer<WeatherInfo>() {
            @Override
            public void onChanged(WeatherInfo weatherInfo) {
                String temperature = context.getString(R.string.format_temperature , weatherInfo.getMain().getTemp());
                views.setTextViewText(R.id.appwidget_text, temperature);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        });

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent , 0);
        views.setOnClickPendingIntent(R.id.appwidget_text , pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}