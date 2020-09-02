package com.example.vali.pubgithub.utils

import android.content.Context
import com.example.vali.pubgithub.R
import com.example.vali.pubgithub.data.entity.Owner
import com.google.gson.Gson

const val SHAREDPREFERENCE_USERPROFILE = "pubgithub"

object SharedPreferencesHelper {

    fun saveOwner(owner: Owner, context: Context) {
       context.getSharedPreferences(SHAREDPREFERENCE_USERPROFILE, Context.MODE_PRIVATE)?.let { pSharedPref ->
            Gson().toJson(owner)
            val editor = pSharedPref.edit()
            editor.remove(context.getString(R.string.owner_shared_text)).apply()
            editor.putString(context.getString(R.string.owner_shared_text), Gson().toJson(owner))
            editor.apply()
        }
    }

    fun getOwner(context: Context): String? {
        return context.getSharedPreferences(SHAREDPREFERENCE_USERPROFILE, Context.MODE_PRIVATE)
            ?.getString(context.getString(R.string.owner_shared_text), null)
    }

    fun deleteOwner(context: Context) {
        context.getSharedPreferences(SHAREDPREFERENCE_USERPROFILE, Context.MODE_PRIVATE)?.let { pSharedPref ->
            val editor = pSharedPref.edit()
            editor.remove(context.getString(R.string.owner_shared_text)).apply()
            editor.apply()
        }
    }
}