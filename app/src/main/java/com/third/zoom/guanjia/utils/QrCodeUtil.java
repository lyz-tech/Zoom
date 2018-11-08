package com.third.zoom.guanjia.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.third.zoom.common.listener.HttpListener;
import com.third.zoom.common.listener.NormalListener;
import com.third.zoom.guanjia.bean.XWBack;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Alienware on 2018/10/26.
 */

public class QrCodeUtil {

    //    private static String QR_URL = "http://m.test.wafaiot.com/wafa/test/api/thirdParty/mac/for/mini/qr/code";
//    private static String vendorOpenId = "gJ8EMFa4";
    //    private static String MAC = "865933031234567";
    private static String vendorOpenId = "TdE75W33";
    private static String MAC = "865933030876078";
    private static String QR_URL = "http://m.wafaiot.com/wafa/api/thirdParty/mac/for/mini/qr/code";

    public static void setMac(String mac) {
        MAC = mac;
    }

    public static void getQrString(final HttpListener normalListener) {
        String url = QR_URL;
        OkHttpClient okHttpClient = new OkHttpClient();
        url = url + "?" + "vendorOpenId=" + vendorOpenId + "&" + "mac=" + MAC;
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ZM", "getQrString = " + e.getMessage());
                normalListener.onFail(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                XWBack xwBack = gson.fromJson(result, XWBack.class);
                normalListener.onSuccess(xwBack);
            }
        });
    }

    /**
     * 为HttpGet 的 url 方便的添加多个name value 参数。
     *
     * @param url
     * @param params
     * @return
     */
    public static String attachHttpGetParams(String url, LinkedHashMap<String, String> params) {

        Iterator<String> keys = params.keySet().iterator();
        Iterator<String> values = params.values().iterator();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("?");

        for (int i = 0; i < params.size(); i++) {
            String value = null;
            try {
                value = URLEncoder.encode(values.next(), "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            stringBuffer.append(keys.next() + "=" + value);
            if (i != params.size() - 1) {
                stringBuffer.append("&");
            }
        }

        return url + stringBuffer.toString();
    }


    /**
     * @ClassName: QRCodeUtil
     * @Description: 二维码工具类
     * @Author Wangnan
     * @Date 2017/2/8
     */

    /**
     * 创建二维码位图
     *
     * @param content 字符串内容(支持中文)
     * @param width   位图宽度(单位:px)
     * @param height  位图高度(单位:px)
     * @return
     */
    @Nullable
    public static Bitmap createQRCodeBitmap(String content, int width, int height) {
        return createQRCodeBitmap(content, width, height, "UTF-8", "H", "2", Color.BLACK, Color.WHITE);
    }

    /**
     * 创建二维码位图 (支持自定义配置和自定义样式)
     *
     * @param content          字符串内容
     * @param width            位图宽度,要求>=0(单位:px)
     * @param height           位图高度,要求>=0(单位:px)
     * @param character_set    字符集/字符转码格式 (支持格式:{@link  })。传null时,zxing源码默认使用 "ISO-8859-1"
     * @param error_correction 容错级别 (支持级别:{@link  })。传null时,zxing源码默认使用 "L"
     * @param margin           空白边距 (可修改,要求:整型且>=0), 传null时,zxing源码默认使用"4"。
     * @param color_black      黑色色块的自定义颜色值
     * @param color_white      白色色块的自定义颜色值
     * @return
     */
    @Nullable
    public static Bitmap createQRCodeBitmap(String content, int width, int height,
                                            @Nullable String character_set, @Nullable String error_correction, @Nullable String margin,
                                            @ColorInt int color_black, @ColorInt int color_white) {

        /** 1.参数合法性判断 */
        if (TextUtils.isEmpty(content)) { // 字符串内容判空
            return null;
        }

        if (width < 0 || height < 0) { // 宽和高都需要>=0
            return null;
        }

        try {
            /** 2.设置二维码相关配置,生成BitMatrix(位矩阵)对象 */
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();

            if (!TextUtils.isEmpty(character_set)) {
                hints.put(EncodeHintType.CHARACTER_SET, character_set); // 字符转码格式设置
            }

            if (!TextUtils.isEmpty(error_correction)) {
                hints.put(EncodeHintType.ERROR_CORRECTION, error_correction); // 容错级别设置
            }

            if (!TextUtils.isEmpty(margin)) {
                hints.put(EncodeHintType.MARGIN, margin); // 空白边距设置
            }
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值 */
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = color_black; // 黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white; // 白色色块像素设置
                    }
                }
            }

            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,之后返回Bitmap对象 */
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }


}
