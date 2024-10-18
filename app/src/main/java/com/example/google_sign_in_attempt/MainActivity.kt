package com.example.google_sign_in_attempt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.google_sign_in_attempt.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


class MainActivity : AppCompatActivity() {
    private lateinit var gso : GoogleSignInOptions
    private lateinit var gsc : GoogleSignInClient

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("126804112970-tv50779stfmp3oaqj9sah18bdi180v7d.apps.googleusercontent.com")
            .requestEmail()
            .build()


        gsc = GoogleSignIn.getClient(this,gso)

        binding.googleBtn.setOnClickListener{
            signIn()
        }

    }

    private fun signIn() {
        val intent : Intent = gsc.getSignInIntent();
        startActivityForResult(intent,1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1000){
            var task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try{
                task.getResult(ApiException::class.java)
                goToSecondActivity()
            }
            catch(e : ApiException){
                Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()
                Log.d("MainActivity", "ResultCode: $resultCode, Data: $data")

            }
        }
    }

    private fun goToSecondActivity() {
        var intent = Intent(this,SecondActivity::class.java)
        startActivity(intent)
        finish()
    }
}