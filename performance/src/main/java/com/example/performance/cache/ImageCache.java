package com.example.performance.cache;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

@SuppressLint(value={"StaticFieldLeak","DefaultLocale"})
public class ImageCache {
    private final static String TAG = "ImageCache";
    // 内存中的图片缓存
    private HashMap<String, Bitmap> mImageMap = new HashMap<String, Bitmap>();
    // 图片地址与视图控件的映射关系
    private HashMap<String, ImageView> mViewMap = new HashMap<String, ImageView>();
    // 缓存队列，采用FIFO先进先出策略，需操作队列首尾两端，故采用双端队列
    private LinkedList<String> mFifoList = new LinkedList<String>();
    // 缓存队列，采用LRU近期最少使用策略，Android专门提供了LruCache实现该算法
    private LruCache<String, Bitmap> mImageLru;

    private ImageCacheConfig mConfig; // 声明一个图片缓存配置对象
    private String mDir = ""; // 缓存图片的文件目录
    private ThreadPoolExecutor mPool; // 声明一个线程池对象
    private static Handler mHandler; // 声明一个渲染处理器对象
    private static ImageCache mCache = null; // 声明一个图片缓存对象
    private static Context mContext; // 声明一个上下文对象

    // 通过单例模式获得图片缓存的唯一实例
    public static ImageCache getInstance(Context context) {
        if (mCache == null) {
            mCache = new ImageCache();
            mCache.mContext = context;
        }
        return mCache;
    }

