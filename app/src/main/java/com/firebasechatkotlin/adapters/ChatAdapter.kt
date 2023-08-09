package com.firebasechatkotlin.adapters

import android.content.Context
import android.graphics.Color
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.net.toUri
import androidx.core.os.persistableBundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.facebook.drawee.view.SimpleDraweeView
import com.firebasechatkotlin.R
import com.firebasechatkotlin.listeners.OnItemClickListener
import com.firebasechatkotlin.models.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class ChatAdapter(
    val context: Context,
    val data: ArrayList<Message>,
    val loggedUId: String,
    val isGroupChat: Boolean
) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null
    var dateList =ArrayList<String>()

    override fun onCreateViewHolder(p0: ViewGroup, type: Int): ViewHolder {
//        return if (type == 1) {
//            ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_others_message, p0, false))
//        } else {
//            ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_my_message, p0, false))
//        }
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_my_message, p0, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }
 var i=0;
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        var message = data[p1]
        var nextPosition = p1-1
        if(data.size != 2) {
            if (nextPosition > 0) {
                if (timestampToDate(message.timestamp) != timestampToDate(data[p1 - 1].timestamp)) {
                    p0.tvDateDetails.visibility = View.VISIBLE
                    p0.tvDateDetails.setText(timestampToDate(message.timestamp))
                    dateList.add(timestampToDate(message.timestamp))
                } else {
                    p0.tvDateDetails.visibility = View.GONE
                }
            }
        }

        if(p1==0){
            p0.tvDateDetails.visibility = View.VISIBLE
            p0.tvDateDetails.setText(timestampToDate(message.timestamp))
            dateList.add(timestampToDate(message.timestamp))
        }


        if (getItemViewType(p1) == 1) {
            p0.relLeft.visibility = View.VISIBLE
            p0.relRight.visibility = View.GONE
           // p0.tvLeftMessage.text = message.message
            if(message.message.isEmpty()){
               // p0.ivImageMessage.setImageURI(message.image.toUri())
                Glide.with(context)
                    .load(message.image.toUri())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)) // Caches the image
                    .into(p0.leftImageMessage)
                p0.tvLeftMessage.visibility = View.GONE
                p0.leftImageMessage.visibility = View.VISIBLE
            }
            else{
                p0.tvLeftMessage.text = message.message
                p0.leftImageMessage.visibility = View.GONE
                p0.tvLeftMessage.visibility = View.VISIBLE
            }
            p0.tvLeftName.text = message.user.displayname
            if (isGroupChat) {
                p0.ivLeftProfile.visibility = View.VISIBLE
                p0.tvLeftName.visibility = View.VISIBLE
                p0.tvLeftTimeDate.text = timestampToAmPmTime(message.timestamp)
                p0.ivLeftProfile.setImageURI(message.user.profile)
            } else {
                p0.ivLeftProfile.visibility = View.GONE
                p0.tvLeftName.visibility = View.GONE
                p0.tvLeftTimeDate.text = timestampToAmPmTime(message.timestamp)
            }
        } else {
            p0.relLeft.visibility = View.GONE
            p0.relRight.visibility = View.VISIBLE
            if(message.message.isEmpty()){
                Glide.with(context)
                    .load(message.image.toUri())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)) // Caches the image
                    .into(p0.rightImageMessage)
                p0.rightImageMessage.visibility = View.VISIBLE
                p0.tvRightMessage.visibility = View.GONE
            }
            else{
                p0.tvRightMessage.text = message.message
                p0.rightImageMessage.visibility = View.GONE
                p0.tvRightMessage.visibility = View.VISIBLE
            }
            p0.tvRightTimeDate.text = timestampToAmPmTime(message.timestamp)
            if (isGroupChat) {
                p0.ivRightProfile.visibility = View.VISIBLE
                p0.tvRightName.visibility = View.VISIBLE
                p0.ivRightProfile.setImageURI(message.user.profile)
                p0.tvRightName.text = message.user.displayname
            } else {
                p0.ivRightProfile.visibility = View.GONE
                p0.tvRightName.visibility = View.GONE
            }
        }

//        if (isGroupChat) {
//            p0.tvName.visibility = VISIBLE
//            p0.ivProfileImage.visibility = VISIBLE
//        } else {
//            p0.tvName.visibility = GONE
//           p0.ivProfileImage.visibility = GONE
//        }
        p0.ivRightProfile.setOnClickListener { setImagePopup(message.user.profile!!) }
        p0.ivLeftProfile.setOnClickListener { setImagePopup(message.user?.profile!!) }
        p0.itemView.setOnClickListener {

        }
    }

    fun setImagePopup(p0: String) {
        var imagePopup: ImagePopup = ImagePopup(context)
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

    override fun getItemViewType(position: Int): Int {
        return if (loggedUId.equals(data.get(position).senderId)) {
            0
        } else {
            1
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val relRight: RelativeLayout = view.findViewById(R.id.rel_right)
        val tvRightName: TextView = view.findViewById(R.id.rightTvName)
        val tvRightMessage:TextView = view.findViewById(R.id.rightTvMessage)
        val tvRightTimeDate:TextView = view.findViewById(R.id.rightTvTimeDate)
        val ivRightProfile: SimpleDraweeView = view.findViewById(R.id.rightIvProfileImage)
        val relLeft:RelativeLayout = view.findViewById(R.id.rel_left)
        val tvLeftName:TextView = view.findViewById(R.id.leftTvName)
        val tvLeftMessage:TextView = view.findViewById(R.id.leftTvMessage)
        val tvLeftTimeDate :TextView= view.findViewById(R.id.leftTvTimeDate)
        val ivLeftProfile:SimpleDraweeView = view.findViewById(R.id.leftIvProfileImage)
        val rightImageMessage : AppCompatImageView = view.findViewById(R.id.rightImageMsg)
        val leftImageMessage : AppCompatImageView = view.findViewById(R.id.leftImageMsg)
        val tvDateDetails : AppCompatTextView = view.findViewById(R.id.tvDateDetails)
//        val ivProfileImage = view.ivProfileImage
//        val tvName = view.tvName
    }

    public fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun getRelativeTimeString(timestamp: Long): String {
        Log.d("TAG", "getRelativeTimeString: "+timestamp)
        return if (System.currentTimeMillis() <= timestamp + 60000) {
            "Just Now"
        } else {
            DateUtils.getRelativeTimeSpanString(
                timestamp,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS
            ).toString()
        }
    }
    fun timestampToDate(timestamp: Long): String {
        // Convert the timestamp to a Date object
        val date = Date(timestamp)

        // Define the date format you want
        val sdf = SimpleDateFormat("dd-MMM-yyyy")
        sdf.timeZone = TimeZone.getDefault()

        // Format the Date object to a string
        return sdf.format(date)
    }

    fun timestampToAmPmTime(timestamp: Long): String {
        // Convert the timestamp to a Date object
        val date = Date(timestamp)

        // Define the time format with AM/PM indication
        val sdf = SimpleDateFormat("hh:mm a")
        sdf.timeZone = TimeZone.getDefault()

        // Format the Date object to a string
        return sdf.format(date)
    }

}