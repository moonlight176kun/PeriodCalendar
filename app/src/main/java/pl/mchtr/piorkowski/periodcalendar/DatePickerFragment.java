package pl.mchtr.piorkowski.periodcalendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Date;

import pl.mchtr.piorkowski.periodcalendar.util.AppPreferences;

/**
 * Fragment for DatePickerDialog handling.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private String initialDateString;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date initialDate = AppPreferences.lastPeriodDate(initialDateString);
        int year = initialDate.getYear();
        int month = initialDate.getMonth();
        int day = initialDate.getDay();
        DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);

        pickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        return pickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        PreferencesActivity activity = (PreferencesActivity) getActivity();
        activity.lastPeriodDateSet(year, month, dayOfMonth);
    }

    public String getInitialDateString() {
        return initialDateString;
    }

    public void setInitialDateString(String initialDateString) {
        this.initialDateString = initialDateString;
    }
}
