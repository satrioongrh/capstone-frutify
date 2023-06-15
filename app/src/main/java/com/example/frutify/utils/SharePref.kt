package com.example.frutify.utils

import android.content.Context
import android.content.SharedPreferences

class SharePref(context: Context) {

    private var SHARED_PREF = "DATA_USER"
    private var sharedPref: SharedPreferences
    val editor: SharedPreferences.Editor

    init {
        sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        editor = sharedPref.edit()
    }



    fun setStringPreference(prefKey: String, value: String) {
        editor.putString(prefKey, value)
        editor.apply()
    }

    fun setIntPreference(prefKey: String, value: Int) {
        editor.putInt(prefKey, value)
        editor.apply()
    }

    fun setBooleanPreference(prefKey: String, value: Boolean) {
        editor.putBoolean(prefKey, value)
        editor.apply()
    }

    fun clearPreferenceByKey(prefKey: String) {
        editor.remove(prefKey)
        editor.apply()
    }

    fun clearPreferences() {
        editor.clear().apply()
    }

    val getToken = sharedPref.getString(Constant.PREF_TOKEN, "")
    val getUserId = sharedPref.getInt(Constant.PREF_USER_ID, 0)
    val isLogin = sharedPref.getBoolean(Constant.PREF_IS_LOGIN, false)
    val getPhone = sharedPref.getString(Constant.PREF_USER_PHONE, "")
    val getEmail = sharedPref.getString(Constant.PREF_EMAIL, "")
    val getRoles = sharedPref.getString(Constant.ROLES, "")
    val getUserRoles = sharedPref.getString(Constant.PREF_USER_ROLE, "")
    val getFullname = sharedPref.getString(Constant.PREF_USER_FULLNAME, "")
    val getAddress = sharedPref.getString(Constant.PREF_USER_ADDRESS, "")


}