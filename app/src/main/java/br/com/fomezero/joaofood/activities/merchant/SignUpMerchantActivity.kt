package br.com.fomezero.joaofood.activities.merchant

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.children
import br.com.fomezero.joaofood.R
import br.com.fomezero.joaofood.activities.LoginActivity
import br.com.fomezero.joaofood.activities.WelcomeNewUserActivity
import br.com.fomezero.joaofood.modules.img.domain.api.UploadImageProvider
import br.com.fomezero.joaofood.modules.img.domain.model.ImgResult
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_new_food.*
import kotlinx.android.synthetic.main.activity_sign_up_merchant.*
import kotlinx.android.synthetic.main.activity_sign_up_merchant.addFoodButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

class SignUpMerchantActivity : AppCompatActivity(), View.OnClickListener {
    private val db: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_merchant)



        auth = Firebase.auth
        signUpButton.setOnClickListener(this)

        addFoodButton.setOnClickListener {
            if (Build.VERSION.SDK_INT < 23) {
                takePhoto()
            } else {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    takePhoto()
                } else {
                    val permissionStorage = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    //Asking request Permissions
                    ActivityCompat.requestPermissions(this, permissionStorage, 9)
                }
            }
        }


    }

    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent,CAMERA_REQUEST)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Camera is unavailable", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SignUpMerchantActivity.CAMERA_REQUEST && resultCode == RESULT_OK) {
            val photo = data?.extras?.get("data") as Bitmap?
            profilepic.setImageBitmap(photo)
            profilepic.visibility = View.VISIBLE
            // TODO: Send photo do imgur and send url to database
//            val imageUri = photo?.let { getImageUri(this, it) }
        }
    }

    override fun onClick(v: View?) {
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        val fields = textFields.children

        if(completeNameField.text.toString().isEmpty() && emailField.text.toString().isEmpty() &&
            phoneNumberField.text.toString().isEmpty() && addressfield.text.toString().isEmpty()){
            val msg = Toast.makeText(
                applicationContext,
                "Please fill in all fields.",
                Toast.LENGTH_LONG
            )
            msg.setGravity(Gravity.CENTER, 0, 400)
            msg.show()
            return
        }
        for (field in fields) {
            if (field is TextInputEditText && field.text.toString().isEmpty()) {
                val msg = Toast.makeText(
                    applicationContext,
                    "Please fill in all fields.",
                    Toast.LENGTH_LONG
                )
                msg.setGravity(Gravity.CENTER, 0, 400)
                msg.show()
                return
            }
        }

        if (validatePassword().not()) {
            val msg =
                Toast.makeText(applicationContext, "Passwords are different.", Toast.LENGTH_LONG)
            msg.setGravity(Gravity.CENTER, 0, 400)
            msg.show()
            return
        }


        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    runOnUiThread {
                        Toast.makeText(baseContext, "Successful Sign Up!.", Toast.LENGTH_SHORT).show()
                        val welcomeNewUserIntent = Intent(this, LoginActivity::class.java)
                        startActivity(welcomeNewUserIntent)
                        finish()

                    }
                    saveDataToFirestore()
                    val user = auth.currentUser
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    runOnUiThread {
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT)
                            .show()
                    }
//                    updateUI(null)

                }
            }

    }



    private fun saveDataToFirestore() {
        val imgResult = UploadImageProvider.uploadFile(profilepic.drawable.toBitmap(), "img123")
        var url = ""
        if (imgResult is ImgResult.Success) {
            url = imgResult.reponse?.data?.link.toString()
        }
        val ongData = hashMapOf(
            "name" to completeNameField.text.toString(),
            "email" to emailField.text.toString(),
            "phoneNumber" to phoneNumberField.text.toString(),
            "address" to addressfield.text.toString(),
            "imageProf" to url
        )
        db.collection("merchants")
            .add(ongData)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")


                val userData = hashMapOf(
                    "email" to emailField.text.toString(),
                    "type" to "merchant",
                    "data" to documentReference
                )
                db.collection("users")
                    .add(userData)
                    .addOnSuccessListener { usersDocumentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${usersDocumentReference.id}")
                        GlobalScope.launch(Dispatchers.IO) {
                            val imgResult = UploadImageProvider.uploadFile(profilepic.drawable.toBitmap(), usersDocumentReference.id)

                            if (imgResult is ImgResult.Success) {
                                val url = imgResult.reponse?.data?.link
                                val map = HashMap<String, Any>()
                                val array = arrayOf(url)
                                map["image"] = Arrays.asList(*array)
                                usersDocumentReference.update(map)
                            }

                        }


                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }


    private fun validatePassword(): Boolean =
        passwordField.text.toString() == passwordConfirmationField.text.toString()

    override fun onBackPressed() {
        super.onBackPressed()
        val welcomeNewUserIntent = Intent(this, WelcomeNewUserActivity::class.java)
        startActivity(welcomeNewUserIntent)
        finish()
    }

    companion object {
        private const val TAG = "SignUpMerchantActivity"
        private const val CAMERA_REQUEST = 1888
    }
}