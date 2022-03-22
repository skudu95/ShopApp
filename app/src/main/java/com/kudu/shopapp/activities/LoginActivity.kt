package com.kudu.shopapp.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.kudu.shopapp.R
import com.kudu.shopapp.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
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

        //click events assigned
        binding.tvForgotPassword.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
        binding.tvRegister.setOnClickListener(this)

        /*binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }*/
    }


    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.tv_forgot_password -> {
                    startActivity(Intent(this, ForgotPasswordActivity::class.java))
                }
                R.id.btn_login -> {
                    loginRegisteredUser()
                }
                R.id.tv_register -> {
                    startActivity(Intent(this, RegisterActivity::class.java))
                }

            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                //showErrorSnackBar("Your details are valid", false)
                true
            }
        }
    }

    private fun loginRegisteredUser() {

        if (validateLoginDetails()) {

            //show progress dialog
            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

            //login using firebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    hideProgressDialog()

                    if (task.isSuccessful) {
                        //TODO: send user to main activity
                        showErrorSnackBar("Login Successful..!!", false)
                    } else {
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }

    }


}