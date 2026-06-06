package com.swart.passiveinkomen;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class IncomeWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            updateWidget(context, appWidgetManager, id);
        }
    }

    static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(WidgetDataPlugin.PREFS, Context.MODE_PRIVATE);
        double todayEarned = Double.parseDouble(prefs.getString("today_earned", "0.0"));
        double perSecond = Double.parseDouble(prefs.getString("per_second", "0.0"));

        String todayStr = String.format("€%.4f", todayEarned);
        String perSecStr = String.format("€%.6f/s", perSecond);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.income_widget);
        views.setTextViewText(R.id.widget_today, todayStr);
        views.setTextViewText(R.id.widget_per_sec, perSecStr);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static void updateAllWidgets(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context, IncomeWidget.class));
        new IncomeWidget().onUpdate(context, manager, ids);
    }
}
