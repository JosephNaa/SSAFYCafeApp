package com.ssafy.smartstoredb.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.activity.MainActivity
import java.io.IOException
import java.util.*

// Order 탭 - 지도 화면
private const val TAG = "Map_싸피"
class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var mainActivity: MainActivity
    private val UPDATE_INTERVAL = 1000 // 1초
    private val FASTEST_UPDATE_INTERVAL = 500 // 0.5초

    private var mMap: GoogleMap? = null
    private var currentMarker: Marker? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var mCurrentLocatiion: Location
    private lateinit var currentPosition: LatLng
    private var STORE_LOCATION = LatLng(37.241446, 131.864695)
    private var firstRenering: Boolean = true
    private lateinit var mapView: MapView
    private var distance =0
    private val requiredMapPermission = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CALL_PHONE
    )
    private val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult() // ◀ StartActivityForResult 처리를 담당
    ) {
        // 사용자가 GPS 를 켰는지 검사함
        if (checkLocationServicesStatus()) {
            startLocationUpdates()
        }
    }
    private val mapPermissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        // 구현
        requestActivity
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, null)
        firstRenering = true
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = UPDATE_INTERVAL.toLong()
            smallestDisplacement = 10.0f
            fastestInterval = FASTEST_UPDATE_INTERVAL.toLong()
        }

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        mapView = view.findViewById(R.id.map)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.button).setOnClickListener {
            Log.d(TAG, "onViewCreated: $distance")
         mainActivity.openFragment(6,"distance",distance)
        }

    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        //권한 요청 대화상자 (권한이 없을때) & 실행 시 독도
        setDefaultLocation()

        // 1. 위치 권한을 가지고 있는지 확인
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            requiredMapPermission[0]
        )

        // 권한이 허용되어 있다면 위치 업데이트 시작
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 권한 허용 되어있음.
            // ( 안드로이드 6.0 이하 버전은 런타임 권한이 필요없기 때문에 이미 허용된 걸로 인식)
            startLocationUpdates()
        } else { //2. 권한이 없다면 권한 요청 진행
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는 다이얼로그를 이용한 권한 요청
            if (ActivityCompat.shouldShowRequestPermissionRationale(

                    requireActivity(),
                    requiredMapPermission[0]
                )
            ) {
                val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                builder.setTitle("위치 권한 허용")
                    .setMessage("위치 권한 허용이 필요합니다")
                    .setPositiveButton("확인") { _, _ ->
                        mapPermissionResult.launch(requiredMapPermission[0])
                    }
                val alertDialog = builder.create()
                alertDialog.show()
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 권한 요청
                mapPermissionResult.launch(requiredMapPermission[0])
            }
        }
        mMap!!.setOnMarkerClickListener(this)

        mMap!!.uiSettings.isMyLocationButtonEnabled = true
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locationList = locationResult.locations
            if (locationList.size > 0) {

                val location = locationList[locationList.size - 1]
                currentPosition = LatLng(location.latitude, location.longitude)
                val markerTitle: String = getCurrentAddress(currentPosition)
                val markerSnippet =
                    "위도: ${location.latitude.toString()}, 경도: ${location.longitude}"

                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet)
                mCurrentLocatiion = location
            }
        }
    }

    private fun startLocationUpdates() {
        // 위치서비스 활성화 여부 check
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting()
        } else {
            if (checkPermission()) {

                mFusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()!!
                )
                if (mMap != null) mMap!!.isMyLocationEnabled = true
                if (mMap != null) mMap!!.uiSettings.isZoomControlsEnabled = true
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (checkPermission()) {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            if (mMap != null && checkLocationServicesStatus()) mMap!!.isMyLocationEnabled = true
        }
    }

    override fun onStop() {
        super.onStop()
        mFusedLocationClient.removeLocationUpdates(locationCallback)

    }

    fun getCurrentAddress(latlng: LatLng): String {

        //지오코더: GPS를 주소로 변환
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address>?
        try {
            addresses = geocoder.getFromLocation(
                latlng.latitude,
                latlng.longitude,
                1
            )
        } catch (ioException: IOException) {
            //네트워크 문제
            Toast.makeText(requireContext(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show()
            return "지오코더 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(requireContext(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show()
            return "잘못된 GPS 좌표"
        }

        return if (addresses == null || addresses.isEmpty()) {
            Toast.makeText(requireContext(), "주소 발견 불가", Toast.LENGTH_LONG).show()
            "주소 발견 불가"
        } else {
            val address = addresses[0]
            address.getAddressLine(0).toString()
        }
    }

    private fun checkLocationServicesStatus(): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            requiredMapPermission[0]
        )

        return hasFineLocationPermission == PackageManager.PERMISSION_GRANTED
    }
    private  fun distance( lat1:Double, lon1:Double, lat2:Double, lon2:Double, unit:String):Double {

        var theta = lon1 - lon2;
        var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }

        return (dist);
    }


    // This function converts decimal degrees to radians
    private fun deg2rad(deg:Double):Double {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private fun  rad2deg(rad:Double):Double {
        return (rad * 180 / Math.PI);
    }

    fun setCurrentLocation(location: Location, markerTitle: String?, markerSnippet: String?) {
        currentMarker?.remove()

        val currentLatLng = LatLng(location.latitude, location.longitude)

        ///////////////스토어/////////////////

        val storeLatLng = LatLng(location.latitude + 0.002, location.longitude + 0.002)
        STORE_LOCATION = storeLatLng
        distance = distance(currentLatLng.latitude,currentLatLng.longitude,storeLatLng.latitude,storeLatLng.longitude,"meter").toInt()
        val markerStore = MarkerOptions()
        markerStore.position(storeLatLng)
        markerStore.title("store")
        //커스텀 마커
        val bitmap = (ResourcesCompat.getDrawable(
            resources,
            R.drawable.location_icon, null
        ) as BitmapDrawable).bitmap
        val resize = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
        markerStore.icon(BitmapDescriptorFactory.fromBitmap(resize))
        //
        markerStore.snippet("임의로 넣음")
        markerStore.draggable(true)
        ////////////////////////////////////

//        val markerOptions = MarkerOptions()
//        markerOptions.position(currentLatLng)
//        markerOptions.title(markerTitle)
//        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resize))
//        markerOptions.snippet(markerSnippet)
//        markerOptions.draggable(true)

        currentMarker = mMap!!.addMarker(markerStore)
//        currentMarker = mMap!!.addMarker(markerOptions)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)
        mMap!!.animateCamera(cameraUpdate)
    }

    private fun setDefaultLocation() {

        val markerTitle = "위치정보 가져올 수 없음"
        val markerSnippet = "위치 퍼미션과 GPS 활성 여부 확인 필요"


        val location = Location("")
        location.latitude = STORE_LOCATION.latitude
        location.longitude = STORE_LOCATION.longitude

        setCurrentLocation(location, markerTitle, markerSnippet)

    }

    private fun checkPermission(): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    /******** 위치서비스 활성화 여부 check *********/
    private val GPS_ENABLE_REQUEST_CODE = 2001
    private var needRequest = false

    private fun showDialogForLocationServiceSetting() {
        val builder: androidx.appcompat.app.AlertDialog.Builder =
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            "앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { _, _ ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        }
        builder.setNegativeButton(
            "취소"
        ) { dialog, _ -> dialog.cancel() }
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE ->
                //사용자가 GPS를 켰는지 검사함
                if (checkLocationServicesStatus()) {
                    needRequest = true
                    return
                } else {
                    Toast.makeText(
                        requireContext(),
                        "위치 서비스가 꺼져 있어, 현재 위치를 확인할 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun showDialogStore() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setView(R.layout.dialog_map_store)
            setTitle("매장 상세")
            setCancelable(true)
            setPositiveButton("전화걸기") { dialog, _ ->
                if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED){
                    mapPermissionResult.launch(requiredMapPermission[1])
                    startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:01000000000")))
                    dialog.cancel()
                Toast.makeText(requireContext(),"dd",Toast.LENGTH_SHORT).show()
                }else {
                    startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:01000000000")))
                    dialog.cancel()
                }
            }
            setNegativeButton("길찾기") { dialog, _ ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr="+currentPosition.latitude+","+currentPosition.longitude+"&daddr="+STORE_LOCATION.latitude+","+STORE_LOCATION.longitude)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                })
                dialog.cancel()
            }
        }
        builder.create().show()
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        showDialogStore()
        return true;
    }
}