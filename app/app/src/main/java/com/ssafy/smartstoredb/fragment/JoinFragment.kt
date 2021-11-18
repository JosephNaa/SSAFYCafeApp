package com.ssafy.smartstoredb.fragment

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.activity.LoginActivity
import com.ssafy.smartstoredb.databinding.FragmentJoinBinding
import com.ssafy.smartstoredb.service.UserService

// 회원 가입 화면
private const val TAG = "JoinFragment_싸피"

class JoinFragment : Fragment() {
    private lateinit var loginActivity: LoginActivity
    private var checkedId = false
    lateinit var binding: FragmentJoinBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //id 중복 확인 버튼
        binding.btnConfirm.setOnClickListener {

            var isValidId =
                UserService(loginActivity).isAvailableId(binding.editTextJoinID.text.toString())
            //중복아닐때
            if (isValidId) {
                Toast.makeText(context, "사용가능한 id 입니다", Toast.LENGTH_SHORT).show()
            }
            //중복일때
            else {
                Toast.makeText(context, "중복된 id 입니다", Toast.LENGTH_SHORT).show()
            }


        }

        // 회원가입 버튼
        binding.btnJoin.setOnClickListener {
            var isValidId =
                UserService(loginActivity).isAvailableId(binding.editTextJoinID.text.toString())
            if (isValidId && binding.editTextJoinID.text.toString() != "" && binding.editTextJoinPW.text.toString() != "" && binding.editTextJoinName.text.toString() != "") {
                UserService(loginActivity).join(
                    binding.editTextJoinID.text.toString(),
                    binding.editTextJoinName.text.toString(),
                    binding.editTextJoinPW.text.toString()

                )
                Toast.makeText(context, "회원가입 완료", Toast.LENGTH_SHORT).show()
                loginActivity.openFragment(3)
            } else if (!isValidId) {
                Toast.makeText(context, "중복된 id 입니다", Toast.LENGTH_SHORT).show()
            } else if (binding.editTextJoinID.text.toString() == "") {
                Toast.makeText(context, "id를 입력해주세요", Toast.LENGTH_SHORT).show()
            } else if (binding.editTextJoinPW.text.toString() == "") {
                Toast.makeText(context, "pw를 입력해주세요", Toast.LENGTH_SHORT).show()
            } else if (binding.editTextJoinName.text.toString() == "") {
                Toast.makeText(context, "별명을 입력해주세요", Toast.LENGTH_SHORT).show()
            }

        }
    }
}