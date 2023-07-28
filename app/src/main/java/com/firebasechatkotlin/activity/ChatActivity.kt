package com.firebasechatkotlin.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.firebasechatkotlin.R
import com.firebasechatkotlin.adapters.ChatAdapter
import com.firebasechatkotlin.databinding.ActivityChatBinding
import com.firebasechatkotlin.models.Message
import com.firebasechatkotlin.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var activityChatBinding : ActivityChatBinding

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityChatBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        user = intent.getParcelableExtra("user")
        loggedUserId = intent.getStringExtra("loggedUserId")

        title = user?.displayname
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)


        database = FirebaseDatabase.getInstance()

        activityChatBinding.btnSend.setOnClickListener(this)

        getData()
    }

    private fun setAdapter() {
        if (adapter == null) {
            activityChatBinding.rvMessage.layoutManager =
                LinearLayoutManager(this)
            adapter = ChatAdapter(this, messageList!!, loggedUserId!!)
            activityChatBinding.rvMessage.adapter = adapter
        } else {
            adapter?.notifyDataSetChanged()
        }

        activityChatBinding.rvMessage.scrollToPosition(messageList?.size!! - 1)
    }

    private fun getData() {

        val query = database?.reference?.child("message")?.child(getMessageId(loggedUserId!!, user?.uid!!))

        query?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                activityChatBinding.progress.visibility = View.GONE
            }

            override fun onDataChange(p0: DataSnapshot) {
                activityChatBinding.progress.visibility = View.GONE
                messageList?.clear()
                for (data in p0.children) {
                    var message: Message? = Message(
                        data.child("message").value as String,
                        data.child("senderId").value as String,
                        data.child("receiverId").value as String,
                        data.child("timestamp").value as Long
                    )
                    messageList?.add(message!!)

                }

                setAdapter()
            }

        })
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

    private fun sendMessage() {
        val key = database?.reference?.child("message")?.push()?.key
        val firebase =
            database?.reference?.child("message")?.child(getMessageId(loggedUserId!!, user?.uid!!))?.child(key!!)

        firebase?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

        var message = Message(activityChatBinding.etMessage.text.toString().trim(), loggedUserId!!, user?.uid!!, System.currentTimeMillis())
        firebase?.setValue(message)

        activityChatBinding.etMessage.setText("")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSend -> {
                if (!TextUtils.isEmpty(activityChatBinding.etMessage.text.toString().trim())) {
                    sendMessage()
                } else {
                    Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
