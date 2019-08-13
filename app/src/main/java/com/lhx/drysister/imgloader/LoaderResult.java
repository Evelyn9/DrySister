package com.lhx.drysister.imgloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Description: 加载结果类
 * Created by Ly on 2019/8/12.
 */
public class LoaderResult {
    public ImageView img;
    public String uri;
    public Bitmap bitmap;
    public int reqWidth;
    public int reqHeight;

    public LoaderResult(ImageView img, String uri, Bitmap bitmap, int reqWidth, int reqHeight) {
        this.img = img;
        this.uri = uri;
        this.bitmap = bitmap;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }
}
