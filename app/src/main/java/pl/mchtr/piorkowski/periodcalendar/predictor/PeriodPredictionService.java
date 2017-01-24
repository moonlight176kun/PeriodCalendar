package pl.mchtr.piorkowski.periodcalendar.predictor;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import pl.mchtr.piorkowski.periodcalendar.period_days.PeriodDaysManager;
import pl.mchtr.piorkowski.periodcalendar.util.AppPreferences;

public class PeriodPredictionService extends IntentService {
    private static final String TAG = "PeriodPredictionService";

    private static final String ACTION_RECALCULATE_PERIOD_ON_DEMAND
            = AppPreferences.APPLICATION_PREFIX + "action.RECALCULATE_ON_DEMAND";

    public static final String ACTION_SCHEDULED_RECALCULATION
            = AppPreferences.APPLICATION_PREFIX + "action.SCHEDULED_RECALCULATION";

    private PeriodDaysManager periodDaysManager;
    private PeriodCalculator periodCalculator;

    public PeriodPredictionService() {
        super("PeriodPredictionService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        periodDaysManager = new PeriodDaysManager(getApplicationContext());
        periodCalculator = new PeriodCalculator(periodDaysManager);
    }

    public static void recalculateOnDemand(Context context) {
        Intent intent = new Intent(context, PeriodPredictionService.class);
        intent.setAction(ACTION_RECALCULATE_PERIOD_ON_DEMAND);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (action == null) {
                Log.e(TAG, "Null action! Intent object: " + intent.toString());
                return;
            }

            switch (action) {
                case ACTION_RECALCULATE_PERIOD_ON_DEMAND:
                    Log.i(TAG, "On demand recalcualtion");
                    periodCalculator.calculate();
                    break;
                case ACTION_SCHEDULED_RECALCULATION:
                    Log.i(TAG, "Scheduled recalculation!");
                    break;
                default:
                    throw new IllegalArgumentException("Unknown action name :\"" + action + "\"");
            }
        }
    }
}
