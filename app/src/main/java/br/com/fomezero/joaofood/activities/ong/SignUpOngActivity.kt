package br.com.fomezero.joaofood.activities.ong

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.DialogInterface
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import br.com.fomezero.joaofood.R
import br.com.fomezero.joaofood.activities.LoginActivity
import br.com.fomezero.joaofood.activities.WelcomeNewUserActivity
import br.com.fomezero.joaofood.modules.img.domain.api.UploadImageProvider
import br.com.fomezero.joaofood.modules.img.domain.model.ImgResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up_merchant.*
import kotlinx.android.synthetic.main.activity_sign_up_ong.cnpjField
import kotlinx.android.synthetic.main.activity_sign_up_ong.completeNameField
import kotlinx.android.synthetic.main.activity_sign_up_ong.emailField
import kotlinx.android.synthetic.main.activity_sign_up_ong.ongNameField
import kotlinx.android.synthetic.main.activity_sign_up_ong.passwordConfirmationField
import kotlinx.android.synthetic.main.activity_sign_up_ong.passwordField
import kotlinx.android.synthetic.main.activity_sign_up_ong.phoneNumberField
import kotlinx.android.synthetic.main.activity_sign_up_ong.signUpButton
import kotlinx.android.synthetic.main.activity_sign_up_ong.websiteField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

class SignUpOngActivity : AppCompatActivity(), View.OnClickListener {
    private val db: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_ong)


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

    override fun onBackPressed() {
        super.onBackPressed()
        val welcomeNewUserIntent = Intent(this, WelcomeNewUserActivity::class.java)
        startActivity(welcomeNewUserIntent)
        finish()
    }

    private fun takePhoto() {
//        val optionsMenu = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Exit")
//        val builder = AlertDialog.Builder(this)
//
//        builder.setItems(
//            optionsMenu
//        ) { dialogInterface, i ->
//            if (optionsMenu[i].equals("Take Photo")) {
//                // Open the camera and get the photo
//                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                startActivityForResult(takePictureIntent, SignUpOngActivity.CAMERA_REQUEST)
//            } else if (optionsMenu[i].equals("Choose from Gallery")) {
//                // choose from  external storage
//                val takePictureIntent = Intent(
//                    Intent.ACTION_PICK,
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                )
//                startActivityForResult(takePictureIntent, SignUpOngActivity.CAMERA_REQUEST)
//            } else if (optionsMenu[i].equals("Exit")) {
//                dialogInterface.dismiss()
//            }
//
//        }
//        builder.show()

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, SignUpOngActivity.CAMERA_REQUEST)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Camera is unavailable", Toast.LENGTH_LONG).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SignUpOngActivity.CAMERA_REQUEST && resultCode == RESULT_OK) {
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

        if(completeNameField.text.toString().isEmpty() && ongNameField.text.toString().isEmpty() &&
            websiteField.text.toString().isEmpty() && emailField.text.toString().isEmpty() && cnpjField.text.toString().isEmpty()
            && phoneNumberField.text.toString().isEmpty()){
            val msg = Toast.makeText(
                applicationContext,
                "Please fill in all fields.",
                Toast.LENGTH_LONG
            )
            msg.setGravity(Gravity.CENTER, 0, 400)
            msg.show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    runOnUiThread {
                        Toast.makeText(baseContext, "Successful Sign Up!.", Toast.LENGTH_SHORT).show()
                        val welcomeNewUserIntent = Intent(this, LoginActivity::class.java)
                        startActivity(welcomeNewUserIntent)
                        finish()
                    }
                    val user = auth.currentUser
                    saveDataToFirestore()
//                    updateUI(user)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    runOnUiThread {
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
//                    updateUI(null)
                }
            }

    }

    private fun saveDataToFirestore() {
        val ongData = hashMapOf(
            "ownerName" to completeNameField.text.toString(),
            "name" to ongNameField.text.toString(),
            "siteUrl" to websiteField.text.toString(),
            "email" to emailField.text.toString(),
            "cnpj" to cnpjField.text.toString(),
            "phoneNumber" to phoneNumberField.text.toString(),
            "isApproved" to false,
        )
        db.collection("ongs")
            .add(ongData)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                val userData = hashMapOf(
                    "email" to emailField.text.toString(),
                    "type" to "ong",
                    "data" to documentReference
                )

                GlobalScope.launch(Dispatchers.IO) {
                    val imgResult = UploadImageProvider.uploadFile(profilepic.drawable.toBitmap(), documentReference.id)

                    if (imgResult is ImgResult.Success) {
                        val url = imgResult.reponse?.data?.link
                        val map = HashMap<String, Any>()
                        val array = arrayOf(url)
                        val imageUrl = url;
                        map["image"] = Arrays.asList(*array)
//                        documentReference.update(map)
                        documentReference.update("imageURL", imageUrl)
                    }


                }

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
                                val imageUrl = url;
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

    private fun validatePassword(): Boolean = passwordField.text.toString() == passwordConfirmationField.text.toString()

    companion object {
        private const val TAG = "SignUpMerchantActivity"
        private const val CAMERA_REQUEST = 1888
    }
}