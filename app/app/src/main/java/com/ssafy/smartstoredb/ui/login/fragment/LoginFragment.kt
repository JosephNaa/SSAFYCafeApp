package com.ssafy.smartstoredb.ui.login.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLogin.mOAuthLoginHandler
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.config.ApplicationClass
import com.ssafy.smartstoredb.ui.login.LoginActivity
import com.ssafy.smartstoredb.data.service.UserService
import com.ssafy.smartstoredb.model.dto.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException

import org.json.JSONObject

import com.nhn.android.naverlogin.OAuthLogin.mOAuthLoginHandler

import android.widget.ImageButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInApi
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


// 로그인 화면
private const val TAG = "LoginFragment_싸피"
class LoginFragment : Fragment(){
    private lateinit var loginActivity: LoginActivity
    private var loginType: String? = null
    lateinit var btnLogin : Button
    lateinit var btnJoin : Button
    lateinit var btnKakao : ImageButton
    lateinit var btnNaver : OAuthLoginButton
    lateinit var btnGoogle : ImageButton

    private var checkedId = false
    var socialUser = User()

    lateinit var mOAuthLoginInstance : OAuthLogin
    lateinit var mContext: Context
    val OAUTH_CLIENT_ID = "szowXGoW6mjaBusX181m"
    val OAUTH_CLIENT_SECRET = "idi_iJ2_Oi"
    val OAUTH_CLIENT_NAME = "ssafy_cafe_final"

    var mAuth: FirebaseAuth?=null
    var mGoogleSignInClient: GoogleSignInClient?=null

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
        btnKakao = view.findViewById(R.id.btnKakao)
        btnNaver = view.findViewById(R.id.btnNaver)
        btnGoogle = view.findViewById(R.id.btnGoogle)

        var id = view.findViewById<EditText>(R.id.editTextLoginID)
        var password = view.findViewById<EditText>(R.id.editTextLoginPW)

        btnLogin.setOnClickListener{
            login(id.text.toString(), password.text.toString())
        }

        btnJoin.setOnClickListener {
            loginActivity.openFragment(2)
        }

        btnKakao.setOnClickListener {
            val callback: (OAuthToken?, Throwable?) -> Unit = {token, error ->
                if (error != null) {
                    Log.d(TAG, "onViewCreated: 로그인 실패, $error")
                } else if (token != null) {
                    Log.d(TAG, "onViewCreated: 로그인 성공 ${token}")
                    writeSharedPreference("kakao")

                    UserApiClient.instance.me { user, error ->
                        Log.d(TAG, "회원번호: ${user?.id}")
                        Log.d(TAG, "닉네임: ${user?.kakaoAccount?.profile?.nickname}")

                        socialUser.id = "k"+user?.id.toString()
                        socialUser.name = user?.kakaoAccount?.profile?.nickname!!
                        socialUser.pass = "pass" + user?.id.toString()
                        socialUser.stamps = 0

                        UserService().isAvailable(socialUser.id, CheckIdCallback())

                        ApplicationClass.sharedPreferencesUtil.addUser(socialUser)
                        loginActivity.openFragment(1)

                    }
                }
            }

            if (LoginClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
                LoginClient.instance.loginWithKakaoTalk(requireContext(), callback = callback)
            } else {
                LoginClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
            }
        }

        mContext = requireContext()
        mOAuthLoginInstance = OAuthLogin.getInstance()
        mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME)
        btnNaver.setOAuthLoginHandler(mOAuthLoginHandler)

        loginType = readSharedPreference()

        if (loginType.equals("naver")) {
            deleteSharedPreference()
            naverLogout()
        } else if (loginType.equals("kakao")) {
            deleteSharedPreference()
            UserApiClient.instance.logout{}
        } else if (loginType.equals("google")) {
            deleteSharedPreference()
            mGoogleSignInClient?.signOut()
            mAuth?.signOut()
        }

        btnGoogle.setOnClickListener {
            initAuth()
        }
    }



    private fun initAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.firebase_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)

        mAuth = FirebaseAuth.getInstance()
        signIn()
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth?.currentUser

                    Log.d(TAG, "user: ${user!!.displayName} ${user!!.email}")

                    socialUser.id = "g" + user!!.email
                    socialUser.name = user!!.displayName.toString()
                    socialUser.pass = "pass" + user!!.email
                    socialUser.stamps = 0

                    UserService().isAvailable(socialUser.id, CheckIdCallback())

                    ApplicationClass.sharedPreferencesUtil.addUser(socialUser)

                    writeSharedPreference("google")

                    loginActivity.openFragment(1)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    private val mOAuthLoginHandler = @SuppressLint("HandlerLeak") object : OAuthLoginHandler() {
        override fun run(success: Boolean) {
            if (success) {
                writeSharedPreference("naver")
                RequestApiTask().execute()
            } else {
                val errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).code
                val errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext)
                Log.d(TAG, "errorCode: $errorCode errorDesc: $errorDesc")
            }
        }

    }

    inner class RequestApiTask() : AsyncTask<Void?, Void?, String>() {
        override fun onPreExecute() {}

        override fun doInBackground(vararg params: Void?): String? {
            val url = "https://openapi.naver.com/v1/nid/me"
            val at = mOAuthLoginInstance.getAccessToken(mContext)
            return mOAuthLoginInstance.requestApi(mContext, at, url)
        }

        override fun onPostExecute(content: String) {
            try {
                val loginResult = JSONObject(content)
                if (loginResult.getString("resultcode") == "00") {
                    val response = loginResult.getJSONObject("response")
                    val email = response.getString("email")
                    val name = response.getString("name")

//                    Toast.makeText(mContext, "id : $id email : $email", Toast.LENGTH_SHORT).show()
//                    Toast.makeText(mContext, "res : $response", Toast.LENGTH_SHORT).show()

                    socialUser.id = "n" + email
                    socialUser.name = name
                    socialUser.pass = "pass" + email
                    socialUser.stamps = 0

                    UserService().isAvailable(socialUser.id, CheckIdCallback())

                    ApplicationClass.sharedPreferencesUtil.addUser(socialUser)
                    loginActivity.openFragment(1)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    fun naverLogout() {
        mOAuthLoginInstance.logout(mContext)
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

    inner class CheckIdCallback: RetrofitCallback<Boolean> {
        override fun onSuccess( code: Int, id: Boolean) {

            if (!id) {
                checkedId = true
                UserService().join(socialUser, JoinCallback())

            }else{
            }
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "아이디 중복 체크 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }

    inner class JoinCallback: RetrofitCallback<Boolean> {
        override fun onSuccess( code: Int, check: Boolean) {

            if (check) {
                Toast.makeText(context, "회원가입되었습니다!", Toast.LENGTH_SHORT).show()
                loginActivity.openFragment(3)
            }
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "회원가입 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }

    // SP 저장
    private fun writeSharedPreference(type: String){
        val sp = context?.getSharedPreferences("login_type", Context.MODE_PRIVATE)
        val editor = sp!!.edit()
        editor.putString("type", type)
        editor.apply()
    }

    // SP 읽기
    private fun readSharedPreference(): String{
        val sp = context?.getSharedPreferences("login_type", Context.MODE_PRIVATE)
        return sp?.getString("type", "") ?: ""
    }

    // SP 지우기
    private fun deleteSharedPreference() {
        val sp = context?.getSharedPreferences("login_type", Context.MODE_PRIVATE)
        val editor = sp!!.edit()
        editor.clear()
        editor.apply()
    }
}