package com.ssafy.smartstoredb.ui.login.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.config.ApplicationClass
import com.ssafy.smartstoredb.ui.login.LoginActivity
import com.ssafy.smartstoredb.data.service.UserService
import com.ssafy.smartstoredb.model.dto.User

// 로그인 화면
private const val TAG = "LoginFragment_싸피"
class LoginFragment : Fragment(){
    private lateinit var loginActivity: LoginActivity
    lateinit var btnLogin : Button
    lateinit var btnJoin : Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnLogin = view.findViewById(R.id.btnLogin)
        btnJoin = view.findViewById(R.id.btnJoin)
        var id = view.findViewById<EditText>(R.id.editTextLoginID)
        var password = view.findViewById<EditText>(R.id.editTextLoginPW)

        btnLogin.setOnClickListener{
            login(id.text.toString(), password.text.toString())
        }

        btnJoin.setOnClickListener {
            loginActivity.openFragment(2)
        }
    }

    // Login API Call
    private fun login(loginId: String, loginPass: String) {
        val user = User(loginId, loginPass)
        UserService().login(user, LoginCallback())
    }

    inner class LoginCallback: RetrofitCallback<User> {
        override fun onSuccess( code: Int, user: User) {

            if (user.id != null) {
                Toast.makeText(context,"로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                // 로그인 시 user정보 sp에 저장
                ApplicationClass.sharedPreferencesUtil.addUser(user)
                loginActivity.openFragment(1)
            }else{
                Toast.makeText(context,"ID 또는 패스워드를 확인해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "유저 정보 불러오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }

}