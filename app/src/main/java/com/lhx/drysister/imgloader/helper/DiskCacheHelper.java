package com.lhx.drysister.imgloader.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.util.Log;

import com.lhx.drysister.imgloader.SisterCompress;
import com.lhx.drysister.imgloader.disklrucache.DiskLruCache;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Description: 磁盘缓存相关
 * Created by Ly on 2019/8/12.
 */
public class DiskCacheHelper {
    private static final String TAG = DiskCacheHelper.class.getSimpleName();
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50; // 设置磁盘缓存区的大小为：50M
    private static final int DISK_CACHE_INDEX = 0;

    private Context mContext;
    private DiskLruCache mDiskLruCache;
    private SisterCompress mCompress;
    private boolean mIsDiskLruCacheCreated = false; // 磁盘缓存是否创建

    public DiskCacheHelper(Context mContext) {
        this.mContext = mContext;
        mCompress = new SisterCompress();
        File diskCacheDir = getDiskCacheDir(mContext, "diskCache");
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdir();
        }
        if (getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE) {
            try {
                mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                mIsDiskLruCacheCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获得磁盘缓存的目录
     */
    private File getDiskCacheDir(Context context, String dirName) {
        // 判断机身存储是否可用
        boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if (externalStorageAvailable) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        Log.v(TAG, "diskCachePath= " + cachePath);
        return new File(cachePath + File.separator + dirName);
    }

    /**
     * 查询可用空间大小
     */
    private long getUsableSpace(File path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return path.getUsableSpace();
        }
        final StatFs statFs = new StatFs(path.getPath());
        return statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong();
    }

    /**
     * 根据Key加载磁盘缓存中的图片
     */
    public Bitmap loadBitmapFromDiskCache(String key, int reqWidth, int reqHeight) throws IOException {
        Log.v(TAG, "加载磁盘缓存中的图片");
        // 判断是否在主线程里操作
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("不能在UI线程中加载图片");
        }
        if (mDiskLruCache == null) {
            return null;
        }
        Bitmap bitmap = null;
        // 获取磁盘缓存中的图片，添加到内存缓存中
        DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
        if (snapshot != null) {
            FileInputStream fileInputStream = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
            FileDescriptor fileDescriptor = fileInputStream.getFD();
            bitmap = mCompress.decodeBitmapFromFileDescriptor(fileDescriptor, reqWidth, reqHeight);
        }
        return bitmap;
    }

    /**
     * 将图片字节流缓存到磁盘
     *
     * @return Bitmap 用于显示
     */
    public Bitmap saveImgByte(String key, int reqWidth, int reqHeight, byte[] bytes) {
        // 判断是否在主线程里操作
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("不能在UI线程里做网络操作！");
        }
        if (mDiskLruCache == null) {
            return null;
        }
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            if (editor != null) {
                OutputStream output = editor.newOutputStream(DISK_CACHE_INDEX);
                output.write(bytes);
                output.flush();
                editor.commit();
                output.close();
                return loadBitmapFromDiskCache(key, reqWidth, reqHeight);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DiskLruCache getmDiskLruCache() {
        return mDiskLruCache;
    }

    public boolean getIsDiskLruCacheCreate() {
        return mIsDiskLruCacheCreated;
    }

    public void setIsDiskLruCacheCreate(boolean status) {
        this.mIsDiskLruCacheCreated = status;
    }


}
