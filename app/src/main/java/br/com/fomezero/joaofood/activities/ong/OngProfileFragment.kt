package br.com.fomezero.joaofood.activities.ong

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import br.com.fomezero.joaofood.R
import br.com.fomezero.joaofood.activities.ActiveUserData
import br.com.fomezero.joaofood.activities.LoginActivity
import br.com.fomezero.joaofood.util.loadImage
import kotlinx.android.synthetic.main.fragment_ong_profile.*


class OngProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ong_profile, container, false)
    }

    override fun onStart() {
        super.onStart()
        accountExitButton.setOnClickListener {
            ActiveUserData.signOut()
            val loginIntent = Intent(activity, LoginActivity::class.java)
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(loginIntent)
            activity?.finish()
        }

        profileName.text = ActiveUserData.data?.getString("name") ?: "Unknown"
        ownerName.text = ActiveUserData.data?.getString("ownerName")?:"Unknown"
        acc_email.text = ActiveUserData.data?.getString("email")?:"Unknown"
        acc_phone.text = ActiveUserData.data?.getString("phoneNumber")?:"Unknown"
        siteUrl.text = ActiveUserData.data?.getString("siteUrl")?:"Unknown"


//        profilePicture.loadImage(ActiveUserData.data?.getString("image").toString()?:"https://i.imgur.com/SyjPj3f.png",  CircularProgressDrawable(context!!));
//        Log.d("Ashwith",ActiveUserData.data?.getString("image").toString())
//        ActiveUserData.data?.getString("image")?.let { imageUrl ->
//            context?.let{
//                profilePicture.loadImage(imageUrl,  CircularProgressDrawable(context!!))
//            }
//        }
        if(ActiveUserData.data?.getString("imageURL") == null){
            profilePicture.loadImage("https://foodforalladmin.netlify.app/assets/food_logo.png",CircularProgressDrawable(activity!!));
        }
        else{
        profilePicture.loadImage(ActiveUserData.data?.getString("imageURL"),CircularProgressDrawable(activity!!));
        }

//        if(ActiveUserData.data?.getString("name")?.toString() == "Care Foundation"){
//            profilePicture.loadImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSlZ-MT4dbx2DMIV33zW8PCLRIxJgIBJvHYFA&usqp=CAU",CircularProgressDrawable(activity!!));
//        }
//        else if(ActiveUserData.data?.getString("name")?.toString() == "Spoon"){
//            profilePicture.loadImage("https://media-exp1.licdn.com/dms/image/C560BAQGaQIPt1OBsMQ/company-logo_400_400/0/1582840078342?e=1651708800&v=beta&t=JVFygzGTbH8ks4WUuimAVo9bhjusOuyWpvhwjJxBtAA",CircularProgressDrawable(activity!!));
//        }
//        else if(ActiveUserData.data?.getString("name")?.toString() == "Akshay Patra"){
//            profilePicture.loadImage("https://www.akshayapatrausa.org/wp-content/uploads/2019/04/ffe-logo7.png",CircularProgressDrawable(activity!!));
//        }


    }
}