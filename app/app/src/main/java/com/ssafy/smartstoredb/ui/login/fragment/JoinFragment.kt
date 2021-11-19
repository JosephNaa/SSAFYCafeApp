package com.ssafy.smartstoredb.ui.login.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.ui.login.LoginActivity
import com.ssafy.smartstoredb.databinding.FragmentJoinBinding
import com.ssafy.smartstoredb.data.service.UserService
import com.ssafy.smartstoredb.model.dto.User

// 회원 가입 화면
private const val TAG = "JoinFragment_싸피"

class JoinFragment : Fragment() {
    private var checkedId = false
    lateinit var binding: FragmentJoinBinding
    private lateinit var loginActivity: LoginActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //id 중복 확인 버튼
        binding.btnConfirm.setOnClickListener{
            val id = binding.editTextJoinID.text.toString()

            if (id == "") {
                Toast.makeText(context, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            UserService().isAvailable(id, CheckIdCallback())
        }

        // 회원가입 버튼
        binding.btnJoin.setOnClickListener {
            val id = binding.editTextJoinID.text.toString()
            val pw = binding.editTextJoinPW.text.toString()
            val name = binding.editTextJoinName.text.toString()

            if (isNotEmpty(id, pw, name)) {
                if (checkedId) {
                    val user = User(id, name, pw)
                    UserService().join(user, JoinCallback())
                } else {
                    Toast.makeText(context, "아이디 중복확인을 해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    inner class CheckIdCallback: RetrofitCallback<Boolean> {
        override fun onSuccess( code: Int, id: Boolean) {

            if (!id) {
                checkedId = true
                binding.editTextJoinID.setBackgroundResource(R.drawable.textview_regular)
                Toast.makeText(context,"사용가능한 아이디입니다.", Toast.LENGTH_SHORT).show()
            }else{
                binding.editTextJoinID.setBackgroundResource(R.drawable.textview_regular_red)
                Toast.makeText(context,"중복된 아이디입니다.", Toast.LENGTH_SHORT).show()
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

    private fun isNotEmpty(id: String, pw: String, name: String): Boolean {
        var flag = true

        if (id != "") {
            binding.editTextJoinID.setBackgroundResource(R.drawable.textview_regular)
        } else {
            binding.editTextJoinID.setBackgroundResource(R.drawable.textview_regular_red)
            flag = false
        }

        if (pw != "") {
            binding.editTextJoinPW.setBackgroundResource(R.drawable.textview_regular)
        } else {
            binding.editTextJoinPW.setBackgroundResource(R.drawable.textview_regular_red)
            flag = false
        }

        if (name != "") {
            binding.editTextJoinName.setBackgroundResource(R.drawable.textview_regular)
        } else {
            binding.editTextJoinName.setBackgroundResource(R.drawable.textview_regular_red)
            flag = false
        }

        return flag
    }
}