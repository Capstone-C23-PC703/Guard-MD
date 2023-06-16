package com.noga.guard.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.noga.guard.ARG_PARAM1
import com.noga.guard.ARG_PARAM2
import com.noga.guard.R
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList


class FragmentDirasakan : Fragment(), MainView {

    var dataListGempa: MutableList<ModelGempaDirasakan> = ArrayList()
    var adapterDirasakan: AdapterDirasakan? = null
    var mainPresenter: MainPresenter? = null
    var progressDialog: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dirasakan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        progressDialog = ProgressDialog(activity)
        progressDialog?.setTitle("Mohon tunggu")
        progressDialog?.setCancelable(false)
        progressDialog?.setMessage("Sedang mengambil data...")

        adapterDirasakan = AdapterDirasakan(dataListGempa)
        mainPresenter = Main(this)
        (mainPresenter as Main).getDataGempaDirasakan()

        listGempaDirasakan.setLayoutManager(LinearLayoutManager(activity))
        listGempaDirasakan.setHasFixedSize(true)
        listGempaDirasakan.setAdapter(adapterDirasakan)
    }

    override fun onGetDataJSON(response: JSONObject?) {
        try {
            val jsonArray = response?.getJSONArray("features")
            if (jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
                    val dataApi = ModelGempaDirasakan()
                    val jsonObject = jsonArray.getJSONObject(i)
                    val jsonObjectData = jsonObject.getJSONObject("properties")
                    dataApi.strTanggal = jsonObjectData.getString("tanggal")
                    dataApi.strKoordinat = jsonObjectData.getString("posisi")
                    dataApi.strKedalaman = jsonObjectData.getString("kedalaman")
                    dataApi.strMagnitude = jsonObjectData.getString("magnitude")
                    dataApi.strKeterangan = jsonObjectData.getString("keterangan")
                    dataApi.strDirasakan = jsonObjectData.getString("dirasakan")
                    dataListGempa.add(dataApi)
                }
            }
            adapterDirasakan?.notifyDataSetChanged()
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(activity, "Oops, ada yang tidak beres. Coba ulangi beberapa saat lagi.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNotice(pesanNotice: String?) {
        Toast.makeText(activity, "Oops! Sepertinya ada masalah dengan koneksi internet kamu.", Toast.LENGTH_SHORT).show()
    }

    override fun onProses(proses: Boolean) {
        if (proses) {
            progressDialog?.show()
        } else {
            progressDialog?.dismiss()
        }
    }

}