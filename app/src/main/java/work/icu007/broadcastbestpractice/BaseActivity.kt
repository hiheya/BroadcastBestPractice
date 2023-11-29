package work.icu007.broadcastbestpractice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


/*
 * Author: Charlie_Liao
 * Time: 2023/11/23-17:40
 * E-mail: rookie_l@icu007.work
 */

open class BaseActivity : AppCompatActivity() {
    lateinit var receiver: ForceOfflineReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction("work.icu007.broadcastbestpractice.FORCE_OFFLINE")
        receiver = ForceOfflineReceiver()
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    inner class ForceOfflineReceiver : BroadcastReceiver(){
        
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "onReceive: receive broadcast context == null? ${context == null}")
            if (context != null) {
                AlertDialog.Builder(context).apply {
                    setTitle("Warning")
                    setMessage("U are forced to be offline. please try to login again.")
                    setCancelable(false)
                    setPositiveButton("OK"){_,_ ->
                        ActivityCollector.finishAll()
                        val i = Intent(context,LoginActivity::class.java)
                        context.startActivity(i)
                    }
                    show()
                }
            }
        }

    }
    companion object{
        const val TAG = "BaseActivity"
    }
}