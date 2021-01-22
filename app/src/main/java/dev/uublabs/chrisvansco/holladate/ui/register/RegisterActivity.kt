package dev.uublabs.chrisvansco.holladate.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dev.uublabs.chrisvansco.holladate.R

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RegisterFragment.newInstance())
                .commitNow()
        }
    }
}