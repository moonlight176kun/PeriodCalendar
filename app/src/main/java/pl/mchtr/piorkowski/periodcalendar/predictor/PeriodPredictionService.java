package pl.mchtr.piorkowski.periodcalendar.predictor;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.common.base.Optional;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import pl.mchtr.piorkowski.periodcalendar.period_days.PeriodDaysBean;
import pl.mchtr.piorkowski.periodcalendar.period_days.PeriodDaysManager;
import pl.mchtr.piorkowski.periodcalendar.util.AppPreferences;

public class PeriodPredictionService extends IntentService {
    private static final String TAG = "PeriodPredictionService";

    private static final String ACTION_RECALCULATE_PERIOD_ON_DEMAND
            = AppPreferences.APPLICATION_PREFIX + "action.RECALCULATE_ON_DEMAND";

    public static final String ACTION_SCHEDULED_RECALCULATION
            = AppPreferences.APPLICATION_PREFIX + "action.SCHEDULED_RECALCULATION";

    private final PeriodDaysManager periodDaysManager;

    public PeriodPredictionService() {
        super("PeriodPredictionService");
        this.periodDaysManager = new PeriodDaysManager(this);
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
                    handleOnDemandRecalculation();
                    break;
                case ACTION_SCHEDULED_RECALCULATION:
                    Log.i(TAG, "Scheduled recalculation!");
                    break;
                default:
                    throw new IllegalArgumentException("Unknown action name :\"" + action + "\"");
            }
        }
    }

    private void handleOnDemandRecalculation() {
        Optional<PeriodDaysBean> beanOpt = periodDaysManager.getLastPeriodDaysBean();
        if (!beanOpt.isPresent()) {
            PeriodDaysBean bean = recalculateNewPeriodDaysFromSettings();
            periodDaysManager.addNewPeriodDaysBean(bean);
            return;
        }

        PeriodDaysBean lastPeriodDays = beanOpt.get();
        LocalDate now = new LocalDate();

        if (now.isAfter(lastPeriodDays.getLatestDate())) {
            LocalDate newLastPeriodDate = lastPeriodDays.getPeriodDays().get(0);
            PeriodDaysBean bean = recalculateNewPeriodDays(newLastPeriodDate);
            periodDaysManager.updateLastPeriodDate(newLastPeriodDate);
            periodDaysManager.addNewPeriodDaysBean(bean);
        } else {
            PeriodDaysBean bean = recalculateNewPeriodDaysFromSettings();
            periodDaysManager.updateNextPeriodDaysBean(bean);
        }
    }

    private PeriodDaysBean recalculateNewPeriodDaysFromSettings() {
        Optional<LocalDate> lastPeriodDateOpt = periodDaysManager.getLastPeriodDate();
        if (!lastPeriodDateOpt.isPresent()) {
            Log.e(TAG, "Recalculation on demand and no last period date found!");
            throw new IllegalStateException("Lack of last period date!");
        }

        return recalculateNewPeriodDays(lastPeriodDateOpt.get());
    }

    private PeriodDaysBean recalculateNewPeriodDays(LocalDate lastFirstPeriodDay) {
        List<LocalDate> nextPeriodDays = getNextPeriodDays(lastFirstPeriodDay);
        List<LocalDate> nextFertileDays = getNextFertileDays(nextPeriodDays.get(0));
        LocalDate nextOvulationDay = getNextOvulationDay(nextFertileDays);
        return new PeriodDaysBean(nextPeriodDays, nextFertileDays, nextOvulationDay);
    }

    private List<LocalDate> getNextPeriodDays(LocalDate lastPeriodDay) {
        List<LocalDate> periodDays = new ArrayList<>();
        LocalDate periodDay = lastPeriodDay.plusDays(periodDaysManager.getPeriodLength());
        periodDays.add(periodDay);

        for (int i = 0; i < periodDaysManager.getMenstruationLength() - 1; ++i) {
            periodDay = periodDay.plusDays(1);
            periodDays.add(periodDay);
        }

        return periodDays;
    }

    private List<LocalDate> getNextFertileDays(LocalDate nextPeriodDate) {
        int diff = periodDaysManager.getPeriodLength() - 20;
        LocalDate fertileDay = nextPeriodDate.plusDays(diff);
        List<LocalDate> fertileDays = new ArrayList<>();
        fertileDays.add(fertileDay);

        for (int i = 0; i < 6; ++i) {
            fertileDay = fertileDay.plusDays(1);
            fertileDays.add(fertileDay);
        }

        return fertileDays;
    }

    private LocalDate getNextOvulationDay(List<LocalDate> fertileDays) {
        int size = fertileDays.size();
        if (size < 2) {
            return fertileDays.get(0);
        } else {
            return fertileDays.get(size - 2);
        }
    }
}
