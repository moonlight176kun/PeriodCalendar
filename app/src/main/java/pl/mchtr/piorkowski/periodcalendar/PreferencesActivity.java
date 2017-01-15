package pl.mchtr.piorkowski.periodcalendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.google.common.base.Optional;

import org.joda.time.LocalDate;

import pl.mchtr.piorkowski.periodcalendar.util.AppPreferences;
import pl.mchtr.piorkowski.periodcalendar.util.OptionalUtil;
import pl.mchtr.piorkowski.periodcalendar.validator.IntegerWithinRange;

public class PreferencesActivity extends AppCompatActivity implements DatePickerFragment.OnDateSetListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        EditText menstruationLength = (EditText) findViewById(R.id.menstruation_length_value);
        EditText periodLength = (EditText) findViewById(R.id.period_length_value);
        EditText lastPeriodDate = (EditText) findViewById(R.id.last_period_date_value);
        Switch incomingPeriodNotification  = (Switch) findViewById(R.id.incoming_period_notification);
        Switch fertileDaysNotification = (Switch) findViewById(R.id.fertile_days_notification);
        Switch ovulationNotification = (Switch) findViewById(R.id.ovulation_notification);

        menstruationLength.addTextChangedListener(new IntegerWithinRange(menstruationLength, 8, 60));
        periodLength.addTextChangedListener(new IntegerWithinRange(periodLength, 5, 12));

        setFieldValue(menstruationLength, AppPreferences.MENSTRUATION_LENGTH_KEY,
                AppPreferences.DEFAULT_MENSTRUATION_LENGTH);
        setFieldValue(periodLength, AppPreferences.PERIOD_LENGTH_KEY,
                AppPreferences.DEFAULT_PERIOD_LENGTH);
        setFieldValue(lastPeriodDate, AppPreferences.LAST_PERIOD_DATE_KEY, AppPreferences.defaultLastPeriodDate());
        setFieldValue(incomingPeriodNotification, AppPreferences.INCOMING_PERIOD_NOTIFICATION_KEY);
        setFieldValue(fertileDaysNotification, AppPreferences.FERTILE_DAYS_NOTIFICATION_KEY);
        setFieldValue(ovulationNotification, AppPreferences.OVULATION_NOTIFICATION_KEY);

    }

    private void setFieldValue(EditText editText, String sharedPreferencesKey, String defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences(AppPreferences.SHARED_PREFERENCES_FILE,
                MODE_PRIVATE);
        String value = sharedPreferences.getString(sharedPreferencesKey, defaultValue);
        editText.setText(value);
    }

    private void setFieldValue(Switch switchView, String sharedPreferencesKey) {
        SharedPreferences sharedPreferences = getSharedPreferences(AppPreferences.SHARED_PREFERENCES_FILE,
                MODE_PRIVATE);
        switchView.setChecked(sharedPreferences.getBoolean(sharedPreferencesKey, false));
    }

    public void menstruationLengthPlusOne(View view) {
        changeNumericalFieldValueByDiff(R.id.menstruation_length_value, 1);
    }

    public void menstruationLengthMinusOne(View view) {
        changeNumericalFieldValueByDiff(R.id.menstruation_length_value, -1);
    }

    public void periodLengthPlusOne(View view) {
        changeNumericalFieldValueByDiff(R.id.period_length_value, 1);
    }

    public void periodLengthMinusOne(View view) {
        changeNumericalFieldValueByDiff(R.id.period_length_value, -1);
    }

    public void showDatePickerDialog(View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        SharedPreferences preferences = getSharedPreferences(AppPreferences.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        String dateString = preferences.getString(AppPreferences.LAST_PERIOD_DATE_KEY,
                AppPreferences.defaultLastPeriodDate());
        newFragment.setInitiallySelectedDate(AppPreferences.convertStringToDate(dateString, new LocalDate()));
        newFragment.show(getFragmentManager(), AppPreferences.DATE_PICKER_DIALOG_TAG);
    }

    @Override
    public void onDateSet(LocalDate selectedDate) {
        EditText lastPeriodValue = (EditText) findViewById(R.id.last_period_date_value);
        lastPeriodValue.setText(AppPreferences.convertDateToString(selectedDate));
    }

    private void changeNumericalFieldValueByDiff(int fieldId, int diff) {
        EditText textView = (EditText) findViewById(fieldId);
        Optional<Integer> optionalFieldValue = OptionalUtil.parseInteger(textView.getText().toString());
        if (optionalFieldValue.isPresent()) {
            textView.setText(String.valueOf(optionalFieldValue.get() + diff));
        }
    }

    public void updatePreferences(View view) {
        SharedPreferences preferences = getSharedPreferences(AppPreferences.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        savePreferenceStringValue(R.id.menstruation_length_value, AppPreferences.MENSTRUATION_LENGTH_KEY, editor);
        savePreferenceStringValue(R.id.period_length_value, AppPreferences.PERIOD_LENGTH_KEY, editor);
        savePreferenceStringValue(R.id.last_period_date_value, AppPreferences.LAST_PERIOD_DATE_KEY, editor);
        savePreferenceBooleanValue(R.id.incoming_period_notification,
                AppPreferences.INCOMING_PERIOD_NOTIFICATION_KEY, editor);
        savePreferenceBooleanValue(R.id.fertile_days_notification,
                AppPreferences.FERTILE_DAYS_NOTIFICATION_KEY, editor);
        savePreferenceBooleanValue(R.id.ovulation_notification,
                AppPreferences.OVULATION_NOTIFICATION_KEY, editor);

        editor.putBoolean(AppPreferences.BASIC_USER_PREFERENCES_AVAILABLE, true);
        editor.apply();

        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }

    public void savePreferenceStringValue(int viewId, String preferenceKey, SharedPreferences.Editor editor) {
        EditText view = (EditText) findViewById(viewId);
        editor.putString(preferenceKey, view.getText().toString());
    }

    public void savePreferenceBooleanValue(int viewId, String preferenceKey, SharedPreferences.Editor editor) {
        Switch s = (Switch) findViewById(viewId);
        editor.putBoolean(preferenceKey, s.isChecked());
    }
}
