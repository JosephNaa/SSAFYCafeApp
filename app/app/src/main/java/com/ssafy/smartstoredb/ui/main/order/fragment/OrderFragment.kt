package com.ssafy.smartstoredb.ui.main.order.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstoredb.databinding.FragmentOrderBinding
import com.ssafy.smartstoredb.model.dto.Product
import com.ssafy.smartstoredb.data.service.ProductService
import com.ssafy.smartstoredb.ui.main.MainActivity
import com.ssafy.smartstoredb.ui.main.order.adapter.MenuAdapter

// 하단 주문 탭
private const val TAG = "OrderFragment_싸피"
class OrderFragment : Fragment(){
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var prodList:List<Product>
    private lateinit var binding:FragmentOrderBinding
    private var save = ""
    private var mylocation:LatLng = LatLng(36.1,123.1)
    private val locationManager by lazy {
        requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    fun checkPermission() {
        val permissionListener = object : PermissionListener {
            // 권한 얻기에 성공했을 때 동작 처리
            override fun onPermissionGranted() {
                initView()
            }
            // 권한 얻기에 실패했을 때 동작 처리
            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(requireContext(), "위치 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        TedPermission.with(requireContext())
            .setPermissionListener(permissionListener)
            .setDeniedMessage("[설정] 에서 위치 접근 권한을 부여해야만 사용이 가능합니다.")
            // 필요한 권한 설정
            .setPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .check()
    }
    private fun initView() {
        setLastLocation()
        getProviders()
    }
    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun setLastLocation() {

        var lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        Log.d(TAG, "setLastLocation: ${locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)}")
        if (lastKnownLocation != null) {
            mylocation= LatLng(lastKnownLocation.latitude,lastKnownLocation.longitude)
        }
        lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (lastKnownLocation != null) {
            mylocation=LatLng(lastKnownLocation.latitude,lastKnownLocation.longitude)
        }

        lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        if (lastKnownLocation != null) {
            mylocation=LatLng(lastKnownLocation.latitude,lastKnownLocation.longitude)
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getProviders(){
        val listProviders = locationManager.allProviders as MutableList<String>
        val isEnable = BooleanArray(3)
        for (provider in listProviders) {
            when ( provider ) {
                LocationManager.GPS_PROVIDER -> {
                    isEnable[0] = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        0f,
                        listener
                    )
                    Log.d(TAG, provider + '/' + isEnable[0].toString())
                }
                LocationManager.NETWORK_PROVIDER -> {
                    isEnable[1] = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        0,
                        0f,
                        listener
                    )
                    Log.d(TAG, provider + '/' + isEnable[1].toString())
                }
                LocationManager.PASSIVE_PROVIDER -> {
                    isEnable[2] = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)
                    locationManager.requestLocationUpdates(
                        LocationManager.PASSIVE_PROVIDER,
                        0,
                        0f,
                        listener
                    )
                    Log.d(TAG, provider + '/' + isEnable[2].toString())
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            save = it.getInt("distance", 0).toString()
            Log.d(TAG, "onCreate: $save")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(key: String, value: Int) =
            OrderFragment().apply {
                arguments = Bundle().apply {
                    putInt(key, value)
                }
            }
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initData()
        binding.distanceText.text = " 매장까지의 거리는 ${distance(mylocation.latitude,mylocation.longitude,mylocation.latitude+0.002,mylocation.longitude+0.002,"meter").toInt()}m 입니다"

        binding.floatingBtn.setOnClickListener{
            //장바구니 이동
            mainActivity.openFragment(1)
        }

        binding.btnMap.setOnClickListener{
            mainActivity.openFragment(4)
        }
    }

    private fun initData(){
        ProductService().getProductList(ProductCallback())
    }

    inner class ProductCallback: RetrofitCallback<List<Product>> {
        override fun onSuccess( code: Int, productList: List<Product>) {
            productList.let {
                Log.d(TAG, "onSuccess: ${productList}")
                menuAdapter = MenuAdapter(productList)
                menuAdapter.setItemClickListener(object : MenuAdapter.ItemClickListener{
                    override fun onClick(view: View, position: Int, productId:Int) {
                        mainActivity.openFragment(3, "productId", productId)
                    }
                })
            }

            binding.recyclerViewMenu.apply {
                layoutManager = GridLayoutManager(context,3)
                adapter = menuAdapter
                //원래의 목록위치로 돌아오게함
                adapter!!.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }

            Log.d(TAG, "ProductCallback: $productList")
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "유저 정보 불러오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }

    private val listener = object : LocationListener {
        //위치가 변경될때 호출될 method
        @SuppressLint("SetTextI18n")
        override fun onLocationChanged(location: Location) {
            when(location.provider) {
                LocationManager.GPS_PROVIDER -> {
                    mylocation=LatLng(location.latitude,location.longitude)
                }
                LocationManager.NETWORK_PROVIDER -> {
                    mylocation=LatLng(location.latitude,location.longitude)
                }
                LocationManager.PASSIVE_PROVIDER -> {
                    mylocation=LatLng(location.latitude,location.longitude)
                }
            }
        }

        override fun onProviderDisabled(provider: String) {
            //        super.onProviderDisabled(provider)
        }

        @RequiresApi(Build.VERSION_CODES.M)
        @SuppressLint("MissingPermission")
        override fun onProviderEnabled(provider: String) {
            if (isPermitted()) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, this)
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0f, this)
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        if(isPermitted()){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, listener)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, listener)
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0f, listener)
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
        if(isPermitted()) {
            locationManager.removeUpdates(listener)
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun isPermitted():Boolean  {
        return requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}