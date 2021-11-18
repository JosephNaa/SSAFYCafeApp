package com.ssafy.smartstoredb.ui.login

import android.content.Intent
import android.os.Bundle
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.config.ApplicationClass.Companion.sharedPreferencesUtil
import com.ssafy.smartstoredb.databinding.ActivityLoginBinding
import com.ssafy.smartstoredb.ui.base.BaseActivity
import com.ssafy.smartstoredb.ui.login.fragment.JoinFragment
import com.ssafy.smartstoredb.ui.login.fragment.LoginFragment
import com.ssafy.smartstoredb.ui.main.MainActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //로그인 된 상태인지 확인
        var user = sharedPreferencesUtil.getUser()

        //로그인 상태 확인. id가 있다면 로그인 된 상태
        if (user.id != ""){
            openFragment(1)
        } else {
            // 가장 첫 화면은 홈 화면의 Fragment로 지정
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_login, LoginFragment())
                .commit()
        }
    }

    fun openFragment(int: Int){
        val transaction = supportFragmentManager.beginTransaction()
        when(int){
            1 -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent)
            }
            2 -> transaction.replace(R.id.frame_layout_login, JoinFragment())
                .addToBackStack(null)

            3 -> transaction.replace(R.id.frame_layout_login, LoginFragment())
                .addToBackStack(null)
        }
        transaction.commit()
    }

}