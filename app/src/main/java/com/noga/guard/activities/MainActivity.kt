package com.noga.guard.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.renderscript.RenderScript
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import com.noga.guard.R
import com.noga.guard.databinding.ActivityMainBinding
import com.noga.guard.fragment.FragmentBerpotensi
import com.noga.guard.fragment.FragmentDirasakan
import com.noga.guard.fragment.FragmentTerkini
import com.noga.guard.networking.ApiConfig
import com.noga.guard.networking.ApiService
import com.noga.guard.networking.GempaResponse
import com.noga.guard.utils.BottomBarBehavior
import nl.joery.animatedbottombar.AnimatedBottomBar
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity(), LocationListener {
    private var _binding: ActivityMainBinding? = null
    val binding get()=_binding!!


    var fragmentManager: FragmentManager? = null
    var strTanggal: String? = null
    var permissionArrays = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    var latitude = 0.0
    var longitude = 0.0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //permission
        val VersionAndroid = Build.VERSION.SDK_INT
        if (VersionAndroid > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (checkIfAlreadyhavePermission() && checkIfAlreadyhavePermission2()) {
            } else {
                requestPermissions(permissionArrays, 101)
            }
        }

        setContentView(_binding?.root)

        //format tanggal hari ini
        val dateNow = Calendar.getInstance().time
        strTanggal = DateFormat.format("EEE", dateNow) as String

        //hide show tab
        val layoutParams = binding.tabNavigation.getLayoutParams() as CoordinatorLayout.LayoutParams
        layoutParams.behavior = BottomBarBehavior()
        if (savedInstanceState == null) {
            binding.tabNavigation.selectTabById(R.id.tabDirasakan, true)
            fragmentManager = supportFragmentManager
            val fragmentDirasakan = FragmentDirasakan()
            fragmentManager!!.beginTransaction().replace(R.id.frameContainer, fragmentDirasakan).commit()
        }

        //fragment
        binding.tabNavigation.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(lastIndex: Int, lastTab: AnimatedBottomBar.Tab?, newIndex: Int, newTab: AnimatedBottomBar.Tab) {
                var fragment: Fragment? = null
                when (newTab.id) {
                    R.id.tabDirasakan -> fragment = FragmentDirasakan()
                    R.id.tabBerpotensi -> fragment = FragmentBerpotensi()
                    R.id.tabSkala -> fragment = FragmentTerkini()
                }
                if (fragment != null) {
                    fragmentManager = supportFragmentManager
                    fragmentManager!!.beginTransaction().replace(R.id.frameContainer, fragment).commit()
                }
            }
        })

        //method get tanggal
