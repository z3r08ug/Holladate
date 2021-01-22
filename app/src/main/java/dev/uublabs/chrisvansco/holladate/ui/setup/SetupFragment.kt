package dev.uublabs.chrisvansco.holladate.ui.setup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage.*
import com.theartofdev.edmodo.cropper.CropImageView
import dev.uublabs.chrisvansco.holladate.R
import dev.uublabs.chrisvansco.holladate.databinding.SetupFragmentBinding
import dev.uublabs.chrisvansco.holladate.ui.main.MainActivity
import dev.uublabs.chrisvansco.holladate.ui.util.BaseFragment

class SetupFragment : BaseFragment() {

    private var auth: FirebaseAuth? = null
    private var usersRef: DatabaseReference? = null
    private var currentUserId: String = ""
    private var userProfilePicRef: StorageReference? = null
    private var downloadUrl: String? = null

    private var _binding: SetupFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = SetupFragment()
        private val TAG = SetupActivity::class.java.simpleName + "_TAG"
        private const val GALLERY_PICK = 5
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = SetupFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val currentUser: FirebaseUser? = auth?.currentUser

        if (currentUser != null) {
            sendUserToMainActivity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        currentUserId = auth!!.currentUser!!.uid
        usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId)
        userProfilePicRef = FirebaseStorage.getInstance().reference.child("profile_pic")
        binding.setupProfilePicCIV.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, Companion.GALLERY_PICK)
        }
        binding.setupSaveBTN.setOnClickListener { saveAccountSetupInformation() }
        usersRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profile_pic")) {
                        val image: String = dataSnapshot.child("profile_pic").value.toString()
                        Picasso.get().load(image).placeholder(R.drawable.profile)
                            .into(binding.setupProfilePicCIV)
                    } else {
                        Toast.makeText(
                            activity,
                            "Please select a profile image first...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Companion.GALLERY_PICK && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(requireActivity())
        }
        if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result: ActivityResult? = getActivityResult(data)
            if (resultCode == AppCompatActivity.RESULT_OK) {
                binding.progressBar.visibility = View.VISIBLE
                val resultUri: Uri = result!!.uri
                binding.setupProfilePicCIV.setImageURI(resultUri)
                val filePath: StorageReference? = userProfilePicRef?.child("$currentUserId.png")
                filePath?.putFile(resultUri)?.addOnCompleteListener { task ->
                    binding.progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        Toast.makeText(
                            activity,
                            "Profile image was stored successfully...",
                            Toast.LENGTH_SHORT
                        ).show()
                        task.result?.metadata?.reference?.downloadUrl
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    downloadUrl = task.result.toString()
                                    Log.d(TAG, "onComplete: downloadURL for pic: $downloadUrl")
                                }
                            }

                        //                            usersRef.child("profile_pic").setValue(downloadUrl)
                        //                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                        //                                    {
                        //                                        @Override
                        //                                        public void onComplete(@NonNull Task<Void> task)
                        //                                        {
                        //                                            if (task.isSuccessful())
                        //                                            {
                        //                                                Log.d(TAG, "onComplete: profile pic saved to database");
                        //                                                //Intent intent = new Intent(SetupActivity.this, SetupActivity.class);
                        //                                                //startActivity(intent);
                        //
                        //                                                Toast.makeText(SetupActivity.this, "Profile image was saved successfully...", Toast.LENGTH_SHORT).show();
                        //                                            }
                        //                                            else
                        //                                            {
                        //                                                String message = task.getException().getMessage();
                        //                                                Toast.makeText(SetupActivity.this, String.format("Error occurred: %s", message), Toast.LENGTH_SHORT).show();
                        //                                            }
                        //                                        }
                        //                                    });
                    } else {
                        val message: String = task.exception?.message.toString()
                        Toast.makeText(
                            activity,
                            String.format("Error occurred: %s", message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    activity,
                    "Error occurred: Image could not be cropped. Please try again...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveAccountSetupInformation() {
        val username = binding.setupUsernameET.text.toString()
        val fullname: String = binding.setupFullNameET.text.toString()
        val country: String = binding.setupCountryET.text.toString()
        val age: String = binding.setupAgeET.text.toString()
        val location: String = binding.setupLocationET.text.toString()
        val category: String = binding.setupCategoryET.text.toString()
        when {
            TextUtils.isEmpty(username) -> {
                Toast.makeText(activity, "Username needs to be filled out...", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(fullname) -> {
                Toast.makeText(activity, "Full name needs to be filled out...", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(country) -> {
                Toast.makeText(activity, "Country needs to be filled out...", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(downloadUrl) -> {
                Toast.makeText(activity, "Please select a profile picture first...", Toast.LENGTH_SHORT)
                    .show()
            }
            TextUtils.isEmpty(age) -> {
                Toast.makeText(activity, "Age needs to be filled out...", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(location) -> {
                Toast.makeText(activity, "Location needs to be filled out...", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(category) -> {
                Toast.makeText(activity, "Category needs to be filled out...", Toast.LENGTH_SHORT).show()
            }
            else -> {
                binding.progressBar.visibility = View.VISIBLE
                val userMap = HashMap<String, Any>()
                userMap["username"] = username
                userMap["fullName"] = fullname
                userMap["country"] = country
                userMap["status"] = "Hi I am a developer."
                userMap["gender"] = "Alien"
                userMap["dob"] = "none"
                userMap["relationship"] = "none"
                userMap["profile_pic"] = downloadUrl!!
                userMap["age"] = age
                userMap["location"] = location
                userMap["category"] = category
                userMap["uid"] = currentUserId
                usersRef?.updateChildren(userMap)
                    ?.addOnCompleteListener { task ->
                        binding.progressBar.visibility = View.GONE
                        if (task.isSuccessful) {
                            sendUserToMainActivity()
                            Toast.makeText(
                                activity,
                                "Your account was created successfully...",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            val message: String? = task.exception?.message
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

    private fun sendUserToMainActivity() {
        sendUserToActivity(MainActivity())
    }

}