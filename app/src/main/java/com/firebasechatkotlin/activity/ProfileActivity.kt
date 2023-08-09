package com.firebasechatkotlin.activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.firebasechatkotlin.R
import com.firebasechatkotlin.databinding.ActivityProfileBinding
import com.firebasechatkotlin.models.User

class ProfileActivity : AppCompatActivity() {
    var TAG: String = javaClass.simpleName

    var user: User? = null
    lateinit var context: Context
    lateinit var activityProfileBinding : ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProfileBinding = DataBindingUtil.setContentView(this,R.layout.activity_profile)
        setTheme(R.style.AppTheme)

        context = this@ProfileActivity
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        user = intent.getSerializableExtra("user") as User

        supportActionBar?.title = "Profile"

        activityProfileBinding.tvDisplayName.text = user?.displayname
        activityProfileBinding.tvDisplayEmail.text = user?.email
        activityProfileBinding.ivProfile.setImageURI(user?.profile)

        activityProfileBinding.ivProfile.setOnClickListener { setImagePopup(user?.profile!!) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
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
}