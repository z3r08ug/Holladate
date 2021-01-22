package dev.uublabs.chrisvansco.holladate.ui.register

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dev.uublabs.chrisvansco.holladate.databinding.RegisterFragmentBinding
import dev.uublabs.chrisvansco.holladate.ui.main.MainActivity
import dev.uublabs.chrisvansco.holladate.ui.setup.SetupActivity
import dev.uublabs.chrisvansco.holladate.ui.util.BaseFragment
import kotlinx.android.synthetic.main.register_fragment.*


class RegisterFragment : BaseFragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    private var _binding: RegisterFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RegisterFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerBTN.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        val email: String = binding.registerEmailET.text.toString()
        val password: String = binding.registerPasswordET.text.toString()
        val confirmPassword: String = binding.registerConfirmET.text.toString()

        when {
            TextUtils.isEmpty(email) -> {
                Toast.makeText(activity, "Please enter your email address...", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(activity, "Please enter your password...", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(confirmPassword) -> {
                Toast.makeText(activity, "Please confirm your password...", Toast.LENGTH_SHORT).show()
            }
            password != confirmPassword -> {
                Toast.makeText(
                    activity,
                    "The password entered does not match the confirmed password...",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                progressBar.visibility = View.VISIBLE
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        progressBar.visibility = View.GONE
                        if (task.isSuccessful) {
                            Toast.makeText(
                                activity,
                                "Authentication was successful...",
                                Toast.LENGTH_SHORT
                            ).show()
                            sendUserToActivity(SetupActivity())
                        } else {
                            val message = task.exception!!.message
                            Toast.makeText(
                                activity,
                                String.format("Error occurred: %s", message),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            sendUserToActivity(MainActivity())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}