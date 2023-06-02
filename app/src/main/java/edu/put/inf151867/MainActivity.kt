package edu.put.inf151867

import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import edu.put.inf151867.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var mPrefs: SharedPreferences
    companion object {
        var LAST_SYNC_DATE = ""
        var USERNAME = ""
        var GAMES_NUMBER = ""
        var EXPANSIONS_NUMBER = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        mPrefs = getSharedPreferences(localClassName, MODE_PRIVATE)
        val lastSyncDate = mPrefs.getString("lastSyncDate", LAST_SYNC_DATE).toString()
        LAST_SYNC_DATE = lastSyncDate
        val username = mPrefs.getString("username", USERNAME).toString()
        USERNAME = username
        val gn = mPrefs.getString("gamesNumber", GAMES_NUMBER).toString()
        GAMES_NUMBER = gn
        val en = mPrefs.getString("expansionsNumber", EXPANSIONS_NUMBER).toString()
        EXPANSIONS_NUMBER = en
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> Toast.makeText(this, "Brak opcji", Toast.LENGTH_LONG).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onPause() {
        super.onPause()

        val ed: SharedPreferences.Editor = mPrefs.edit()
        ed.putString("lastSyncDate", LAST_SYNC_DATE)
        ed.putString("username", USERNAME)
        ed.putString("gamesNumber", GAMES_NUMBER)
        ed.putString("expansionsNumber", EXPANSIONS_NUMBER)
        ed.apply()
    }
}