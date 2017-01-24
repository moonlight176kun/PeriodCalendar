package pl.mchtr.piorkowski.periodcalendar.period_days;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.joda.time.LocalDate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.mchtr.piorkowski.periodcalendar.util.AppPreferences;

public class PeriodDaysManager {
    private static final String TAG = "PeriodDaysManager";
    private static final Gson GSON = new Gson();
    private static final Type PERIOD_DAYS_GSON_TYPE = new TypeToken<ArrayList<PeriodDaysBean>>(){}.getType();
    public static final String EMPTY_LIST = "[]";

    private final SharedPreferences preferences;

    public PeriodDaysManager(Context ctx) {
        this.preferences = ctx.getSharedPreferences(AppPreferences.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    public List<PeriodDaysBean> getAllPeriodDaysBeans() {
        String periodDaysBeanString = preferences.getString(AppPreferences.PERIOD_DAYS_BEANS_LIST_KEY, EMPTY_LIST);
        return GSON.fromJson(periodDaysBeanString, PERIOD_DAYS_GSON_TYPE);
    }

    public Optional<PeriodDaysBean> getLastPeriodDaysBean() {
        List<PeriodDaysBean> beanList = getAllPeriodDaysBeans();
        if (beanList.isEmpty()) {
            return Optional.absent();
        }

        return Optional.fromNullable(beanList.get(beanList.size() - 1));
    }

    public Set<LocalDate> getHistoricPeriodDays() {
        return getHistoricPeriodDays(getAllPeriodDaysBeans());
    }

    public Set<LocalDate> getHistoricPeriodDays(List<PeriodDaysBean> periodDaysBeanList) {
        Set<LocalDate> set = new HashSet<>();

        for (PeriodDaysBean bean : periodDaysBeanList) {
            set.addAll(bean.getPeriodDays());
        }

        return set;
    }

    public Set<LocalDate> getHistoricFertileDays() {
        return getHistoricFertileDays(getAllPeriodDaysBeans());
    }

    public Set<LocalDate> getHistoricFertileDays(List<PeriodDaysBean> periodDaysBeanList) {
        Set<LocalDate> set = new HashSet<>();

        for (PeriodDaysBean bean : periodDaysBeanList) {
            set.addAll(bean.getFertileDays());
        }

        return set;
    }

    public Set<LocalDate> getHistoricOvulationDays() {
        return getHistoricOvulationDays(getAllPeriodDaysBeans());
    }

    public Set<LocalDate> getHistoricOvulationDays(List<PeriodDaysBean> periodDaysBeanList) {
        Set<LocalDate> set = new HashSet<>();

        for (PeriodDaysBean bean : periodDaysBeanList) {
            set.add(bean.getOvulationDay());
        }

        return set;
    }

    public int getPeriodLength() {
        return Integer.parseInt(preferences.getString(AppPreferences.PERIOD_LENGTH_KEY,
                AppPreferences.DEFAULT_PERIOD_LENGTH));
    }

    public int getMenstruationLength() {
        return Integer.parseInt(preferences.getString(AppPreferences.MENSTRUATION_LENGTH_KEY,
                AppPreferences.DEFAULT_MENSTRUATION_LENGTH));
    }

    public Optional<LocalDate> getLastPeriodDate() {
        String stringDate = preferences.getString(AppPreferences.LAST_PERIOD_DATE_KEY, null);
        if (stringDate == null) {
            return Optional.absent();
        }

        return Optional.fromNullable(AppPreferences.convertStringToDate(stringDate, null));
    }

    public void addNewPeriodDaysBean(PeriodDaysBean periodDaysBean) {
        List<PeriodDaysBean> list = getAllPeriodDaysBeans();
        list.add(periodDaysBean);
        String listString = GSON.toJson(list, PERIOD_DAYS_GSON_TYPE);
        Log.i(TAG, String.format("Adding new period days bean at the end of list: %s", listString));
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AppPreferences.PERIOD_DAYS_BEANS_LIST_KEY, listString);
        editor.apply();
    }

    public void updateNextPeriodDaysBean(PeriodDaysBean updatedPeriodDaysBean) {
        List<PeriodDaysBean> list = getAllPeriodDaysBeans();
        if (!list.isEmpty()) {
            list.remove(list.size() - 1);
        }
        list.add(updatedPeriodDaysBean);
        String listString = GSON.toJson(list, PERIOD_DAYS_GSON_TYPE);
        Log.i(TAG, String.format("Updating new period days bean at then end of list: %s", listString));
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AppPreferences.PERIOD_DAYS_BEANS_LIST_KEY, listString);
        editor.apply();
    }

    public void updateLastPeriodDate(LocalDate updatedLastPeriodDate) {
        String stringDate = AppPreferences.convertDateToString(updatedLastPeriodDate);
        Log.i(TAG, String.format("Updating last period day to: %s", stringDate));
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AppPreferences.LAST_PERIOD_DATE_KEY, stringDate);
        editor.apply();
    }
}
