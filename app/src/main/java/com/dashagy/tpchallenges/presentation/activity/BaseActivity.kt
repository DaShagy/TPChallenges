package com.dashagy.tpchallenges.presentation.activity

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.dashagy.tpchallenges.R

abstract class BaseActivity : AppCompatActivity() {

    abstract fun onMenuChangeActivityPressed()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_change_activity -> {
                onMenuChangeActivityPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}