## 实践-实现强制下线功能

我们完全可以借刚刚学的广播知识，非常轻松地实现强制下线这一功能。

强制下线功能需要先关闭所有的Activity，然后回到登录界面。如果你的反应足够快，应该会想到我们在第3章的最佳实践部分已经实现过关闭所有Activity的功能了，因此这里使用同样的方案即可。先创建一个ActivityCollector类用于管理所有的Activity，代码如下所示：

```kotlin
package work.icu007.broadcastbestpractice

import android.app.Activity


/*
 * Author: Charlie_Liao
 * Time: 2023/11/23-17:35
 * E-mail: rookie_l@icu007.work
 * manage all activity
 */

object ActivityCollector {
    private val activites = ArrayList<Activity>()

    // add activities to ArrayList
    fun addActivity(activity: Activity){
        activites.add(activity)
    }

    // remove activities from ArrayList
    fun removeActivity(activity: Activity){
        activites.remove(activity)
    }

    // finish all activities
    fun finishAll(){
        for (activity in activites){
            if (!activity.isFinishing){
                activity.finish()
            }
        }
        activites.clear()
    }
}
```

然后创建BaseActivity类作为所有Activity的父类，代码如下所示：

```kotlin
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
            Log.d(TAG, "onReceive: receive broadcast context == null: ${context == null}")
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
```

首先需要创建一个LoginActivity来作为登录界面，登录界面需要一个横向的LinearLayout，用于输入账号信息；还需要一个横向的LinearLayout，用于输入密码信息；最后创建一个登录按钮。

最后监听登录button：

```kotlin
package work.icu007.broadcastbestpractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import work.icu007.broadcastbestpractice.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {
    lateinit var activityLoginBinding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(activityLoginBinding.root)

        activityLoginBinding.login.setOnClickListener {
            val account = activityLoginBinding.accountEdit.text.toString()
            val password = activityLoginBinding.passwordEdit.text.toString()
            if (account == "admin" && password == "123456"){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "account or password is invalid", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
```

首先将LoginActivity的继承结构改成继承自BaseActivity，然后在登录按钮的点击事件里对输入的账号和密码进行判断：如果账号是admin并且密码是123456，就认为登录成功并跳转到MainActivity，否则就提示用户账号或密码错误。

因此MainActivity就是登录成功后进入的程序主界面，我们只需要加入强制下线功能就可以了。修改activity_main.xml中的代码，如下所示：

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <Button
        android:id="@+id/btnForceOffline"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Send force offline broadcast"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

只有一个按钮用于触发强制下线功能。然后修改MainActivity中的代码监听下线button

```kotlin
package work.icu007.broadcastbestpractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import work.icu007.broadcastbestpractice.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    lateinit var activityMainBinding: ActivityMainBinding
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
```

我们在按钮的点击事件里发送了一条广播，广播的值为work.icu007.broadcastbestpractice.FORCE_OFFLINE，这条广播就是用于通知程序强制用户下线的。也就是说，强制用户下线的逻辑并不是写在MainActivity里的，而是应该写在接收这条广播的BroadcastReceiver里。这样强制下线的功能就不会依附于任何界面了，不管是在程序的任何地方，只要发出这样一条广播，就可以完成强制下线的操作了。那么毫无疑问，接下来我们就需要创建一个BroadcastReceiver来接收这条强制下线广播。唯一的问题就是，应该在哪里创建呢？由于BroadcastReceiver中需要弹出一个对话框来阻塞用户的正常操作，但如果创建的是一个静态注册的BroadcastReceiver，是没有办法在onReceive()方法里弹出对话框这样的UI控件的，而我们显然也不可能在每个Activity中都注册一个动态的BroadcastReceiver。

那么到底应该怎么办呢？答案其实很明显，只需要在BaseActivity中动态注册一个BroadcastReceiver就可以了

先来看一下ForceOfflineReceiver中的代码，这次onReceive()方法里可不再是仅仅弹出一个Toast了，而是加入了较多的代码，那我们就来仔细看看吧。首先是使用AlertDialog.Builder构建一个对话框。注意，这里一定要调用setCancelable()方法将对话框设为不可取消，否则用户按一下Back键就可以关闭对话框继续使用程序了。然后使用setPositiveButton()方法给对话框注册确定按钮，当用户点击了“OK”按钮时，就调用ActivityCollector的finishAll()方法销毁所有Activity，并重新启动LoginActivity。

再来看一下我们是怎么注册ForceOfflineReceiver这个BroadcastReceiver的。可以看到，这里重写了onResume()和onPause()这两个生命周期方法，然后分别在这两个方法里注册和取消注册了ForceOfflineReceiver。

为什么要这样写呢？之前不都是在onCreate()和onDestroy()方法里注册和取消注册BroadcastReceiver的吗？这是因为我们始终需要保证只有处于栈顶的Activity才能接收到这条强制下线广播，非栈顶的Activity不应该也没必要接收这条广播，所以写在onResume()和onPause()方法里就可以很好地解决这个问题，当一个Activity失去栈顶位置时就会自动取消BroadcastReceiver的注册。

最后还需要对AndroidManifest.xml文件进行修改，是将主Activity设置为LoginActivity，而不再是MainActivity，因为肯定不能在用户没登录的情况下就直接进入程序主界面。