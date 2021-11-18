package com.ssafy.smartstoredb.fragment

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ssafy.smartstoredb.ApplicationClass
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.activity.LoginActivity
import com.ssafy.smartstoredb.service.UserService

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
            var user = UserService(loginActivity).login(id.text.toString().trim(), password.text.toString().trim())

            if(user.id != ""){
                Toast.makeText(context,"로그인 되었습니다.", Toast.LENGTH_SHORT).show()

                //로그인 하면 shared preference 에 저장
                ApplicationClass.sharedPreferencesUtil.putUser(user)

                //메인 화면으로 전환
                loginActivity.openFragment(1)

            }else{
                Toast.makeText(context,"id 혹은 password를 확인해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        btnJoin.setOnClickListener {
            loginActivity.openFragment(2)
        }

    }

}