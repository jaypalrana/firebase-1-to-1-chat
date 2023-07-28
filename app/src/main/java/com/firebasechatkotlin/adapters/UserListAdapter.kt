package com.firebasechatkotlin.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.firebasechatkotlin.R
import com.firebasechatkotlin.listeners.OnItemClickListener
import com.firebasechatkotlin.models.User

class UserListAdapter(val context: Context, val data: ArrayList<User>) :
    RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_user_list, p0, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        var user = data[p1]

        p0.tvName.text = user.displayname
        p0.tvEmailId.text = user.email
        p0.llMain.setOnClickListener {
            if (listener != null) {
                listener?.onItemClick(p0.llMain, p1)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById(R.id.tvName) as AppCompatTextView
        val tvEmailId = view.findViewById(R.id.tvEmailId) as AppCompatTextView
        val llMain = view.findViewById(R.id.llMain) as LinearLayout
    }

    public fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}