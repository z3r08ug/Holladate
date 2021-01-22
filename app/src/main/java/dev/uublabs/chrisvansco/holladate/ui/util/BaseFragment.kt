package dev.uublabs.chrisvansco.holladate.ui.util

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment

open class BaseFragment: Fragment() {
    fun sendUserToActivity(newActivity: Activity) {
        val intent = Intent(activity, newActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        activity?.finish()
    }
}