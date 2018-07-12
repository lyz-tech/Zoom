package com.third.zoom.common.utils;

public class Global {
	public static final String ROOT_PATH = "/";

	public static final String FileTypes[][] = {
			// File Image
			{ ".png", ".jpg", ".jpeg", ".gif", ".bmp" },
			// File Audio
			{ ".mp3", ".wav", ".ogg", "midi" },
			// File Video
			{ ".mp4", ".rmvb", ".avi", ".flv", ".mkv" },
			// File Web Text
			{ ".jsp", ".html", ".htm", ".js", ".php" },
			// File Text
			{ ".txt", ".c", ".cpp", ".xml", ".py", ".json", ".log" },
			// File Excel
			{ ".xls", ".xlsx" },
			// File Word
			{ ".doc", ".docx" },
			// File PPT
			{ "ppt", ".pptx" },
			// File PDF
			{ ".pdf" },
			// File Package
			{ ".jar", ".zip", ".rar", ".gz", ".apk" } };
	
	public static final int TypeStart[] = { 100, 200, 300, 400, 500, 600, 700,
			800, 900, 1000 };

	public static final int IndexImage = 0;
	public static final int IndexAudio = 1;
	public static final int IndexVideo = 2;
	public static final int IndexWebText = 3;
	public static final int IndexText = 4;
	public static final int IndexExcel = 5;
	public static final int IndexWord = 6;
	public static final int IndexPPT = 7;
	public static final int IndexPDF = 8;
	public static final int IndexPackage = 9;

	public static final int FileType_Other = 0;
	public static final int FileType_Dir = 1;

	/**
	 * 1.File Type: Image
	 * */
	public static final int FileType_Png = 100;
	public static final int FileType_Jpg = 101;
	public static final int FileType_gif = 102;
	public static final int FileType_jpeg = 103;
	public static final int FileType_bmp = 104;

	/**
	 * 1.File Type: Audio
	 * */
	public static final int FileType_mp3 = 200;
	public static final int FileType_wav = 201;
	public static final int FileType_ogg = 202;
	public static final int FileType_midi = 203;

	/**
	 * 3.File Type: Video
	 * */
	public static final int FileType_mp4 = 300;
	public static final int FileType_rmvb = 301;
	public static final int FileType_avi = 302;
	public static final int FileType_flv = 303;
	public static final int FileType_mkv = 304;

	/**
	 * 4.File Type: Web Text
	 * */
	public static final int FileType_jsp = 400;
	public static final int FileType_html = 401;
	public static final int FileType_htm = 405;
	public static final int FileType_js = 406;
	public static final int FileType_php = 407;

	/**
	 * 5.File Type:Text
	 * */
	public static final int FileType_txt = 500;
	public static final int FileType_c = 501;
	public static final int FileType_cpp = 502;
	public static final int FileType_xml = 503;
	public static final int FileType_py = 504;
	public static final int FileType_json = 505;
	public static final int FileType_log = 506;

	/**
	 * 6.File Type:Excel
	 * */
	public static final int FileType_xls = 600;
	public static final int FileType_xlsx = 601;

	/**
	 * 7.File Type:Word
	 * */
	public static final int FileType_doc = 700;
	public static final int FileType_docx = 701;

	/**
	 * 8.File Type:PPT
	 * */
	public static final int FileType_ppt = 800;
	public static final int FileType_pptx = 801;
	/**
	 * 9.File Type:PDF
	 * */
	public static final int FileType_pdf = 900;

	/**
	 * 10.File Type:Package
	 * */
	public static final int FileType_jar = 1000;
	public static final int FileType_zip = 1001;
	public static final int FileType_rar = 1002;
	public static final int FileType_gz = 1003;
	public static final int FileType_apk = 1004;
}
