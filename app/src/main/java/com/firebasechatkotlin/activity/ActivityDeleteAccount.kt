package com.firebasechatkotlin.activity

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.firebasechatkotlin.R
import com.firebasechatkotlin.databinding.ActivityDeleteUserAccountBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class ActivityDeleteAccount : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityDeleteUserAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_delete_user_account)
        binding.etEmailId.setText(FirebaseAuth.getInstance().currentUser!!.email)
        binding.btnDeleteAccount.setOnClickListener(this)
    }


    override fun onClick(v: android.view.View?) {
        when (v?.id) {
            R.id.btnDeleteAccount -> {
                if (TextUtils.isEmpty(binding.etPassword.text)) {
                    Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
                } else {
                    binding.progress.visibility = View.VISIBLE
                    deleteAccount()
                }
            }

        }
    }

    private fun deleteAccount() {
        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(
            user!!.email.toString(),
            binding.etPassword.text.toString()
        )
        var deletedUserId = user.uid

        user?.let {
            it.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        it.delete()
                            .addOnCompleteListener { deleteTask ->
                                if (deleteTask.isSuccessful) {
                                    Toast.makeText(this, "Account Deleted Successfully.", Toast.LENGTH_SHORT).show()
                                    val db = FirebaseFirestore.getInstance()
                                    val collectionReference = db.collection("users")
                                    val documentId = deletedUserId
                                    val documentReference = collectionReference.document(documentId)
                                    documentReference.delete()
                                        .addOnSuccessListener {
                                            startActivity(Intent(this,LoginActivity::class.java))
                                            finishAffinity()
                                        }
                                        .addOnFailureListener { e ->
                                            // An error occurred
                                        }
                                }
                            }
                        binding.progress.visibility = View.GONE
                    }
                    else{
                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                        binding.progress.visibility = View.GONE
                    }

                }
        }

    }
}