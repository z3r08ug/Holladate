package dev.uublabs.chrisvansco.holladate.ui.setup

import android.app.Instrumentation.*
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import dev.uublabs.chrisvansco.holladate.R
import dev.uublabs.chrisvansco.holladate.ui.main.MainActivity
import dev.uublabs.chrisvansco.holladate.ui.main.MainFragment


class SetupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setup_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SetupFragment.newInstance())
                .commitNow()
        }
    }
}