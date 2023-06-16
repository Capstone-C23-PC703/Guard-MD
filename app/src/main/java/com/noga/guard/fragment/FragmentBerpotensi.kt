package com.noga.guard.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.noga.guard.R
import com.noga.guard.adapter.AdapterBerpotensi
import com.noga.guard.model.ModelGempaBerpotensi
import com.noga.guard.views.Main
import com.noga.guard.views.MainPresenter
import com.noga.guard.views.MainView
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONException
import org.json.JSONObject


class FragmentBerpotensi : Fragment(), MainView {
    // TODO: Rename and change types of parameters
    var dataListGempa: MutableList<ModelGempaBerpotensi> = ArrayList()
    var adapterBerpotensi: AdapterBerpotensi? = null
    var mainPresenter: MainPresenter? = null
    var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_berpotensi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        progressDialog = ProgressDialog(activity)
        progressDialog?.setTitle("Please Wait")
        progressDialog?.setCancelable(false)
        progressDialog?.setMessage("Sedang Mengambil Data")

        adapterBerpotensi = AdapterBerpotensi(dataListGempa)

        mainPresenter = Main(this)
        (mainPresenter as Main).getDataGempaBerpotensi()

        rvGempaTerkini.setLayoutManager(LinearLayoutManager(activity))
        rvGempaTerkini.setHasFixedSize(true)
        rvGempaTerkini.setAdapter(adapterBerpotensi)
    }

    override fun onGetDataJSON(response: JSONObject?) {
        try {
            val jsonArray = response?.getJSONArray("features")
            if (jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
                    val dataApi = ModelGempaBerpotensi()
                    val jsonObject = jsonArray.getJSONObject(i)
                    val jsonObjectData = jsonObject.getJSONObject("properties")
                    dataApi.strTanggal = jsonObjectData.getString("tanggal")
                    dataApi.strWaktu = jsonObjectData.getString("jam")
                    dataApi.strLintang = jsonObjectData.getString("lintang")
                    dataApi.strBujur = jsonObjectData.getString("bujur")
                    dataApi.strMagnitude = jsonObjectData.getString("magnitude")
                    dataApi.strKedalaman = jsonObjectData.getString("kedalaman")
                    dataApi.strWilayah = jsonObjectData.getString("wilayah")
                    dataListGempa.add(dataApi)
                }
            }
            adapterBerpotensi?.notifyDataSetChanged()
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(
                activity,
                "Oops, ada yang tidak beres. Coba ulangi beberapa saat lagi.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    override fun onNotice(pesanNotice: String?) {
        Toast.makeText(
            activity,
            "Oops! Sepertinya ada masalah dengan koneksi internet kamu.",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onProses(proses: Boolean) {
        if (proses) {
            progressDialog?.show()
        } else {
            progressDialog?.dismiss()
        }
    }
}