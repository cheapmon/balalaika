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
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.github.cheapmon.balalaika.databinding.ActivityMainBinding
import com.github.cheapmon.balalaika.databinding.AppBarMainBinding
import com.github.cheapmon.balalaika.databinding.NavDictionaryBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

/** Main activity for Balalaika */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    /** Prepare user interface and check for database updates */
    override fun onCreate(savedInstanceState: Bundle?) {
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
                R.id.nav_home, R.id.nav_history, R.id.nav_bookmarks, R.id.nav_preferences,
                R.id.nav_selection
            ),
            drawerLayout = drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        setupNavView(navView)
        observeData(activityBinding)
    }

    private fun setupNavView(navView: NavigationView) {
        val layoutInflater = LayoutInflater.from(navView.context)
        val binding = NavDictionaryBinding.inflate(layoutInflater, navView, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        navView.addHeaderView(binding.root)
    }

    private fun observeData(binding: ActivityMainBinding) {
        val contentBinding = binding.appBarMain.mainContent
        contentBinding.lifecycleOwner = this
        contentBinding.viewModel = viewModel
        viewModel.messages.observe(this, Observer {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        })
    }

    /** @suppress */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
