package pl.mchtr.piorkowski.periodcalendar.util;

import java.text.DateFormat;
import java.util.Date;

/**
 * Utility class holding common application preferences.
 */
public final class AppPreferences {

    private AppPreferences() { }

    private static final String APPLICATION_PREFIX = "pl.mchtr.piorkowski.periodcalendar.";
    public static final String SHARED_PREFERENCES_FILE = APPLICATION_PREFIX + "shared_preferences";
    public static final String BASIC_USER_PREFERENCES_AVAILABLE = "basic_user_preferences_available";
    public static final String DATE_PICKER_DIALOG_TAG = APPLICATION_PREFIX + "date_picker_dialog";
    public static final String MENSTRUATION_LENGTH_KEY = APPLICATION_PREFIX + "menstruation_length";
    public static final String DEFAULT_MENSTRUATION_LENGTH = "28";
    public static final String PERIOD_LENGTH_KEY = APPLICATION_PREFIX + "period_length";
    public static final String DEFAULT_PERIOD_LENGTH = "5";
    public static final String LAST_PERIOD_DATE_KEY = APPLICATION_PREFIX + "last_period_date";

    public static String defaultLastPeriodDate() {
        return lastPeriodDate(new Date());
    }

    public static String lastPeriodDate(Date date) {
        return DateFormat.getDateInstance(DateFormat.SHORT).format(date);
    }

    public static final String INCOMING_PERIOD_NOTIFICATION_KEY = APPLICATION_PREFIX + "incoming_period_notification";
    public static final String FERTILE_DAYS_NOTIFICATION_KEY = APPLICATION_PREFIX + "fertile_days_notification";
    public static final String OVULATION_NOTIFICATION_KEY = APPLICATION_PREFIX + "ovulation_notification";

    public static Date lastPeriodDate(String dateString) {
        return lastPeriodDate(dateString, new Date());
    }

    public static Date lastPeriodDate(String dateString, Date defaultDate) {
        return OptionalUtil.parseDate(dateString, DateFormat.getDateInstance(DateFormat.SHORT)).or(defaultDate);
    }


}
