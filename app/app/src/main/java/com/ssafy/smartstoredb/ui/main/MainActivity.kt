package com.ssafy.smartstoredb.ui.main

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.os.RemoteException
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.common.util.Utility
import com.ssafy.smartstore.dto.ShoppingCart
import com.ssafy.smartstoredb.*
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.config.ApplicationClass
import com.ssafy.smartstoredb.data.service.FirebaseTokenService
import com.ssafy.smartstoredb.databinding.ActivityMainBinding
import com.ssafy.smartstoredb.ui.base.BaseActivity
import com.ssafy.smartstoredb.ui.login.LoginActivity
import com.ssafy.smartstoredb.ui.main.home.fragment.BeaconDialog
import com.ssafy.smartstoredb.ui.main.home.fragment.HomeFragment
import com.ssafy.smartstoredb.ui.main.mypage.fragment.MypageFragment
import com.ssafy.smartstoredb.ui.main.order.fragment.*
import org.altbeacon.beacon.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "MainActivity_싸피"
val SP_NAME = "fcm_message"
lateinit var message: String

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate), BeaconConsumer {
    private lateinit var bottomNavigation : BottomNavigationView

    var fragment: Fragment?=null

    var nfcAdapter: NfcAdapter?=null
    var pIntent: PendingIntent?= null
    lateinit var filters: Array<IntentFilter>

    // 장바구니 - local
    private val ShoppingCart = mutableListOf<ShoppingCart>()

    // beacon
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var myLocation : Location? =null

    private lateinit var beaconManager: BeaconManager
    private val STORE_DISTANCE = 1
    private val region = Region("altbeacon",null,null,null)
    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var needBLERequest = true
    private val PERMISSIONS_CODE = 100
    private var isStore = false

    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        var keyHash = Utility.getKeyHash(this)
        Log.d(TAG, "onCreate: $keyHash")

        initFirebase()

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        getNFCData(intent)

        setNdef()

        setBeacon()

        createNotificationChannel(channel_id, "ssafy")

        checkPermissions()

        startScan()

        // 가장 첫 화면은 홈 화면의 Fragment로 지정
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_main, HomeFragment())
            .commit()

        bottomNavigation = findViewById(R.id.tab_layout_bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.navigation_page_1 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, HomeFragment())
                        .commit()
                    true
                }
                R.id.navigation_page_2 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, OrderFragment())
                        .commit()
                    true
                }
                R.id.navigation_page_3 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, MypageFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }

        bottomNavigation.setOnNavigationItemReselectedListener { item ->
            // 재선택시 다시 랜더링 하지 않기 위해 수정
            if(bottomNavigation.selectedItemId != item.itemId){
                bottomNavigation.selectedItemId = item.itemId
            }
        }
    }

    fun openFragment(index:Int, key:String, value:Int){
        moveFragment(index, key, value)
    }

    fun openFragment(index: Int) {
        moveFragment(index, "", 0)
    }

    private fun moveFragment(index:Int, key:String, value:Int){
        val transaction = supportFragmentManager.beginTransaction()
        when(index){
            //장바구니
            1 -> transaction.replace(R.id.frame_layout_main, ShoppingListFragment.newInstance(key, value))
                .addToBackStack(null)
            //주문 상세 보기
            2 -> transaction.replace(R.id.frame_layout_main, OrderDetailFragment.newInstance(key, value))
                .addToBackStack(null)
            //메뉴 상세 보기
            3 -> transaction.replace(R.id.frame_layout_main, MenuDetailFragment.newInstance(key, value))
                .addToBackStack(null)
            //map으로 가기
            4 -> transaction.replace(R.id.frame_layout_main, MapFragment())
                .addToBackStack(null)
            //logout
            5 -> {
                logout()
            }
            6-> {
                transaction.replace(R.id.frame_layout_main, OrderFragment.newInstance(key,value))
                    .addToBackStack(null)
                hideBottomNav(false)

            }
        }
        transaction.commit()
    }

    fun logout(){
        //preference 지우기
        ApplicationClass.sharedPreferencesUtil.deleteUser()


        //화면이동
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent)
    }

    fun hideBottomNav(state : Boolean){
        if(state) bottomNavigation.visibility =  View.GONE
        else bottomNavigation.visibility = View.VISIBLE
    }

    private fun initFirebase() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {
                task ->

            if (!task.isSuccessful) {
                Log.w(TAG, "FCM 토큰 얻기에 실패하였습니다.", task.exception)
                return@OnCompleteListener
            }
            // token log 남기기
            Log.d(TAG, "token: ${task.result?:"task.result is null"}")
            uploadToken(task.result!!)
        })

    }

    private fun getNFCData(intent: Intent) {
        Log.d(TAG, "getNFCData: ")
        //Tag가 태깅되었을 때 데이터 추출
        if (intent.action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)

            if (rawMsgs != null) {
                val message = arrayOfNulls<NdefMessage>(rawMsgs.size)

                for (i in rawMsgs.indices) {
                    message[i] = rawMsgs[i] as NdefMessage
                }

                //실제 저장되어 있는 데이터 추출
                var record_data = message[0]!!.records[0]
                val record_type = record_data.type
                val type = String(record_type)
                if (type.equals("T")) {
                    val data = message[0]!!.records[0].payload
                    Log.d(TAG, "getNFCData: $data")
                    //가져온 데이터를 TaxtView에 반영
                } else{
                    Log.d(TAG, "getNFCData: nodata")
                }


            }
        }
    }

    private fun setNdef(){
//        intent.action = NfcAdapter.ACTION_NDEF_DISCOVERED
        val i = Intent(this, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        pIntent = PendingIntent.getActivity(this, 0, i, 0)

        val tag_filter = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        filters = arrayOf(tag_filter)
    }

    private fun setBeacon(){
        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))
        bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        isStore = false
    }

    // NotificationChannel 설정
    private fun createNotificationChannel(id: String, name: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance)

            val notificationManager: NotificationManager
                    = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkPermissions(){

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        Log.d(TAG, "onNewIntent: ")

        if(intent!!.action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            val detectTag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            Log.d(TAG, "onNewIntent: $detectTag")
            getData(rawMsgs)
        }
    }

    private fun getData(msg: Array<Parcelable>?) {
        Log.d(TAG, "getData: ")

        if (msg != null) {
            val message = arrayOfNulls<NdefMessage>(msg.size)

            for (i in msg.indices) {
                message[i] = msg[i] as NdefMessage
            }

            var record_data = message[0]!!.records[0]
            val record_type = record_data.type
            val type = String(record_type)

            if (type.equals("T")) {
                val data = message[0]!!.records[0].payload
                Log.d(TAG, "getData: ${type} ${String(data, 3, data.size-3)}")
                var table = String(data, 3, data.size-3)
                ApplicationClass.sharedPreferencesUtil.addTable("order_table $table")
                Toast.makeText(this,  "${table}번 테이블 번호가 등록되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // beacon
    private fun isEnableBLEService(): Boolean{
        if(!bluetoothAdapter!!.isEnabled){
            return false
        }
        return true
    }

    // Beacon Scan 시작
    private fun startScan() {
        if(!isEnableBLEService()){
            requestEnableBLE()
            Log.d(TAG, "startScan: 블루투스가 켜지지 않았습니다.")
            return
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                PERMISSIONS_CODE
            )
        }
        Log.d(TAG, "startScan: beacon Scan start")

        beaconManager.bind(this)
    }

    private fun requestEnableBLE(){
        val callBLEEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        requestBLEActivity.launch(callBLEEnableIntent)
        Log.d(TAG, "requestEnableBLE: ")
    }

    private val requestBLEActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (isEnableBLEService()) {
            needBLERequest = false
            startScan()
        }
    }

    // 위치 정보 권한 요청 결과 콜백 함수
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_CODE -> {
                if(grantResults.isNotEmpty()) {
                    for((i, permission) in permissions.withIndex()) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.i(TAG, "$permission 권한 획득에 실패하였습니다.")
                            finish()
                        }
                    }
                }
            }
        }
    }

    override fun onBeaconServiceConnect() {
        beaconManager.addMonitorNotifier(object : MonitorNotifier {
            override fun didEnterRegion(region: Region?) {
                try {
                    Log.d(TAG, "비콘을 발견하였습니다.------------${region.toString()}")
                    beaconManager.startRangingBeaconsInRegion(region!!)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

            override fun didExitRegion(region: Region?) {
                try {
                    Log.d(TAG, "비콘을 찾을 수 없습니다.")
                    Toast.makeText(this@MainActivity, "매장 밖 지역 입니다", Toast.LENGTH_SHORT).show()
                    beaconManager.stopRangingBeaconsInRegion(region!!)
                    isStore = false
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

            override fun didDetermineStateForRegion(i: Int, region: Region?) {}
        })

        beaconManager.addRangeNotifier { beacons, region ->
            for (beacon in beacons) {
                if(isYourBeacon(beacon)){
                    // 한번만 띄우기 위한 조건
                    if(isStore == false){
                        Log.d(TAG, "distance: " + beacon.distance + " Major : " + beacon.id2 + ", Minor" + beacon.id3)
                        isStore = true
                        showDialog()
                    }
                }
            }

            if(beacons.isEmpty()){

            }
        }

        try {
            beaconManager.startMonitoringBeaconsInRegion(region)

        } catch (e: RemoteException){
            e.printStackTrace()
        }
    }

    private fun isYourBeacon(beacon: Beacon): Boolean {
//        return (beacon.id2.toString() == BEACON_MAJOR &&
//                beacon.id3.toString() == BEACON_MINOR &&
//                beacon.distance <= STORE_DISTANCE
//                )
        return (beacon.distance <= STORE_DISTANCE)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        beaconManager.stopMonitoringBeaconsInRegion(region)
        beaconManager.stopRangingBeaconsInRegion(region)
        beaconManager.unbind(this)
        super.onDestroy()
    }

    private fun showDialog(){
        Log.d(TAG, "showDialog: ")
        val dialog = BeaconDialog()
        dialog.show(supportFragmentManager, "CustomDialog")
    }

    companion object{
        // Notification Channel ID
        const val channel_id = "ssafy_channel"
        // ratrofit  수업 후 network 에 업로드 할 수 있도록 구성
        fun uploadToken(token:String){
            // 새로운 토큰 수신 시 서버로 전송
            val storeService = ApplicationClass.retrofit.create(FirebaseTokenService::class.java)
            storeService.uploadToken(token).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if(response.isSuccessful){
                        val res = response.body()
                        Log.d(TAG, "onResponse: $res")
                    } else {
                        Log.d(TAG, "onResponse: Error Code ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d(TAG, t.message ?: "토큰 정보 등록 중 통신오류")
                }
            })
        }
    }
}