    // 初始化图片缓存的配置
    public ImageCache initConfig(ImageCacheConfig config) {
        mCache.mConfig = config;
        mCache.mDir = mCache.mConfig.mDir;
        if (mCache.mDir == null || mCache.mDir.length() <= 0) {
            // 生成缓存图片的文件目录
            mCache.mDir = mContext.getExternalFilesDir(
                    Environment.DIRECTORY_DOWNLOADS).toString() + "/image_cache";
        }
        Log.d(TAG, "mDir=" + mCache.mDir);
        // 若目录不存在，则先创建新目录
        File dir = new File(mCache.mDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 创建一个固定大小的线程池
        mCache.mPool = (ThreadPoolExecutor)
                Executors.newFixedThreadPool(mCache.mConfig.mThreadCount);
        mCache.mHandler = new RenderHandler((Activity) mCache.mContext);
        // 如果采用最近最少使用算法，则要设定LruCache缓存的大小
        if (mCache.mConfig.mCacheStyle == ImageCacheConfig.LRU) {
            mImageLru = new LruCache(mCache.mConfig.mMemoryFileCount);
        }
        return mCache;
    }

    // 往图像视图上加载网络图片
    public void show(String url, ImageView iv) {
        iv.setImageDrawable(null);
        if (mConfig.mBeginImage != 0) {
            // 加载操作前先显示开始图片
            iv.setImageResource(mConfig.mBeginImage);
        }
        mViewMap.put(url, iv);
        if (isExist(url)) { // 内存中已存在该图片
            // 直接渲染该图片
            mCache.render(url, getBitmap(url));
        } else { // 内存中不存在该图片
            String path = getFilePath(url);
            if ((new File(path)).exists()) { // 磁盘上已存在该图片
                // 从图片文件中读取位图数据
                Bitmap bitmap = ImageUtil.openBitmap(path);
                if (bitmap != null) {
                    // 直接渲染该图片
                    mCache.render(url, bitmap);
                } else {
                    // 命令线程池启动图片加载任务
                    mPool.execute(new LoadRunnable(url));
                }
            } else { // 磁盘上不存在该图片
                // 命令线程池启动图片加载任务
                mPool.execute(new LoadRunnable(url));
            }
        }
    }

    // 判断内存中是否已存在该图片
    private boolean isExist(String url) {
        if (mCache.mConfig.mCacheStyle == ImageCacheConfig.LRU) { // 最近最少使用算法
            return (mImageLru.get(url) == null) ? false : true;
        } else { // 先进先出算法
            return mImageMap.containsKey(url);
        }
    }

    // 根据图片地址获取内存中的位图数据
    private Bitmap getBitmap(String url) {
        if (mCache.mConfig.mCacheStyle == ImageCacheConfig.LRU) { // 最近最少使用算法
            return mImageLru.get(url);
        } else { // 先进先出算法
            return mImageMap.get(url);
        }
    }

    // 根据图片地址生成对应的图片路径
    private String getFilePath(String url) {
        return String.format("%s/%d.jpg", mDir, url.hashCode());
    }

    // 定义一个渲染处理器，用于在UI主线程中渲染图片
    private static class RenderHandler extends Handler {
        public static WeakReference<Activity> mActivity;

        public RenderHandler(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity act = mActivity.get();
            if (act != null) {
                ImageData data = (ImageData) (msg.obj);
                if (data != null && data.bitmap != null) { // 已获得位图数据
                    // 直接渲染该图片
                    mCache.render(data.url, data.bitmap);
                } else { // 未获得位图数据
                    // 加载失败，则显示错误图片
                    mCache.showError(data.url);
                }
            }
        }
    }

    // 定义一个图片加载任务
    private class LoadRunnable implements Runnable {
        private String mUrl;

        public LoadRunnable(String url) {
            mUrl = url;
        }

        @Override
        public void run() {
            Activity act = RenderHandler.mActivity.get();
            if (act != null) {
                // 从图片网址处获得位图数据
                Bitmap bitmap = ImageHttp.getImage(mUrl);
                if (bitmap != null) {
                    // 如果需要缩略图，则对位图对象进行缩放操作
                    if (mConfig.mSize != null) {
                        bitmap = Bitmap.createScaledBitmap(bitmap,
                                mConfig.mSize.x, mConfig.mSize.y, false);
                    }
                    // 把位图数据保存为图片文件
                    ImageUtil.saveBitmap(getFilePath(mUrl), bitmap);
                }
                ImageData data = new ImageData(mUrl, bitmap);
                // 下面把图片加载信息送给渲染处理器
                Message msg = mHandler.obtainMessage();
                msg.obj = data;
                mHandler.sendMessage(msg);
            }
        }
    }

    // 在界面上渲染位图图片
    private void render(String url, Bitmap bitmap) {
        ImageView iv = mViewMap.get(url);
        if (mConfig.mFadeDuration <= 0) { // 无需展示淡入动画
            iv.setImageBitmap(bitmap);
        } else { // 需要展示淡入动画
            if (isExist(url)) { // 内存中已有图片的，就直接显示
                iv.setImageBitmap(bitmap);
            } else { // 内存中未有图片的，就展示淡入动画
                iv.setAlpha(0.0f);
                // 下面通过灰度动画来展示图像视图的图片淡入效果
                AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                alphaAnimation.setDuration(mConfig.mFadeDuration);
                alphaAnimation.setFillAfter(true);
                iv.setImageBitmap(bitmap);
                iv.setAlpha(1.0f);
                iv.setAnimation(alphaAnimation);
                alphaAnimation.start();
                // 刷新图片缓存内部的排队队列
                mCache.refreshList(url, bitmap);
            }
        }
    }

    // 刷新图片缓存内部的排队队列
    private synchronized void refreshList(String url, Bitmap bitmap) {
        if (mCache.mConfig.mCacheStyle == ImageCacheConfig.LRU) { // 最近最少使用算法
            // 更新LruCache缓存
            mImageLru.put(url, bitmap);
        } else { // 先进先出算法
            if (mFifoList.size() >= mConfig.mMemoryFileCount) { // 已超过内存中的文件数量限制
                // 移除双端队列开头的小伙伴
                String out_url = mFifoList.pollFirst();
                mImageMap.remove(out_url);
            }
            mImageMap.put(url, bitmap);
            // 往双端队列末尾插入新的小伙伴
            mFifoList.addLast(url);
        }
    }

    // 显示加载失败后的错误图片
    private void showError(String url) {
        ImageView iv = mViewMap.get(url);
        if (mConfig.mErrorImage != 0) {
            iv.setImageResource(mConfig.mErrorImage);
        }
    }

    // 清空图片缓存
    public void clear() {
        // 回收图片缓存中的所有位图对象
        for (Map.Entry<String, Bitmap> item_map : mImageMap.entrySet()) {
            Bitmap bitmap = item_map.getValue();
            bitmap.recycle();
        }
        mImageMap.clear(); // 清空位图映射
        mViewMap.clear(); // 清空视图映射
        mFifoList.clear(); // 清空双端队列
        if (mImageLru != null) {
            mImageLru.evictAll(); // 清空LruCache缓存
        }
        mCache = null;
    }

}
