package com.usedocetangerinaestoque.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.usedocetangerinaestoque.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.usedocetangerinaestoque.services.SessionManager

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        lifecycleScope.launch {
            delay(1_500)
            val target = if (sessionManager.isLogged())
                HomeActivity::class.java
            else
                LoginRegisterActivity::class.java

            startActivity(Intent(this@SplashActivity, target))
            finish()
        }
    }
}