package com.example.media.util;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

/**
 * Created by ouyangshen on 2018/5/9.
 */
public class NotifyUtil {

    @TargetApi(Build.VERSION_CODES.O)
    // 创建通知渠道。Android 8.0开始必须给每个通知分配对应的渠道
    public static void createNotifyChannel(Context ctx, String channelId) {
        // 创建一个默认重要性的通知渠道
        NotificationChannel channel = new NotificationChannel(channelId,
                "Channel", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setSound(null, null); // 设置推送通知之时的铃声。null表示静音推送
        channel.enableLights(true); // 设置在桌面图标右上角展示小红点
        channel.setLightColor(Color.RED); // 设置小红点的颜色
        channel.setShowBadge(true); // 在长按桌面图标时显示该渠道的通知
        // 从系统服务中获取通知管理器
        NotificationManager notifyMgr = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        // 创建指定的通知渠道
        notifyMgr.createNotificationChannel(channel);
    }

}
