package br.com.fomezero.joaofood.activities.merchant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import br.com.fomezero.joaofood.R
import br.com.fomezero.joaofood.activities.ActiveUserData
import br.com.fomezero.joaofood.activities.merchant.OngInfoFragment.Companion.PLACEHOLDER_ADDRESS
import br.com.fomezero.joaofood.util.loadImage
import br.com.fomezero.joaofood.model.OngData
import kotlinx.android.synthetic.main.fragment_ong_info.*
import kotlinx.android.synthetic.main.fragment_ong_info.profileName
import kotlinx.android.synthetic.main.fragment_ong_info.profilePicture
import kotlinx.android.synthetic.main.fragment_ong_profile.*


class OngInfoFragment(val ongData: OngData) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ong_info, container, false)
    }

    override fun onStart() {
        super.onStart()

        if(ongData.isApproved == true){
//            approval.text = "This NGO is approved"
             profileName.text = ongData.name+" ✔️"
//            approval.setTextColor(Color.parseColor("#00ff00"))
        }
        else{
             profileName.text = ongData.name+" ❌"
//            approval.text = "This NGO is not approved"
//            approval.setTextColor(Color.parseColor("#ff0000"))
        }
//        profilePicture.loadImage(ongData.imageUrl, CircularProgressDrawable(activity!!))
        ongData.description?.let {
            description.text = getString(R.string.about_text, ongData.siteUrl)
        }
        profilePicture.loadImage(ongData.imageUrl.toString(),CircularProgressDrawable(activity!!));
//        if(ongData.name == "Care Foundation"){
//            profilePicture.loadImage("https://img.freepik.com/free-vector/family-care-foundation-logo-vector_23987-272.jpg",CircularProgressDrawable(activity!!));
//        }
//        else if(ongData.name == "Spoon"){
//            profilePicture.loadImage("https://media-exp1.licdn.com/dms/image/C560BAQGaQIPt1OBsMQ/company-logo_400_400/0/1582840078342?e=1651708800&v=beta&t=JVFygzGTbH8ks4WUuimAVo9bhjusOuyWpvhwjJxBtAA",CircularProgressDrawable(activity!!));
//        }
//        else if(ongData.name == "Akshay Patra"){
//            profilePicture.loadImage("https://yt3.ggpht.com/ytc/AKedOLTgX2kZJcECRZp7ZbAoOnQ4yHuhRAvN29_flCpfnQ=s900-c-k-c0x00ffffff-no-rj",CircularProgressDrawable(activity!!));
//        }
//        else{
//            profilePicture.loadImage("https://thenew.org/app/uploads/2019/06/org-blog-genric-1.jpg",CircularProgressDrawable(activity!!));
//        }

        // TODO: get address from database
        address.text = getString(R.string.address_text, ongData.siteUrl)

        phoneNumber.text = getString(R.string.phone_number, ongData.phoneNumber)
        description.text = "Email :"+ongData.description
        // TODO: get helped number from database
        peopleHelped.text = getString(R.string.people_helped, 75000)

        mapsButton.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://"+ongData.siteUrl))
            startActivity(browserIntent)
//            val gmmIntentUri =
//                Uri.parse("geo:0,0?q=" + Uri.encode("Shivaji Chowk, 416112"))
//
//            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//            mapIntent.setPackage("com.google.android.apps.maps")
//            startActivity(mapIntent)
        }

        callButton.setOnClickListener {
            if (Build.VERSION.SDK_INT < 23) {
                phoneCall()
            } else {
                if (ActivityCompat.checkSelfPermission(
                        activity!!,
                        Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    phoneCall()
                } else {
                    val permissionStorage = arrayOf<String>(Manifest.permission.CALL_PHONE)
                    //Asking request Permissions
                    ActivityCompat.requestPermissions(activity!!, permissionStorage, 9)
                }
            }

        }

    }

    private fun phoneCall() {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ongData.phoneNumber))
        startActivity(intent)
    }

    companion object {
        const val PLACEHOLDER_ADDRESS = "177A Bleecker Street, New York City, NY 10012-1406"
    }
}