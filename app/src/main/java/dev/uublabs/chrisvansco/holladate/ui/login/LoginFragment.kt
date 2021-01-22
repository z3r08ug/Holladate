package dev.uublabs.chrisvansco.holladate.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dev.uublabs.chrisvansco.holladate.databinding.LoginFragmentBinding
import dev.uublabs.chrisvansco.holladate.ui.main.MainActivity
import dev.uublabs.chrisvansco.holladate.ui.register.RegisterActivity
import dev.uublabs.chrisvansco.holladate.ui.util.BaseFragment


class LoginFragment : BaseFragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.loginBTN.setOnClickListener {
            login()
        }
        binding.loginNewAccountLinkTV.setOnClickListener {
            sendUserToRegisterActivity()
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser: FirebaseUser? = auth.currentUser

        if (currentUser != null) {
            sendUserToMainActivity()
        }
    }

    private fun login() {
        val email: String = binding.loginEmailET.text.toString()
        val password: String = binding.loginPasswordET.text.toString()

        when {
            TextUtils.isEmpty(email) -> {
                Toast.makeText(activity, "An email is required...", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(activity, "A password is required...", Toast.LENGTH_SHORT).show()
            }
            else -> {
                binding.progressBar.visibility = View.VISIBLE
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            binding.progressBar.visibility = View.GONE
                            if (task.isSuccessful) {
                                sendUserToMainActivity()
                                Toast.makeText(activity, "Login was successful...", Toast.LENGTH_SHORT).show()
                            } else {
                                val message = task.exception!!.message
                                Toast.makeText(activity, String.format("Error occurred: %s", message), Toast.LENGTH_SHORT).show()
                            }
                        }
            }
        }
    }

    private fun sendUserToMainActivity() {
        sendUserToActivity(MainActivity())
    }

    private fun sendUserToRegisterActivity() {
        val intent = Intent(activity, RegisterActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}