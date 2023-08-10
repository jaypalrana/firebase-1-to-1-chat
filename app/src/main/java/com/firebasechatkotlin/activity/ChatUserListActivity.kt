package com.firebasechatkotlin.activity

import android.R.attr.data
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebasechatkotlin.R
import com.firebasechatkotlin.adapters.GroupListAdapter
import com.firebasechatkotlin.adapters.UserListAdapter
import com.firebasechatkotlin.databinding.ActivityChatUserListBinding
import com.firebasechatkotlin.listeners.OnItemClickListener
import com.firebasechatkotlin.models.GroupModel
import com.firebasechatkotlin.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class ChatUserListActivity : AppCompatActivity(), OnItemClickListener {


    private var loggedUser: FirebaseUser? = null
    private var userList: ArrayList<User>? = ArrayList()
    private var groupList: ArrayList<GroupModel>? = ArrayList()
    private var adapter: UserListAdapter? = null
    private var groupsAdapter: GroupListAdapter? = null
    private var user: User? = null
    lateinit var activityChatUserListBinding: ActivityChatUserListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityChatUserListBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_chat_user_list)
        setTitle(R.string.chat_user_title)

        loggedUser = FirebaseAuth.getInstance().currentUser

        //getData()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 0, 0, "Create Group")
        menu?.add(0, 1, 0, "Profile")
        menu?.add(0, 2, 0, "Logout")
        menu?.add(0, 3, 0, "Delete Account")
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            0 -> startActivityForResult(Intent(this, CreateGroupActivity::class.java), 11)

            2 -> logOut()

            1 -> user.let {
                startActivity(
                    Intent(this, ProfileActivity::class.java).putExtra(
                        "user",
                        user
                    )
                )
            }

            3 -> {
                startActivity(
                    Intent(this, ActivityDeleteAccount::class.java).putExtra(
                        "user",
                        user
                    )
                )
            }
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

        val ref = FirebaseFirestore.getInstance()
        val usersQuery = ref.collection("users")
        val groupsQuery = ref.collection("groups")

        usersQuery.get().addOnCompleteListener {
            activityChatUserListBinding.progress.visibility = View.GONE
            userList!!.clear()
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
                    } else
                        user = User(
                            data.get("uid") as String,
                            data.get("displayname") as String,
                            data.get("email") as String,
                            false,
                            data.get("profile") as String
                        )
                }
                setAdapter()
            } else {
                Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
            }
        }


        groupsQuery.get().addOnCompleteListener {
            activityChatUserListBinding.progress.visibility = View.GONE
            groupList!!.clear()
            if (it.isSuccessful) {
                var querySnapShot = it.getResult()
                var users: ArrayList<User> = ArrayList()
                for (documentSnapshot in querySnapShot) {
                    val dataList: List<Map<String?, String>> = documentSnapshot.data.get("users") as List<Map<String?, String>>

                    for (item in dataList) {
                        var userModel:User = User(
                            item["uid"]!!,
                            item["displayname"]!!,
                            item["email"]!!,
                            item["selected"]!! as Boolean,
                            item["profile"]!!,

                        )
                        users.add(userModel)
                    }

                    if (users.any { it -> it.uid == loggedUser?.uid }) {
                        var groupModel: GroupModel = GroupModel(
                            documentSnapshot.data.get("groupname") as String,
                            users,
                            documentSnapshot.data.get("groupKey") as String,
                            documentSnapshot.data.get("profile") as String
                        )
                        groupList?.add(groupModel)
                    }


                }
                setAdapterForGroups()
            }
        }
    }

    private fun setAdapterForGroups() {
        if (groupsAdapter == null) {
            groupsAdapter = GroupListAdapter(this, groupList!!)
            groupsAdapter?.setOnItemClickListener(this)
            activityChatUserListBinding.rvGroupsList.layoutManager = LinearLayoutManager(this)
            activityChatUserListBinding.rvGroupsList.adapter = groupsAdapter
        } else
            adapter?.notifyDataSetChanged()
    }

    private fun setAdapter() {
        if (adapter == null) {
            adapter = UserListAdapter(this, userList!!)
            adapter?.setOnItemClickListener(this)
            activityChatUserListBinding.rvUserList.layoutManager = LinearLayoutManager(this)
            activityChatUserListBinding.rvUserList.adapter = adapter
        } else {
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onItemClick(view: View, position: Int, key: String) {
        when (view.id) {
            R.id.llMain -> {
                if (key == "users")
                    ChatActivity.startActivity(this, userList?.get(position)!!, loggedUser?.uid!!)
                else if (key == "group") {
                    var intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("group", groupList?.get(position))
                    intent.putExtra("loggedUserId", loggedUser?.uid)
                    intent.putExtra("user", user)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getData()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }
}
