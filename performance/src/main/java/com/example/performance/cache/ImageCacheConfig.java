package com.example.performance.cache;

import android.graphics.Point;

public final class ImageCacheConfig {
    final String mDir; // 缓存图片的文件目录
    final int mMemoryFileCount; // 内存中的文件数量
    final int mDiskFileCount; // 磁盘上的文件数量
    final int mThreadCount; // 图片加载的线程个数
    final int mFadeDuration; // 淡入动画的播放时长
    final int mBeginImage; // 加载开始前的图片资源编号
    final int mErrorImage; // 加载失败后的图片资源编号
    final Point mSize; // 缩略图的文件尺寸
    final int mCacheStyle; // 图片缓存的排队算法
    public final static int FIFO = 0; // 先进先出算法
    public final static int LRU = 1; // 最近最少使用算法

    private ImageCacheConfig(Builder builder) {
        this.mDir = builder.mDir;
        this.mMemoryFileCount = builder.mMemoryFileCount;
        this.mDiskFileCount = builder.mDiskFileCount;
        this.mThreadCount = builder.mThreadCount;
        this.mFadeDuration = builder.mFadeDuration;
        this.mBeginImage = builder.mBeginImage;
        this.mErrorImage = builder.mErrorImage;
        this.mCacheStyle = builder.mCacheStyle;
        this.mSize = builder.mSize;
    }

    // 图片缓存配置的构建器
    public static class Builder {
        private String mDir = null;
        private int mMemoryFileCount = 10; // 默认内存中的文件数量为10
        private int mDiskFileCount = 50; // 默认磁盘上的文件数量为50
        private int mThreadCount = 3; // 默认图片加载的线程个数为3
        private int mFadeDuration = 3000; // 默认淡入动画的播放时长的3秒
        private int mBeginImage = 0;
        private int mErrorImage = 0;
        private int mCacheStyle = FIFO; // 默认图片缓存的排队算法为先进先出算法
        private Point mSize = null;

        // 设置缓存图片的文件目录
        public Builder setCacheDir(String dir) {
            mDir = dir;
            return this;
        }

        // 设置内存中的文件数量
        public Builder setMemoryFileCount(int memory_file_count) {
            mMemoryFileCount = memory_file_count;
            return this;
        }

        // 设置磁盘上的文件数量
        public Builder setDiskFileCount(int disk_file_count) {
            mDiskFileCount = disk_file_count;
            return this;
        }

        // 设置图片加载的线程个数
        public Builder setThreadCount(int thread_count) {
            mThreadCount = thread_count;
            return this;
        }

        // 设置淡入动画的播放时长
        public Builder setFadeDuration(int fade_duration) {
            mFadeDuration = fade_duration;
            return this;
        }

        // 设置加载开始前的图片资源编号
        public Builder setBeginImage(int begin_image) {
            mBeginImage = begin_image;
            return this;
        }

        // 设置加载失败后的图片资源编号
        public Builder setErrorImage(int error_image) {
            mErrorImage = error_image;
            return this;
        }

        // 设置图片缓存的排队算法
        public Builder setCacheStyle(int cache_style) {
            mCacheStyle = cache_style;
            return this;
        }

        // 设置缩略图的文件尺寸
        public Builder resize(int width, int height) {
            mSize = new Point(width, height);
            return this;
        }

        // 根据构建器生成图片缓存配置
        public ImageCacheConfig build() {
            return new ImageCacheConfig(this);
        }
    }
}
