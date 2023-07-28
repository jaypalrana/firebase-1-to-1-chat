package com.firebasechatkotlin.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import com.firebasechatkotlin.R
import com.firebasechatkotlin.adapters.UserListAdapter
import com.firebasechatkotlin.databinding.ActivityChatUserListBinding
import com.firebasechatkotlin.listeners.OnItemClickListener
import com.firebasechatkotlin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ChatUserListActivity : AppCompatActivity(), OnItemClickListener {

    lateinit var activityChatUserListBinding : ActivityChatUserListBinding
    private var loggedUser: FirebaseUser? = null
    private var userList: ArrayList<User>? = ArrayList()
    private var adapter: UserListAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_chat_user_list)
        activityChatUserListBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_chat_user_list
        )

        setTitle(R.string.chat_user_title)

        loggedUser = FirebaseAuth.getInstance().currentUser

        getData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 0, 0, "About us")
        menu?.add(0, 1, 0, "Logout")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            1 -> logOut()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun logOut() {

        FirebaseAuth.getInstance().signOut()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun getData() {

        val ref = FirebaseDatabase.getInstance().reference
        val query = ref.child("users")

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                activityChatUserListBinding.progress.visibility = View.GONE
            }

            override fun onDataChange(p0: DataSnapshot) {
                activityChatUserListBinding.progress.visibility = View.GONE
                userList?.clear()
                for (data in p0.children) {
                    if (data.child("uid").value as String != loggedUser?.uid) {
                        var user: User = User(
                            data.child("uid").value as String,
                            data.child("displayname").value as String,
                            data.child("email").value as String
                        )
                        userList?.add(user)
                    }

                }
                setAdapter()
            }


        })
    }

    private fun setAdapter() {
        if (adapter == null) {
            adapter = UserListAdapter(this, userList!!)
            adapter?.setOnItemClickListener(this)
            activityChatUserListBinding.rvUserList.layoutManager =
                LinearLayoutManager(this)
            activityChatUserListBinding.rvUserList.adapter = adapter
        } else {
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onItemClick(view: View, position: Int) {
        when (view.id) {
            R.id.llMain -> {
                ChatActivity.startActivity(this, userList?.get(position)!!, loggedUser?.uid!!)
            }
        }
    }
}
