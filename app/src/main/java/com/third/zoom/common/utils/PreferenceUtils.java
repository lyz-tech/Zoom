package com.third.zoom.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Map;
import java.util.Set;

public class PreferenceUtils {
	private static SharedPreferences mSharedPreferences = null;
	private static Editor mEditor = null;

	public synchronized static void init(Context context) {
		if (null == mSharedPreferences) {
			mSharedPreferences = context.getSharedPreferences("zoom-default",0);
		}
	}

	public static void removeKey(String key) {
		mSharedPreferences.edit().remove(key).commit();
	}

	public static void removeAll() {
		mSharedPreferences.edit().clear().commit();
	}

	public static void commitString(String key, String value) {
		mSharedPreferences.edit().putString(key, value).commit();
	}

	public static void commitStrings(Map<String, String> stringMap) {
		mEditor = mSharedPreferences.edit();
		for (Map.Entry<String, String> entry : stringMap.entrySet()) {
			mEditor.putString(entry.getKey(), entry.getValue());
		}
		mEditor.commit();
	}

	public static String getString(String key, String faillValue) {
		return mSharedPreferences.getString(key, faillValue);
	}

	public static void commitInt(String key, int value) {
		mSharedPreferences.edit().putInt(key, value).commit();
	}

	public static int getInt(String key, int failValue) {
		return mSharedPreferences.getInt(key, failValue);
	}

	public static void commitLong(String key, long value) {
		mSharedPreferences.edit().putLong(key, value).commit();
	}

	public static long getLong(String key, long failValue) {
		return mSharedPreferences.getLong(key, failValue);
	}

	public static void commitBoolean(String key, boolean value) {
		mSharedPreferences.edit().putBoolean(key, value).commit();
	}

	public static Boolean getBoolean(String key, boolean failValue) {
		return mSharedPreferences.getBoolean(key, failValue);
	}

	public static void commitStringSet(String key, Set<String> values) {
		mSharedPreferences.edit().putStringSet(key, values).commit();
	}

	public static Set<String> getStringSet(String key) {
		return mSharedPreferences.getStringSet(key, null);
	}
}