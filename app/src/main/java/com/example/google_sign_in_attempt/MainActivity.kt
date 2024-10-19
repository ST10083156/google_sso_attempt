package com.example.google_sign_in_attempt

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.google_sign_in_attempt.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task




class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 1001
    }
    private lateinit var gso : GoogleSignInOptions
    private lateinit var gsc : GoogleSignInClient
    private lateinit var binding : ActivityMainBinding
    private lateinit var builder : NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "loginChannel"
            val channelName = "User Registration"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for user registration notifications"
            }


            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }

         builder = NotificationCompat.Builder(this, "loginChannel")
            .setContentTitle("User Registered")
            .setContentText("A new user has successfully registered.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)



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
                sendNotification()
                Handler(Looper.getMainLooper()).postDelayed({
                    goToSecondActivity()
                }, 2000)

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

    private fun sendNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if permission is granted
            if (ContextCompat.checkSelfPermission(
                    this,
                    "android.permission.POST_NOTIFICATIONS"
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission granted, send the notification
                notificationManager.notify(1001, builder.build())
            } else {
                // Request permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf("android.permission.POST_NOTIFICATIONS"),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        } else {
            // No need to check for permission below Android 13
            notificationManager.notify(1001, builder.build())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send the notification
                notificationManager.notify(1001, builder.build())
            } else {
                // Permission denied
                Toast.makeText(this, "Notification permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }





}