package com.github.cheapmon.balalaika

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.github.cheapmon.balalaika.db.BalalaikaDatabase
import com.github.cheapmon.balalaika.db.Category
import com.github.cheapmon.balalaika.ui.home.HomeFragmentDirections
import com.github.cheapmon.balalaika.ui.home.OrderByDialog
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity(), OrderByDialog.OrderByDialogListener {
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BalalaikaDatabase.init(this)
        setupView()
    }

    private fun setupView() {
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
                setOf(R.id.nav_home, R.id.nav_info, R.id.nav_preferences, R.id.nav_history),
                drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.search_button) {
            val navController = findNavController(R.id.nav_host_fragment)
            navController.navigate(HomeFragmentDirections.actionNavHomeToSearchItemFragment(null))
        } else if(id == R.id.order_button) {
            lifecycleScope.launch {
                val categories = withContext(Dispatchers.IO) {
                    BalalaikaDatabase.instance.categoryDao().getOrdered().toTypedArray()
                } + arrayOf(Category("default", "Default", "", 0, hidden = true, orderBy = true))
                OrderByDialog(categories).show(supportFragmentManager, "OrderByDialog")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDialogItemClick(dialog: DialogFragment, category: Category) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit().putString("order_by", category.id).apply()
        val navController = findNavController(R.id.nav_host_fragment)
        navController.navigate(HomeFragmentDirections.actionNavHomeSelf())
    }
}
