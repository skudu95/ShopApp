package com.kudu.shopapp.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kudu.shopapp.R
import com.kudu.shopapp.databinding.ActivityRegisterBinding

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        setUpActionBar()

        binding.tvLogin.setOnClickListener {
//            startActivity(Intent(this, LoginActivity::class.java))
            onBackPressed()
        }

        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }

    //setting up actionbar with back button
    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarRegisterActivity)

        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.back_icon)
        }
        binding.toolbarRegisterActivity.setNavigationOnClickListener { onBackPressed() }
    }

    //checking validations of text input
    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etFirstName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }
            TextUtils.isEmpty(binding.etLastName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }
            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            TextUtils.isEmpty(binding.etConfirmPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true
                )
                false
            }
            binding.etPassword.text.toString()
                .trim { it <= ' ' } != binding.etConfirmPassword.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    true
                )
                false
            }
            !binding.cbTermsAndCondition.isChecked -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_agree_terms_and_condition),
                    true
                )
                false
            }
            else -> {
                //showErrorSnackBar(resources.getString(R.string.registry_successful), false)
                true
            }
        }
    }

    private fun registerUser() {
        //checking validations
        if (validateRegisterDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

            //creating an instance and registering an user with email and password
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    hideProgressDialog()

                    //if registration is successful
                    if (task.isSuccessful) {

                        //firebase registered user
                        val firebaseUser: FirebaseUser = task.result!!.user!!

                        showErrorSnackBar(
                            "You are registered successfully. Your user id is ${firebaseUser.uid}",
                            false
                        )

                        //signs out the user and redirects to Login Activity
                        FirebaseAuth.getInstance().signOut()
                        finish()

                    } else {
                        //if registration failed, show error message
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }


}