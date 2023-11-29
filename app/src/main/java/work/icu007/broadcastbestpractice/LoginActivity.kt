package work.icu007.broadcastbestpractice

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import work.icu007.broadcastbestpractice.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {
    private lateinit var activityLoginBinding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(activityLoginBinding.root)
        val prefs = getPreferences(Context.MODE_PRIVATE)
        val isRemember = prefs.getBoolean("remember_password",false)
        if (isRemember) {
            // 将账号密码设置到文本框当中
            val account = prefs.getString("account","")
            val password = prefs.getString("password","")
            Log.d(TAG, "onCreate: account: $account, password: $password")
            activityLoginBinding.accountEdit.setText(account)
            activityLoginBinding.passwordEdit.setText(password)
            activityLoginBinding.rememberPass.isChecked = true
        }
        activityLoginBinding.login.setOnClickListener {
            val account = activityLoginBinding.accountEdit.text.toString()
            val password = activityLoginBinding.passwordEdit.text.toString()
            if (account == "admin" && password == "123456"){
                val editor = prefs.edit()
                if (activityLoginBinding.rememberPass.isChecked){
                    Log.d(TAG, "account: $account, password: $password")
                    editor.putBoolean("remember_password", true)
                    editor.putString("account", account)
                    editor.putString("password", password)
                } else {
                    editor.clear()
                }
                editor.apply()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "account or password is invalid", Toast.LENGTH_SHORT).show()
            }
        }
    }
}