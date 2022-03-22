package com.kudu.shopapp.activities

import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.kudu.shopapp.R
import com.kudu.shopapp.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
    }

    //setting up actionbar with back button
    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarForgotPasswordActivity)

        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.back_icon)
            actionbar.title = ""
//            actionbar.setDisplayShowTitleEnabled(false)
        }
        binding.toolbarForgotPasswordActivity.setNavigationOnClickListener { onBackPressed() }

        binding.btnSubmit.setOnClickListener {

            val email: String = binding.etEmailFPA.text.toString().trim { it <= ' ' }
            if (email.isEmpty()) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        hideProgressDialog()
                        if (task.isSuccessful) {
                            Toast.makeText(this, resources.getString(R.string.email_sent_success), Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
            }
        }
    }
}