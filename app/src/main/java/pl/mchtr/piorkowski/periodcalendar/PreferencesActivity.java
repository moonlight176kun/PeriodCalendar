package pl.mchtr.piorkowski.periodcalendar;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import pl.mchtr.piorkowski.periodcalendar.util.AppPreferences;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
    }

    public void menstruationLengthPlusOne(View view) {
    }

    public void menstruationLengthMinusOne(View view) {
    }

    public void lastPeriodDateSet(int year, int month, int day) {
        System.out.println(year + "/" + month + "/" + day);
    }

    public void showDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), AppPreferences.DATE_PICKER_DIALOG_TAG);
    }
}
