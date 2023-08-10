package com.firebasechatkotlin.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.firebasechatkotlin.R
import com.firebasechatkotlin.adapters.ChatAdapter
import com.firebasechatkotlin.databinding.ActivityChatBinding
import com.firebasechatkotlin.models.GroupModel
import com.firebasechatkotlin.models.Message
import com.firebasechatkotlin.models.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import java.util.Collections

class ChatActivity : AppCompatActivity(), View.OnClickListener {


    companion object {
        fun startActivity(context: Context, user: User, loggedUserId: String) {
            val intent: Intent? = Intent(context, ChatActivity::class.java)
            intent?.putExtra("user", user)
            intent?.putExtra("loggedUserId", loggedUserId)
            context.startActivity(intent)
        }
    }

    private var user: User? = null
    private var loggedUserId: String? = null;
    private var messageList: ArrayList<Message>? = ArrayList()
    private var database: FirebaseDatabase? = null
    private var adapter: ChatAdapter? = null

    private var isGroupChat: Boolean = false;
    private var group: GroupModel? = null
    lateinit var activityChatBinding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppThemeCreateGroup)
        activityChatBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat)

        activityChatBinding.btnSend.setOnClickListener(this)
        activityChatBinding.ivBack.setOnClickListener(this)
        activityChatBinding.ivProfile.setOnClickListener(this)
        activityChatBinding.addImages.setOnClickListener(this)

        database = FirebaseDatabase.getInstance()

        if (intent.hasExtra("group")) {
            isGroupChat = true
            group = intent.getSerializableExtra("group") as GroupModel
            activityChatBinding.tvTitle.text = group?.groupname
            user = intent.getSerializableExtra("user") as User?
            activityChatBinding.ivProfile.setImageURI(group?.profile)
        } else {
            user = intent.getSerializableExtra("user") as User?
            activityChatBinding.tvTitle.text = user?.displayname
            activityChatBinding.ivProfile.setImageURI(user?.profile)
        }

        loggedUserId = intent.getStringExtra("loggedUserId")
        getData()
    }

    fun setImagePopup(p0: String) {
        var imagePopup: ImagePopup = ImagePopup(this)
//            imagePopup.windowHeight = 800
//            imagePopup.windowWidth = 800
        imagePopup.setFullScreen(true)
        imagePopup.backgroundColor = Color.BLACK
        imagePopup.setImageOnClickClose(true)
        imagePopup.isHideCloseIcon = false
        imagePopup.initiatePopupWithGlide(p0)

//        imagePopup.initiatePopup(p0.drawable)

        imagePopup.viewPopup()
    }

    private fun setAdapter() {
        if (adapter == null) {
            activityChatBinding.rvMessage.layoutManager = LinearLayoutManager(this)
            adapter = ChatAdapter(this, messageList!!, loggedUserId!!, isGroupChat)
            activityChatBinding.rvMessage.adapter = adapter

        } else {
            adapter?.notifyDataSetChanged()
        }

        activityChatBinding.rvMessage.scrollToPosition(messageList?.size!! - 1)
    }

    private fun getData() {
        if (isGroupChat) {
            FirebaseFirestore.getInstance().collection("message")
                .document(group?.groupKey!!)
                .collection(group?.groupKey!!).get().addOnSuccessListener {
                    activityChatBinding.progress.visibility = View.GONE
                    messageList?.clear()
                    for (messageData in it) {
                        val userField = messageData.get("user") as Map<String, Any>
                        var user = User(
                            userField["uid"] as String,
                            userField["displayname"] as String,
                            userField["email"] as String,
                            userField["selected"] as Boolean,
                            userField["profile"] as String,

                        )
                        var message: Message? = Message(
                            messageData.get("message") as String,
                            messageData.get("senderId") as String,
                            messageData.get("receiverId") as String,
                            messageData.get("timestamp") as Long,
                            messageData.get("image") as String,
                            user
                        )
                        messageList?.add(message!!)
                    }
                }
        } else {
            FirebaseFirestore.getInstance().collection("message")
                .document(getMessageId(loggedUserId!!, user?.uid!!))
                .collection(getMessageId(loggedUserId!!, user?.uid!!)).get().addOnSuccessListener {
                    activityChatBinding.progress.visibility = View.GONE
                    messageList?.clear()
                    for (messageData in it) {
                        val userField = messageData.get("user") as Map<String, Any>
                        var user = User(
                            userField["uid"] as String,
                            userField["displayname"] as String,
                            userField["email"] as String,
                            userField["selected"] as Boolean,
                            userField["profile"] as String,)

                        var message: Message? = Message(
                            messageData.get("message") as String,
                            messageData.get("senderId") as String,
                            messageData.get("receiverId") as String,
                            messageData.get("timestamp") as Long,
                            messageData.get("image") as String,
                            user)
                        messageList?.add(message!!)
                    }
                }


        }
        val sortedList = messageList!!.sortedBy { it.timestamp }
        val sortedListDescending = sortedList!!.sortedByDescending { it.timestamp }
        messageList!!.clear()
        messageList!!.addAll(sortedListDescending)

        setAdapter()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)

    }

    private fun getMessageId(uid1: String, uid2: String): String {
        if (uid1 > uid2) {
            return uid1 + uid2
        } else {
            return uid2 + uid1
        }
    }

    private fun sendMessage(imageUrl:String) {
        var message: Message?
        var messageText = ""
        var imageUrlString = ""
        if(imageUrl.isEmpty()){
            messageText = activityChatBinding.etMessage.text.toString().trim()
        }
        else{
            imageUrlString = imageUrl
        }
        if (isGroupChat) {
            message = Message(
                messageText,
                loggedUserId!!,
                "", System.currentTimeMillis(), imageUrlString, user!!
            )
            FirebaseFirestore.getInstance().collection("message").document(group?.groupKey!!)
                .collection(group?.groupKey!!).document(
                    System.currentTimeMillis()
                        .toString()
                ).set(message).addOnSuccessListener {

                }
        } else {
            message = Message(
                messageText,
                loggedUserId!!,
                user?.uid!!,
                System.currentTimeMillis(), imageUrlString, user!!
            )
            FirebaseFirestore.getInstance().collection("message")
                .document(getMessageId(loggedUserId!!, user?.uid!!))
                .collection(getMessageId(loggedUserId!!, user?.uid!!)).document(
                    System.currentTimeMillis()
                        .toString()
                ).set(message).addOnSuccessListener {

                }

            messageList!!.add(message)
            adapter!!.notifyDataSetChanged()
        }
        activityChatBinding.etMessage.setText("")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSend -> {
                if (!TextUtils.isEmpty(activityChatBinding.etMessage.text.toString().trim())) {
                    sendMessage("")
                } else {
                    Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.ivBack -> finish()
            R.id.ivProfile -> if (isGroupChat) setImagePopup(group?.profile!!) else setImagePopup(
                user?.profile!!
            )

            R.id.addImages -> {
                chooseImageIntent()
            }
        }
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
                val key = FirebaseDatabase.getInstance().reference.child("message").push().key
                val groupKey =
                    FirebaseDatabase.getInstance().reference.child("message").child(key!!).push()
                        .key

                var fireStorageReference: StorageReference =
                    FirebaseStorage.getInstance().reference
                var file = File(filePath.toString())
                var storageRef = fireStorageReference.child("images/" + groupKey + file.name)
                storageRef.putFile(filePath!!)
                    .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                        override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                            storageRef.downloadUrl.addOnSuccessListener { uri ->
                                sendMessage(uri.toString())
                            }
                        }
                    }).addOnFailureListener(object : OnFailureListener {
                        override fun onFailure(p0: java.lang.Exception) {

                        }

                    })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}



