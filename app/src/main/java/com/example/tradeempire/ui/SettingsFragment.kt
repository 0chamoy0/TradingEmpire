package com.example.tradeempire.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.*
import com.example.tradeempire.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        setupThemeSwitch()

        setupNotificationPreference()
    }

    private fun setupThemeSwitch() {
        val themePreference = findPreference<SwitchPreferenceCompat>("pref_key_theme_switch")
        themePreference?.setOnPreferenceChangeListener { preference, newValue ->
            val isDarkThemeEnabled = newValue as Boolean
            applyTheme(isDarkThemeEnabled)
            true
        }
    }

    private fun applyTheme(isDark: Boolean) {
        val mode = if (isDark) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun setupNotificationPreference() {
        val notificationPreference = findPreference<Preference>("pref_key_notification_permission")
        notificationPreference?.setOnPreferenceClickListener {
            openAppNotificationSettings()
            true
        }
    }

    private fun openAppNotificationSettings() {
        try {
            val intent = Intent().apply {
                when {
                    android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O -> {
                        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                    }

                    else -> {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", requireContext().packageName, null)
                    }
                }
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Could not open notification settings.",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }

}
