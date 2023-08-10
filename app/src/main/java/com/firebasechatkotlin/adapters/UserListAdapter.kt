package com.firebasechatkotlin.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.facebook.drawee.view.SimpleDraweeView
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
                listener?.onItemClick(p0.llMain, p1, "users")
            }
        }
        p0.ivProfile.setImageURI(user.profile)

        p0.ivProfile.setOnClickListener {
            setImagePopup(user.profile!!)
        }


    }

    fun setImagePopup(p0: String) {
        var imagePopup: ImagePopup = ImagePopup(context)
        imagePopup.setFullScreen(true)
        imagePopup.backgroundColor = Color.BLACK
        imagePopup.setImageOnClickClose(true)
        imagePopup.isHideCloseIcon = false
        imagePopup.initiatePopupWithGlide(p0)
        imagePopup.viewPopup()
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName : AppCompatTextView = view.findViewById(R.id.tvName)
        val tvEmailId:AppCompatTextView = view.findViewById(R.id.tvEmailId)
        val llMain: LinearLayout = view.findViewById(R.id.llMain)
        val ivProfile: SimpleDraweeView = view.findViewById(R.id.ivProfile)
    }

    public fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }


}