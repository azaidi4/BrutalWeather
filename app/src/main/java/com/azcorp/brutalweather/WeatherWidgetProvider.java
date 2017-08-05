package com.azcorp.brutalweather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.Toast;


/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager app = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, WeatherWidgetProvider.class);
            int[] appWidgetIDs = app.getAppWidgetIds(thisWidget);


            onUpdate(context, app, appWidgetIDs);
            Toast.makeText(context, "Refreshing...", Toast.LENGTH_SHORT).show();
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = buildUpdate(context);
        ComponentName thisWidget = new ComponentName(context, WeatherWidgetProvider.class);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(thisWidget, views);
    }

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

    public static RemoteViews buildUpdate(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget_provider);

        SharedPreferences prefs = context.getSharedPreferences("myprefs", Context.MODE_PRIVATE);
        CharSequence phrase = prefs.getString("phrase", null);
        CharSequence temperature = prefs.getString("temperature", null);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int color = sharedPref.getInt("pref_color", 000000);

        //Intent for refreshing weather widget attributes
        Intent refreshIntent = new Intent(context, WeatherWidgetProvider.class);
        refreshIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget_refresh, refreshPendingIntent);

        //Intent for launching widget settings
        Intent settingIntent = new Intent(context, SettingsActivity.class);
        PendingIntent settingPendingIntent = PendingIntent.getActivity(context, 0, settingIntent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_text, settingPendingIntent);

        //Intent for launching Application
        Intent mainAppIntent = new Intent(context, MainActivity.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, mainAppIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget_temp, mainPendingIntent);

        views.setTextViewText(R.id.appwidget_text, phrase);
        views.setTextViewText(R.id.appwidget_temp, temperature);
        views.setInt(R.id.appwidget_layout, "setBackgroundColor",  color);

        return views;
    }
}

