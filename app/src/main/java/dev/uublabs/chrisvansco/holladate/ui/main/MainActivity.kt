package dev.uublabs.chrisvansco.holladate.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dev.uublabs.chrisvansco.holladate.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
    }
}