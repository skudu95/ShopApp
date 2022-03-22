package com.kudu.shopapp.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kudu.shopapp.activities.RegisterActivity
import com.kudu.shopapp.model.User

class Firestore {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {

        // The "users" is a collection name
        mFireStore.collection("users")
            //Document ID for users fields. Here the document is the User ID
            .document(userInfo.id)
            //here the userInfo are fields and the SetOption is set to merge. It is if merge is to done later for replacing fields
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                //function of base activity for transferring the result to it
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while registering the user", e) // need to know further about this
            }
    }
}