package com.example.vali.pubgithub.utils

import android.content.Context
import com.example.vali.pubgithub.data.entity.Owner
import com.google.gson.Gson

object SharedPreferencesHelper {

    fun saveOwner(owner: Owner, context: Context) {
        val pSharedPref =
            context.getSharedPreferences(Constants.SHAREDPREFERENCE_USERPROFILE, Context.MODE_PRIVATE)
        if (pSharedPref != null) {
            Gson().toJson(owner)
            val editor = pSharedPref.edit()
            editor.remove("owner").apply()
            editor.putString("owner", Gson().toJson(owner))
            editor.apply()
        }
    }

    fun getOwner(context: Context): String? {
        return context.getSharedPreferences(Constants.SHAREDPREFERENCE_USERPROFILE, Context.MODE_PRIVATE)
            ?.getString("owner", null)
    }

    fun deleteOwner(context: Context) {
        val pSharedPref =
            context.getSharedPreferences(Constants.SHAREDPREFERENCE_USERPROFILE, Context.MODE_PRIVATE)
        if (pSharedPref != null) {
            val editor = pSharedPref.edit()
            editor.remove("owner").apply()
            editor.apply()
        }
    }
}