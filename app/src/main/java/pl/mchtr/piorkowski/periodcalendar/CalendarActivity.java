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

import pl.mchtr.piorkowski.periodcalendar.util.AppPreferences;

public class CalendarActivity extends AppCompatActivity {

    public static Set<LocalDate> periodDays = new HashSet<>();
    public static Set<LocalDate> fertileDays = new HashSet<>();
    public static Set<LocalDate> ovulationDays = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        SharedPreferences preferences = getSharedPreferences(AppPreferences.SHARED_PREFERENCES_FILE, MODE_PRIVATE);

        if (!preferences.contains(AppPreferences.BASIC_USER_PREFERENCES_AVAILABLE)) {
            startPreferencesActivity();
        }

        CustomCalendarView calendarView = (CustomCalendarView) findViewById(R.id.calendarView);
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);
        calendarView.setShowOverflowDate(true);
        calendarView.setDecorators(Collections.<DayDecorator>singletonList(new SampleDayDecorator()));
        calendarView.refreshCalendar(currentCalendar);
    }

    private void startPreferencesActivity() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

    public void goToPreferences(View view) {
        startPreferencesActivity();
    }

    private class SampleDayDecorator implements DayDecorator {

        @Override
        public void decorate(DayView dayView) {
            LocalDate actualDate = new LocalDate(dayView.getDate());

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
