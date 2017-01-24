package pl.mchtr.piorkowski.periodcalendar.predictor;

import android.util.Log;

import com.google.common.base.Optional;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import pl.mchtr.piorkowski.periodcalendar.period_days.PeriodDaysBean;
import pl.mchtr.piorkowski.periodcalendar.period_days.PeriodDaysManager;

public class PeriodCalculator {

    private static final String TAG = "PeriodCalculator";

    private final PeriodDaysManager manager;

    public PeriodCalculator(PeriodDaysManager manager) {
        this.manager = manager;
    }

    public void calculate() {
        Optional<PeriodDaysBean> beanOpt = manager.getLastPeriodDaysBean();
        if (!beanOpt.isPresent()) {
            PeriodDaysBean bean = recalculateNewPeriodDaysFromSettings();
            manager.addNewPeriodDaysBean(bean);
            return;
        }

        PeriodDaysBean lastPeriodDays = beanOpt.get();
        LocalDate now = new LocalDate();

        if (now.isAfter(lastPeriodDays.getLatestDate())) {
            LocalDate newLastPeriodDate = lastPeriodDays.getPeriodDays().get(0);
            PeriodDaysBean bean = recalculateNewPeriodDays(newLastPeriodDate);
            manager.updateLastPeriodDate(newLastPeriodDate);
            manager.addNewPeriodDaysBean(bean);
        } else {
            PeriodDaysBean bean = recalculateNewPeriodDaysFromSettings();
            manager.updateNextPeriodDaysBean(bean);
        }
    }

    private PeriodDaysBean recalculateNewPeriodDaysFromSettings() {
        Optional<LocalDate> lastPeriodDateOpt = manager.getLastPeriodDate();
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
        LocalDate periodDay = lastPeriodDay.plusDays(manager.getPeriodLength());
        periodDays.add(periodDay);

        for (int i = 0; i < manager.getMenstruationLength() - 1; ++i) {
            periodDay = periodDay.plusDays(1);
            periodDays.add(periodDay);
        }

        return periodDays;
    }

    private List<LocalDate> getNextFertileDays(LocalDate nextPeriodDate) {
        int diff = manager.getPeriodLength() - 20;
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
