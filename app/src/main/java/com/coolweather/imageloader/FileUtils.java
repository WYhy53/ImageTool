package com.coolweather.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
//文件操作工具类
public class FileUtils {
    //SD卡的根目录
    private static String mSdRootPath = Environment.getExternalStorageDirectory().getPath();
    //手机的缓存根目录
    private static String mDataRootPath = null;
    //保存Image的目录名
    private final static String FOLDER_NAME = "/tea/image";
    public FileUtils(Context context) {
        mDataRootPath = context.getCacheDir().getPath();
    }
    //获取储存Image的目录
    private String getStorageDirectory() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                mSdRootPath + FOLDER_NAME : mDataRootPath + FOLDER_NAME;
    }
    //保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录，所以这个save方法在SD卡和手机目录中都将图片保存了
    public void saveBitmap(String url, Bitmap bitmap) throws IOException{
        if (bitmap == null) {
            return;
        }
        String path = getStorageDirectory();//这里获取的储存Image的路径有SD卡也有手机目录
        File folderFile = new File(path);
        //有SD卡则忽略这一步，没有则在手机目录里面创建一个
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
        File file = new File(path + File.separator + getFileName(url));
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
    }
    //从手机或者sd卡获取Bitmap
    public Bitmap getBitmap(String url) {
        return BitmapFactory.decodeFile(getStorageDirectory() +
                File.separator + getFileName(url));
    }
    //判断文件是否存在
    public boolean isFileExists(String fileName) {
        return new File(getStorageDirectory() +
                File.separator + getFileName(fileName)).exists();
    }
    //获取文件的大小
    public long getFileSize(String url) {
        return new File(getStorageDirectory() + File.separator + getFileName(url)).length();
    }
    //删除SD卡或者手机的缓存图片和目录
    public void deleteFile() {
        File dirFile = new File(getStorageDirectory());
        if (!dirFile.exists()) {
            return;
        }
        if (dirFile.isDirectory()) {
            String[] children = dirFile.list();
            for (int i = 0; i < children.length; i++) {
                new File(dirFile, children[i]).delete();
            }
        }
        dirFile.delete();
    }
    //根据url截取文件名
    public String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

}
