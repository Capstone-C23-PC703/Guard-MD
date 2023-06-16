package com.noga.guard.views

interface MainPresenter {
    fun getDataGempaDirasakan()
    fun getDataGempaBerpotensi()
    fun onProses(proses: Boolean)
}