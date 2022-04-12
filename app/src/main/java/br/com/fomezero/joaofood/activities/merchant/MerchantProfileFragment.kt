package br.com.fomezero.joaofood.activities.merchant

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import br.com.fomezero.joaofood.R
import br.com.fomezero.joaofood.activities.ActiveUserData
import br.com.fomezero.joaofood.activities.LoginActivity
import br.com.fomezero.joaofood.util.loadImage
import kotlinx.android.synthetic.main.card_list.view.*
import kotlinx.android.synthetic.main.fragment_merchant_perfil.*
import kotlinx.android.synthetic.main.fragment_merchant_perfil.acc_email
import kotlinx.android.synthetic.main.fragment_merchant_perfil.acc_phone
import kotlinx.android.synthetic.main.fragment_merchant_perfil.accountExitButton
import kotlinx.android.synthetic.main.fragment_merchant_perfil.profileName
import kotlinx.android.synthetic.main.fragment_merchant_perfil.profilePicture
import kotlinx.android.synthetic.main.fragment_ong_profile.*


class MerchantProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_merchant_perfil, container, false)
    }

    override fun onStart() {
        super.onStart()
        accountExitButton.setOnClickListener {
            logout()
        }
        val merchantData = ActiveUserData.data
        val name = merchantData?.getString("name").toString()
        val profilename = name.substringBefore(" ")

        profileName.text = profilename

        acc_name.text = merchantData?.getString("name")
        acc_phone.text = merchantData?.getString("phoneNumber")
        acc_email.text = merchantData?.getString("email")
//        Log.d("Ashwith",merchantData?.getString("imageUrl").toString())
//        ActiveUserData.data?.getString("image")?.let { imageUrl ->
//            context?.let{
//                profilePicture.loadImage(imageUrl, CircularProgressDrawable(activity!!))
//            }
//        }

//        Log.d("AshImg", merchantData?.getString("image").toString())
       profilePicture.loadImage(merchantData?.getString("imageProf"), CircularProgressDrawable(activity!!))
        //profilePicture.loadImage(merchantData?.getString("image"), CircularProgressDrawable(activity!!))"https://images.pexels.com/photos/2379004/pexels-photo-2379004.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"
    }

    private fun logout() {
        ActiveUserData.signOut()
        val loginIntent = Intent(activity, LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(loginIntent)
        activity?.finish()
    }
}