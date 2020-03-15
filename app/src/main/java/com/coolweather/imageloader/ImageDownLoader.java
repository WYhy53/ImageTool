package com.coolweather.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import android.os.Handler;

public class ImageDownLoader {
    String url;
    Bitmap bitmap;
    private static final int LOAD_SUCCESS = 1;
    //缓存Image的类，当存储Image的大小大于LruCache设定的值，系统自动释放内存
    private LruCache<String, Bitmap> lruCache;
    private FileUtils utils;
    private ThreadPoolExecutor executor;
    public  ImageDownLoader(Context context){
        super();
        executor = new ThreadPoolExecutor(1, 4,
                2, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int maxSize = maxMemory / 8;
        lruCache = new LruCache<String, Bitmap>(maxSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // 测量Bitmap的大小 默认返回图片数量
                  return value.getRowBytes() * value.getHeight();
            }
        };
        utils = new FileUtils(context);
    }

    //加载图片（找图片）
    public Bitmap downLoader(final ImageView imageView, final ImageLoaderlistener loaderlistener,String imageUrl){
        url=imageUrl;
        //首先得到图片设置的tag(用来区分图片错位的情况)，所以在调用此类<span style="white-space:pre">
       // final String url = (String) imageView.getTag();
        if (url != null) {
            //这里就是从缓存中去找图片
            final Bitmap bitmap = showCacheBitmap(url);
            if (bitmap != null) {
                return bitmap;
            } else {
                final Handler handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        loaderlistener.onImageLoader((Bitmap) msg.obj, imageView);
                    }
                };//如果缓存中返回的图片为空的，则开启线程进行下载
                executor.execute(new Runnable(){
                    @Override
                    public void run(){
                        HttpUtils httpUtils=new HttpUtils();
                        Bitmap bitmap =httpUtils.getBitmapFromNet(url);
                        if (bitmap != null){
                            Message msg = handler.obtainMessage();
                            msg.obj = bitmap;
                            msg.what = LOAD_SUCCESS;
                            handler.sendMessage(msg);
                            try {
                                utils.saveBitmap(url,bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            lruCache.put(url, bitmap);
                        }
                    }
                    });
                }
        }
      return null;
    }
    //获取bitmap对象 : 内存中没有就去sd卡中去找
    public Bitmap showCacheBitmap(String url){
        //先看内存中有没有
        Bitmap bitmap=lruCache.get(url);
        if (bitmap != null){
            return bitmap;
        }//再看文件中有没有，有则保存到内存中，更新内存
        else if (utils.isFileExists(url) && utils.getFileSize(url) > 0) {
            bitmap = utils.getBitmap(url);
            lruCache.put(url, bitmap);
            return bitmap;
        }
        return null;
    }
    //接口
    public interface ImageLoaderlistener {
        public void onImageLoader(Bitmap bitmap,ImageView imageView);
    }
}
