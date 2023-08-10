package com.firebasechatkotlin.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebasechatkotlin.R
import com.firebasechatkotlin.adapters.CreateGroupUserListAdapter
import com.firebasechatkotlin.databinding.ActivityCreateGroupBinding
import com.firebasechatkotlin.models.GroupModel
import com.firebasechatkotlin.models.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File

class CreateGroupActivity : AppCompatActivity(), View.OnClickListener {


    val TAG = javaClass.simpleName

    var loggedUser: FirebaseUser? = null
    var userList: ArrayList<User>? = ArrayList()
    var createGroupListAdapter: CreateGroupUserListAdapter? = null
    var context: Context? = null
    lateinit var activityCreateGroupBinding: ActivityCreateGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCreateGroupBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_create_group)
        context = this;
        loggedUser = FirebaseAuth.getInstance().currentUser

        getData();

        activityCreateGroupBinding.ivClose.setOnClickListener(this)
        activityCreateGroupBinding.tvCreateGroup.setOnClickListener(this)
        activityCreateGroupBinding.ivGroupProfile.setOnClickListener(this)

    }

    private fun chooseImageIntent() {
        var intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(
            Intent.createChooser(intent, "Select Image"),
            SELECT_IMAGE_REQUEST
        )
    }

    var SELECT_IMAGE_REQUEST: Int = 1001
    var filePath: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            filePath = data.data

            try {
                var bitmap: Bitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, filePath)
                activityCreateGroupBinding.ivGroupProfile.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getData() {
        var ref = FirebaseFirestore.getInstance()
        var query = ref.collection("users")


        query.get().addOnCompleteListener {
            if (it.isSuccessful) {
                var querySnapShot = it.getResult()
                for (documentSnapshot in querySnapShot) {
                    var data = documentSnapshot.data
                    if (data.get("uid") as String != loggedUser?.uid) {
                        var user: User = User(
                            data.get("uid") as String,
                            data.get("displayname") as String,
                            data.get("email") as String,
                            false,
                            data.get("profile") as String
                        )
                        userList?.add(user)
                    }
                }
                setAdapter()
            }
        }

    }

    private fun setAdapter() {
        if (createGroupListAdapter == null) {
            createGroupListAdapter = CreateGroupUserListAdapter(this, userList!!)
            activityCreateGroupBinding.rvUsers.layoutManager = LinearLayoutManager(this)
            activityCreateGroupBinding.rvUsers.adapter = createGroupListAdapter
        } else
            createGroupListAdapter?.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivClose -> finish()

            R.id.ivGroupProfile -> {
                chooseImageIntent()
            }

            R.id.tvCreateGroup -> {
                if (filePath == null) {
                    Toast.makeText(context, "Please select profile image", Toast.LENGTH_SHORT)
                        .show()
                    return
                } else if (TextUtils.isEmpty(
                        activityCreateGroupBinding.edtGroupName.text.toString().trim()
                    )
                ) {
                    Toast.makeText(context, "Please enter group name", Toast.LENGTH_SHORT).show()
                    return
                }
                activityCreateGroupBinding.tvCreateGroup.visibility = View.GONE
                activityCreateGroupBinding.progress.visibility = View.VISIBLE
                Log.e(
                    TAG,
                    " selected items : " + createGroupListAdapter?.data?.filter { it.selected == true }?.size
                )
                var selectedUsers: MutableList<User>? =
                    createGroupListAdapter?.data?.filter { it.selected == true } as MutableList<User>

                if (selectedUsers?.size!! > 0) {

                    selectedUsers.add(
                        User(
                            loggedUser?.uid!!,
                            loggedUser?.displayName!!,
                            loggedUser?.email!!,
                            true
                        )
                    )
                    val key = FirebaseDatabase.getInstance().reference.child("groups").push().key
                    val groupKey =
                        FirebaseDatabase.getInstance().reference.child("groups").child(key!!).push()
                            .key


                    var fireStorageReference: StorageReference =
                        FirebaseStorage.getInstance().reference
                    var file = File(filePath.toString())
                    var storageRef = fireStorageReference.child("images/" + groupKey + file.name)
                    storageRef.putFile(filePath!!)
                        .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                            override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                                storageRef.downloadUrl.addOnSuccessListener { uri ->
                                    var firebase =
                                        FirebaseFirestore.getInstance().collection("groups")
                                    var groupModel = GroupModel(
                                        activityCreateGroupBinding.edtGroupName.text.toString()
                                            .trim(),
                                        selectedUsers,
                                        groupKey!!,
                                        uri.toString()
                                    )
                                    firebase.add(groupModel).addOnCompleteListener {
                                        Toast.makeText(
                                            applicationContext,
                                            "Group Created Successfully",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        finish()
                                    }.addOnFailureListener {
                                        Toast.makeText(
                                            applicationContext,
                                            it.message,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                }
                            }
                        }).addOnFailureListener(object : OnFailureListener {
                            override fun onFailure(p0: java.lang.Exception) {
                                activityCreateGroupBinding.progress.visibility = View.GONE
                                activityCreateGroupBinding.tvCreateGroup.visibility = View.VISIBLE
                            }

                        })
                }
                else{
                    Toast.makeText(applicationContext,"You have not selected any users",Toast.LENGTH_LONG).show()
                    activityCreateGroupBinding.progress.visibility = View.GONE
                }
            }
        }
    }


}