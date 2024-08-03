package com.cholera.eagleeye.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.cholera.eagleeye.R;
public class SettingsActivity extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        if (savedInstanceState == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_fragment_container, new SettingsPreferenceFragment())
                    .commit();
        }

        return view;
    }

    public static class SettingsPreferenceFragment extends PreferenceFragmentCompat
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private static final int REQUEST_CODE_RINGTONE_PICKER = 1001;
        private SharedPreferences sharedPreferences;

        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            setupPreferenceListeners();
        }

        private void setupPreferenceListeners() {
            EditTextPreference usernamePref = findPreference("username");
            ListPreference updateFrequencyPref = findPreference("update_frequency");
            EditTextPreference locationPref = findPreference("location");
            ListPreference dataSourcePref = findPreference("data_source");
            Preference notificationSoundPref = findPreference("notification_sound");
            ListPreference themePref = findPreference("theme");

            if (usernamePref != null) {
                usernamePref.setOnPreferenceChangeListener((preference, newValue) -> {
                    savePreference("username", newValue.toString());
                    return true;
                });
            }

            if (updateFrequencyPref != null) {
                updateFrequencyPref.setOnPreferenceChangeListener((preference, newValue) -> {
                    savePreference("update_frequency", newValue.toString());
                    return true;
                });
            }

            if (locationPref != null) {
                locationPref.setOnPreferenceChangeListener((preference, newValue) -> {
                    savePreference("location", newValue.toString());
                    return true;
                });
            }

            if (dataSourcePref != null) {
                dataSourcePref.setOnPreferenceChangeListener((preference, newValue) -> {
                    savePreference("data_source", newValue.toString());
                    return true;
                });
            }

            if (notificationSoundPref != null) {
                notificationSoundPref.setOnPreferenceClickListener(preference -> {
                    Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);

                    String currentSound = sharedPreferences.getString("notification_sound", "");
                    if (currentSound.isEmpty()) {
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                    } else {
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(currentSound));
                    }

                    startActivityForResult(intent, REQUEST_CODE_RINGTONE_PICKER);
                    return true;
                });
            }

            if (themePref != null) {
                themePref.setOnPreferenceChangeListener((preference, newValue) -> {
                    savePreference("theme", newValue.toString());
                    return true;
                });
            }
        }

        private void savePreference(String key, Boolean value) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }

        private void savePreference(String key, String value) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CODE_RINGTONE_PICKER && data != null) {
                Uri ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                if (ringtoneUri != null) {
                    savePreference("notification_sound", ringtoneUri.toString());
                } else {
                    savePreference("notification_sound", "");
                }
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }

        @Override
        public void onResume() {
            super.onResume();
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}
