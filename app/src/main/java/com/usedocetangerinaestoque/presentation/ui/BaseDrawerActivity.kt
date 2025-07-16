package com.usedocetangerinaestoque.presentation.ui

import android.content.Intent
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.usedocetangerinaestoque.R
import com.usedocetangerinaestoque.services.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseDrawerActivity : AppCompatActivity() {

    protected lateinit var drawerLayout: DrawerLayout
    protected lateinit var toolbar: Toolbar
    protected lateinit var navView: NavigationView
    @Inject lateinit var sessionManager: SessionManager

    override fun setContentView(layoutResID: Int) {
        val full = layoutInflater.inflate(R.layout.activity_drawer_base, null)
        val content = full.findViewById<FrameLayout>(R.id.fragmentContainer)
        layoutInflater.inflate(layoutResID, content, true)
        super.setContentView(full)

        drawerLayout = findViewById(R.id.drawerLayout)
        toolbar    = findViewById(R.id.toolbar)
        navView    = findViewById(R.id.navigationView)

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.nav_stock -> startActivity(Intent(this, StockActivity::class.java))
                R.id.nav_logout  -> {
                    sessionManager.setLogged(false)
                    finishAffinity()
                    startActivity(Intent(this, LoginRegisterActivity::class.java))
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }
}