//        getToday()

        //method get lokasi Anda
        getLatLong()
    }

    private fun getLatLong() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 115)
            return
        }
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val provider = locationManager.getBestProvider(criteria, true)
        val location = provider?.let { locationManager.getLastKnownLocation(it) }
        if (location != null) {
            onLocationChanged(location)
        } else {
            provider?.let { locationManager.requestLocationUpdates(it, 20000, 0f, this) }
        }
    }

    fun setDirasakan(username: String) {
        ApiConfig.getApiService()
            .dirasakan()
            .enqueue(object : Callback<GempaResponse> {
                override fun onResponse(
                    call: Call<GempaResponse>,
                    response: Response<GempaResponse>
                ) {
                    if (response.isSuccessful) {
                        print("${response.body()?.infogempa}")
                        Toast.makeText(this@MainActivity, "yes", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<GempaResponse>, t: Throwable) {
                    Log.d("Failure", t.message.toString())
                }
            })
    }


//    private fun getToday() {
//        val date = Calendar.getInstance().time
//        val defaultDate = DateFormat.format("d MMM yyyy", date) as String
//        val formatToday = "$strTanggal, $defaultDate"
//    }

//    private fun getCuacaToday() {
//        AndroidNetworking.get(ApiService.URL_CUACA)
//            .addPathParameter("lat", latitude.toString())
//            .addPathParameter("lon", longitude.toString())
//            .addPathParameter("API key", "YOUR API KEY OpenWeather")
//            .setPriority(RenderScript.Priority.MEDIUM)
//            .build()
//            .getAsJSONObject(object : JSONObjectRequestListener {
//                @SuppressLint("SetTextI18n")
//                override fun onResponse(response: JSONObject) {
//                    try {
//                        val jsonArrayOne = response.getJSONArray("weather")
//                        val jsonObjectOne = jsonArrayOne.getJSONObject(0)
//                        val jsonObjectTwo = response.getJSONObject("main")
//                        val jsonObjectThree = response.getJSONObject("wind")
//
//                        val strWeather = jsonObjectOne.getString("main")
//                        val strDescWeather = jsonObjectOne.getString("description")
//                        val strKecepatanAngin = jsonObjectThree.getString("speed")
//                        val strKelembaban = jsonObjectTwo.getString("humidity")
//                        val dblTemperatur = jsonObjectTwo.getDouble("temp")
//
//                        if (strDescWeather == "broken clouds") {
//                            iconTemp.setAnimation(R.raw.broken_clouds)
//                            tvWeather.text = "Awan Tersebar"
//                        } else if (strDescWeather == "light rain") {
//                            iconTemp.setAnimation(R.raw.light_rain)
//                            tvWeather.text = "Gerimis"
//                        } else if (strDescWeather == "haze") {
//                            iconTemp.setAnimation(R.raw.broken_clouds)
//                            tvWeather.text = "Berkabut"
//                        } else if (strDescWeather == "overcast clouds") {
//                            iconTemp.setAnimation(R.raw.overcast_clouds)
//                            tvWeather.text = "Awan Mendung"
//                        } else if (strDescWeather == "moderate rain") {
//                            iconTemp.setAnimation(R.raw.moderate_rain)
//                            tvWeather.text = "Hujan Ringan"
//                        } else if (strDescWeather == "few clouds") {
//                            iconTemp.setAnimation(R.raw.few_clouds)
//                            tvWeather.text = "Berawan"
//                        } else if (strDescWeather == "heavy intensity rain") {
//                            iconTemp.setAnimation(R.raw.heavy_intentsity)
//                            tvWeather.text = "Hujan Lebat"
//                        } else if (strDescWeather == "clear sky") {
//                            iconTemp.setAnimation(R.raw.clear_sky)
//                            tvWeather.text = "Cerah"
//                        } else if (strDescWeather == "scattered clouds") {
//                            iconTemp.setAnimation(R.raw.scattered_clouds)
//                            tvWeather.text = "Awan Tersebar"
//                        } else {
//                            iconTemp.setAnimation(R.raw.unknown)
//                            tvWeather.text = strWeather
//                        }
//
//                        tvTempeatur.text = String.format(Locale.getDefault(), "%.0f°C", dblTemperatur)
//                        tvKecepatanAngin.text = "Kecepatan Angin $strKecepatanAngin km/j"
//                        tvKelembaban.text = "Kelembaban $strKelembaban %"
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                        Toast.makeText(this@MainActivity, "Oops, ada yang tidak beres. Coba ulangi beberapa saat lagi.", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onError(anError: ANError) {
//                    Toast.makeText(this@MainActivity, "Oops! Sepertinya ada masalah dengan koneksi internet kamu.", Toast.LENGTH_SHORT).show()
//                }
//            })
//    }

    override fun onLocationChanged(location: Location) {
        latitude = location.latitude
        longitude = location.longitude

        //method get cuaca
//        getCuacaToday()
    }

    private fun checkIfAlreadyhavePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun checkIfAlreadyhavePermission2(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                val intent = intent
                finish()
                startActivity(intent)
            } else {
                getLatLong()
            }
        }
    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
    override fun onProviderEnabled(s: String) {}
    override fun onProviderDisabled(s: String) {}
}