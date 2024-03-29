package kr.co.kkensu.maptest

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kr.co.kkensu.integrationmap.*
import kr.co.kkensu.maptest.*
import kr.co.kkensu.maptest.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_main) }

    private var mapApi: MapApi? = null
    private var mapType = MapType.NAVER_MAP

    private var polyLine: MapPolyLine? = null
    private var circle: MapCircle? = null
    private var polygon: MapPolygon? = null
    private var multiPolygon: MapPolygon? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initResources()
    }

    private fun initResources() {
        initMap()
        binding.btnGoogleMap.setOnClickListener {
            if (mapType == MapType.GOOGLE_MAP) {
                return@setOnClickListener
            }

            mapType = MapType.GOOGLE_MAP
            initMap()
        }

        binding.btnNaverMap.setOnClickListener {
            if (mapType == MapType.NAVER_MAP) {
                return@setOnClickListener
            }

            mapType = MapType.NAVER_MAP
            initMap()
        }

        binding.btnTMap.setOnClickListener {
            if (mapType == MapType.TMAP) {
                return@setOnClickListener
            }

            mapType = MapType.TMAP
            initMap()
        }
    }

    private fun initMap() {
        val childFragmentManager: FragmentManager = supportFragmentManager
        var fragment: Fragment? = childFragmentManager.findFragmentByTag("TAG_MAP")
        if (fragment == null) {
            fragment = MapFragmentFactory.create(this, mapType)
            childFragmentManager.beginTransaction()
                .replace(R.id.map, fragment)
                .commit()
        }
        val mapFragment: MapFragment? = fragment as MapFragment?
        mapFragment?.getMapApi { result ->
            mapApi = result
            // 지도 준비 완료시 초기화
            init()
//            mapLocationSetting()
        }
    }

    private fun init() {
        mapApi?.setCenter(MapPoint(37.505762, 127.045092), 17f, true)
//        mapApi?.addMarker(MapPoint(37.505762, 127.045092))

        val options = MapMarkerOptions()
        options.mapPoint = MapPoint(37.505762, 127.045092)
        options.mapMarkerIcon = MapMarkerFactory.createMarker(this, R.drawable.ic_pin_recommend)
//            options.setMapMarkerClickListener(object : MapMarkerClickListener {
//                override fun onClick(view: View?) {
//                    Toast.makeText(this@MainActivity, "Test", Toast.LENGTH_SHORT).show()
//                }
//
//            })

        binding.btnPolyline.setOnClickListener {
            removeAll()
            val list: MutableList<MapPoint> = ArrayList()
            list.add(MapPoint(37.505804, 127.043628))
            list.add(MapPoint(37.505190, 127.045111))
//            polyLine = mapApi?.addPolyline(list)

//            mapApi?.center = MapPoint(37.505497, 127.0443695)
//            mapApi?.zoom(list, 0, 0, true)

            mapApi?.addMarker(options)

        }

        binding.btnCircle.setOnClickListener {
            removeAll()

            val mapPoint = MapPoint(37.507860, 127.040302)
            val list: MutableList<MapPoint> = ArrayList()
            list.add(mapPoint)
            circle =
                mapApi?.addCircle(mapPoint, 50f, Color.parseColor("#44000000"), 10f, Color.BLUE)

//            mapApi?.center = mapPoint
            mapApi?.zoom(list, 0, 0, true)
        }

        binding.btnPolygon.setOnClickListener {
            removeAll()

            val list2: MutableList<MapPoint> = ArrayList()
            list2.add(MapPoint(37.506479, 127.041939))
            list2.add(MapPoint(37.506049, 127.043099))
            list2.add(MapPoint(37.504290, 127.043202))
            list2.add(MapPoint(37.505487, 127.041398))
            list2.add(MapPoint(37.506479, 127.041939))
            polygon = mapApi?.addPolygon(list2)

//            mapApi?.zoom(list2)
//            mapApi?.center = MapPoint(37.506479, 127.041939)
            mapApi?.zoom(list2, 0, 0, true)
        }

        binding.btnMultiPolygon.setOnClickListener {
            removeAll()
            val list3: MutableList<MutableList<MapPoint>> = ArrayList()

            val list4: MutableList<MapPoint> = ArrayList()
            list4.add(MapPoint(37.506479, 127.041939))
            list4.add(MapPoint(37.506049, 127.043099))
            list4.add(MapPoint(37.504290, 127.043202))
            list4.add(MapPoint(37.505487, 127.041398))
            list4.add(MapPoint(37.506479, 127.041939))
            list3.add(list4)

            val list5: MutableList<MapPoint> = ArrayList()
            list5.add(MapPoint(37.505896, 127.046413))
            list5.add(MapPoint(37.504914, 127.048295))
            list5.add(MapPoint(37.504024, 127.046335))
            list5.add(MapPoint(37.504168, 127.043331))
            list5.add(MapPoint(37.505282, 127.045381))
            list5.add(MapPoint(37.505896, 127.046413))
            list3.add(list5)
            multiPolygon = mapApi?.addMultiPolygon(
                list3,
                Color.parseColor("#999999"),
                Color.parseColor("#44000000"),
                2f
            )

            val list: MutableList<MapPoint> = ArrayList()
            list.addAll(list4)
            list.addAll(list5)

//            mapApi?.center = MapPoint(37.504024, 127.046335)

//            mapApi?.zoom(list)
            mapApi?.zoom(list, 0, 0, true)

        }

        mapApi?.setOnCameraMoveListener { center, zoomLevel, isMoving ->
//            Log.e("JHC_DEBUG", "isMoving : $isMoving")
        }
    }

    private fun removeAll() {
        if (polyLine != null) polyLine?.remove()
        if (circle != null) circle?.remove()
        if (polygon != null) polygon?.remove()
        if (multiPolygon != null) multiPolygon?.remove()
    }
}