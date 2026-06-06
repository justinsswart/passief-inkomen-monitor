package com.swart.passiveinkomen;

import android.content.Context;
import android.content.SharedPreferences;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "WidgetData")
public class WidgetDataPlugin extends Plugin {
    static final String PREFS = "income_widget_prefs";

    @PluginMethod
    public void update(PluginCall call) {
        double todayEarned = call.getDouble("todayEarned", 0.0);
        double perSecond = call.getDouble("perSecond", 0.0);

        Context ctx = getContext();
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit()
            .putString("today_earned", String.valueOf(todayEarned))
            .putString("per_second", String.valueOf(perSecond))
            .apply();

        IncomeWidget.updateAllWidgets(ctx);
        call.resolve();
    }
}
