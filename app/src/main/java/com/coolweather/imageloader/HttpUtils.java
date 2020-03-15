package com.coolweather.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;

import org.apache.http.params.HttpConnectionParams;

import java.io.InputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Handler;

public class HttpUtils {
    Bitmap bitmap;

    public HttpUtils( ){

    }

    public Bitmap getBitmapFromNet(String imageUrl) {
        try {
            URL url=new URL(imageUrl);
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(4000);
            connection.setReadTimeout(4000);
            connection.connect();
            int code=connection.getResponseCode();
            if (code==200){
                InputStream is=connection.getInputStream();
                bitmap= BitmapFactory.decodeStream(is);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }
}
