package work.icu007.broadcastbestpractice

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import work.icu007.broadcastbestpractice.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        activityMainBinding.btnForceOffline.setOnClickListener {
            val intent = Intent("work.icu007.broadcastbestpractice.FORCE_OFFLINE")
            Log.d(TAG, "onCreate: sendBroadcast")
            sendBroadcast(intent)
        }
    }
    companion object{
        const val TAG = "MainActivity"
    }
}