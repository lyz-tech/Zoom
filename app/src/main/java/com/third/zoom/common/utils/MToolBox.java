package com.third.zoom.common.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.third.zoom.R;

import java.io.File;

public class MToolBox {
	// array clear

	public static void memset(int a_Array[], int a_Size, int a_Value) {
		for (int i = 0; i < a_Size; i++) {
			a_Array[i] = a_Value;
		}

	}

	public static void memset(char a_Array[], int a_Size, char a_Value) {
		for (int i = 0; i < a_Size; i++) {
			a_Array[i] = a_Value;
		}
	}

	public static void memset(boolean a_Array[], int a_Size, boolean a_Value) {
		for (int i = 0; i < a_Size; i++) {
			a_Array[i] = a_Value;
		}
	}

	// get image resource id
	public static int getFileImage(int a_FileType) {
		switch (a_FileType) {

		case Global.FileType_Dir:
			return R.drawable.dir;
			
		case Global.FileType_Png:
		case Global.FileType_Jpg:
		case Global.FileType_jpeg:
		case Global.FileType_bmp:
			return R.drawable.image;

		case Global.FileType_gif:
			return R.drawable.gif;
			
		case Global.FileType_mp3:
		case Global.FileType_wav:
		case Global.FileType_ogg:
		case Global.FileType_midi:
			return R.drawable.audio;
			
		case Global.FileType_mp4:
		case Global.FileType_rmvb:
		case Global.FileType_avi:
		case Global.FileType_mkv:
			return R.drawable.vedio;
		case Global.FileType_flv:
			return R.drawable.flv;
			
		case Global.FileType_jsp:
		case Global.FileType_htm:
		case Global.FileType_php:
			return R.drawable.web;

		case Global.FileType_js:
			return R.drawable.js;
			
		case Global.FileType_html:
			return R.drawable.html;
			
		case Global.FileType_c:
			return R.drawable.c;
		case Global.FileType_cpp:
			return R.drawable.cpp;
			
		case Global.FileType_txt:
		case Global.FileType_xml:
		case Global.FileType_py:
		case Global.FileType_json:
		case Global.FileType_log:
			return R.drawable.text;
			
		case Global.FileType_xls:
		case Global.FileType_xlsx:
			return R.drawable.excel;
			
		case Global.FileType_doc:
			return R.drawable.doc;
		case Global.FileType_docx:
			return R.drawable.docx;
			
		case Global.FileType_ppt:
		case Global.FileType_pptx:
			return R.drawable.ppt;
		case Global.FileType_pdf:
			return R.drawable.pdf;
			
		case Global.FileType_jar:
			return R.drawable.jar;
		case Global.FileType_zip:
			return R.drawable.zip;
		case Global.FileType_rar:
			return R.drawable.rar;
		case Global.FileType_gz:
			return R.drawable.gz;
		case Global.FileType_apk:
			return R.drawable.apk;
			
		default:
			return R.drawable.other_file;
		}
	}

	// get sd card path
	public static String getSdcardPath() {
		File l_File = Environment.getExternalStorageDirectory();
		return l_File.getPath();
	}

	/** create open file intent */

	// android��ȡһ�����ڴ�HTML�ļ���intent
	public static Intent getHtmlFileIntent(File file) {
		Uri uri = Uri.parse(file.toString()).buildUpon()
				.encodedAuthority("com.android.htmlfileprovider")
				.scheme("content").encodedPath(file.toString()).build();
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.setDataAndType(uri, "text/html");
		return intent;
	}

	// android��ȡһ�����ڴ�ͼƬ�ļ���intent
	public static Intent getImageFileIntent(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "image/*");
		return intent;
	}

	// android��ȡһ�����ڴ�PDF�ļ���intent
	public static Intent getPdfFileIntent(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/pdf");
		return intent;
	}

	// android��ȡһ�����ڴ��ı��ļ���intent
	public static Intent getTextFileIntent(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "text/plain");
		return intent;
	}

	// android��ȡһ�����ڴ���Ƶ�ļ���intent
	public static Intent getAudioFileIntent(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "audio/*");
		return intent;
	}

	// android��ȡһ�����ڴ���Ƶ�ļ���intent
	public static Intent getVideoFileIntent(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "video/*");
		return intent;
	}

	// android��ȡһ�����ڴ�CHM�ļ���intent
	public static Intent getChmFileIntent(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/x-chm");
		return intent;
	}

	// android��ȡһ�����ڴ�Word�ļ���intent
	public static Intent getWordFileIntent(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/msword");
		return intent;
	}

	// android��ȡһ�����ڴ�Excel�ļ���intent
	public static Intent getExcelFileIntent(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/vnd.ms-excel");
		return intent;
	}

	// android��ȡһ�����ڴ�PPT�ļ���intent
	public static Intent getPPTFileIntent(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		return intent;
	}

	// android��ȡһ�����ڴ�apk�ļ���intent
	public static Intent getApkFileIntent(File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		return intent;
	}

}
