package pl.mchtr.piorkowski.periodcalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AfterBootReceiver extends BroadcastReceiver {

    PeriodRecalculateReceiver periodRecalculateReceiver = new PeriodRecalculateReceiver();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            periodRecalculateReceiver.setPredictionService(context);
        }
    }
}
