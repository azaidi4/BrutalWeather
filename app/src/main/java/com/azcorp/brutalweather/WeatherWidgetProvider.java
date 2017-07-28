package com.azcorp.brutalweather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;


/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidgetProvider extends AppWidgetProvider {

    public static String refresh = "Refresh";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(refresh))
            Log.i("onRecieve", "onReceive: IT WORKSSS");

        super.onReceive(context, intent);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = buildUpdate(context);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
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

        Intent intent = new Intent(context, WeatherWidgetProvider.class);
        intent.setAction(refresh);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        views.setTextViewText(R.id.appwidget_text, phrase);
        views.setOnClickPendingIntent(R.id.appwidget_refresh, pendingIntent);

        return views;
    }
}

