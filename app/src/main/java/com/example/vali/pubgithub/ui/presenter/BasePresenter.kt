package com.example.vali.pubgithub.ui.presenter

interface BasePresenter<T> {
    fun setView(view: T)
    fun detachView()
}