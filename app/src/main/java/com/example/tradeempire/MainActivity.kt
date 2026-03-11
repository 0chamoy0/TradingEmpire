package com.example.tradeempire

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.tradeempire.ui.*
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class
MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.coordinator)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        bottomNavigation = findViewById(R.id.bottom_navigation)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_homepage -> loadFragment(HomepageFragment())
                R.id.nav_chart -> loadFragment(ChartFragment())
                R.id.nav_available_fi -> loadFragment(AvailableFIFragment())
                R.id.nav_settings -> loadFragment(SettingsFragment())
            }
            true
        }

        if (savedInstanceState != null) {
            val selectedId = savedInstanceState.getInt("SELECTED_NAV_ITEM")
            bottomNavigation.selectedItemId = selectedId
        } else {
            bottomNavigation.selectedItemId = R.id.nav_homepage
            loadFragment(HomepageFragment())
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("SELECTED_NAV_ITEM", bottomNavigation.selectedItemId)
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}