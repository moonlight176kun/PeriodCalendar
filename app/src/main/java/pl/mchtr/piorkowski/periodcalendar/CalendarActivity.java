package pl.mchtr.piorkowski.periodcalendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.stacktips.view.CustomCalendarView;
import com.stacktips.view.DayDecorator;
import com.stacktips.view.DayView;

import org.joda.time.LocalDate;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import pl.mchtr.piorkowski.periodcalendar.period_days.PeriodDaysManager;
import pl.mchtr.piorkowski.periodcalendar.util.AppPreferences;

public class CalendarActivity extends AppCompatActivity {

    public static final int GATHER_NEW_PREFERENCES = 1;
    private static Set<LocalDate> periodDays = new HashSet<>();
    private static Set<LocalDate> fertileDays = new HashSet<>();
    private static Set<LocalDate> ovulationDays = new HashSet<>();

    private PeriodDaysManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        manager = new PeriodDaysManager(this);
        periodDays = manager.getHistoricPeriodDays();
        fertileDays = manager.getHistoricFertileDays();
        ovulationDays = manager.getHistoricOvulationDays();

        SharedPreferences preferences = getSharedPreferences(AppPreferences.SHARED_PREFERENCES_FILE, MODE_PRIVATE);

        if (!preferences.contains(AppPreferences.BASIC_USER_PREFERENCES_AVAILABLE)) {
            startPreferencesActivity();
        }

        refreshCalendar();
    }

    private void refreshCalendar() {
        CustomCalendarView calendarView = (CustomCalendarView) findViewById(R.id.calendarView);
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        calendarView.setShowOverflowDate(true);
        calendarView.setDecorators(Collections.<DayDecorator>singletonList(new SampleDayDecorator()));
        calendarView.refreshCalendar(currentCalendar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GATHER_NEW_PREFERENCES) {
            periodDays = manager.getHistoricPeriodDays();
            fertileDays = manager.getHistoricFertileDays();
            ovulationDays = manager.getHistoricOvulationDays();
            refreshCalendar();
        }
    }

    private void startPreferencesActivity() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivityForResult(intent, GATHER_NEW_PREFERENCES);
    }

    public void goToPreferences(View view) {
        startPreferencesActivity();
    }

    private class SampleDayDecorator implements DayDecorator {

        @Override
        public void decorate(DayView dayView) {
            LocalDate actualDate = new LocalDate(dayView.getDate());

            dayView.setBackgroundColor(getResources().getColor(R.color.neutralBackground));

            if (periodDays.contains(actualDate)) {
                dayView.setBackgroundColor(getResources().getColor(R.color.periodDayBackground));
            }

            if (fertileDays.contains(actualDate)) {
                dayView.setBackgroundColor(getResources().getColor(R.color.fertileDayBackground));
            }

            if (ovulationDays.contains(actualDate)) {
                dayView.setBackgroundColor(getResources().getColor(R.color.ovulationDayBackground));
            }
        }
    }

}
