/*
 * Copyright 2020 Simon Kaleschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cheapmon.balalaika

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.github.cheapmon.balalaika.data.insert.ImportUtil
import com.github.cheapmon.balalaika.data.storage.Storage
import com.github.cheapmon.balalaika.databinding.ActivityMainBinding
import com.github.cheapmon.balalaika.databinding.AppBarMainBinding
import com.google.android.material.navigation.NavigationView
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    @Inject
    lateinit var importUtil: ImportUtil

    @Inject
    lateinit var storage: Storage

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as Application).appComponent.inject(this)

        super.onCreate(savedInstanceState)

        val activityBinding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        val barBinding: AppBarMainBinding = activityBinding.appBarMain

        setSupportActionBar(barBinding.toolbar)
        val drawerLayout: DrawerLayout = activityBinding.drawerLayout
        val navView: NavigationView = activityBinding.navView

        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.nav_home, R.id.nav_history, R.id.nav_bookmarks, R.id.nav_preferences
            ),
            drawerLayout = drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        lifecycleScope.launchWhenCreated {
            importUtil.import()
            val orderKey = getString(R.string.preferences_key_order)
            val viewKey = getString(R.string.preferences_key_view)
            if(!storage.contains(orderKey)) storage.putString(orderKey, "-1")
            if(!storage.contains(viewKey)) storage.putString(viewKey, "1")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}