package com.coolweather.imageloader;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    public ImageDownLoader.ImageLoaderlistener imageLoaderlistener;
    Bitmap bitmap;
    ImageView imageView;
    ImageDownLoader imageDownLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.image_view);
    }
    public void loadImage(View view){
        String url = "http://cn.bing.com/az/hprichbg/rb/Dongdaemun_ZH-CN10736487148_1920x1080.jpg";
        imageDownLoader=new ImageDownLoader(this);
        bitmap=imageDownLoader.downLoader(imageView,imageLoaderlistener,url);
        imageView.setImageBitmap(bitmap);
    }
}
