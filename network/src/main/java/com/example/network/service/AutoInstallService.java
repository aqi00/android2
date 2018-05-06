package com.example.network.service;

import java.util.HashMap;
import java.util.Map;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

// 智能安装服务
public class AutoInstallService extends AccessibilityService {
    private final static String TAG = "AutoInstallService";
    private Map<Integer, Boolean> handledMap = new HashMap<Integer, Boolean>();

    public AutoInstallService() {}

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo nodeInfo = event.getSource();
        if (nodeInfo != null) {
            int eventType = event.getEventType();
            if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
                    || eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                if (handledMap.get(event.getWindowId()) == null) {
                    boolean handled = iterateNodesAndHandle(nodeInfo);
                    if (handled) {
                        handledMap.put(event.getWindowId(), true);
                    }
                }
            }
        }
    }

    private boolean iterateNodesAndHandle(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            if (nodeInfo.getClassName().equals("android.widget.Button")) {
                String nodeText = nodeInfo.getText().toString().toUpperCase();
                Log.d(TAG, "getClassName="+nodeInfo.getClassName().toString()+", nodeText=" + nodeText);
                // 系统语言兼容简体中文、繁體中文与英文
                // 忽略“下一步”，响应“安装”和“完成”
                // 忽略“NEXT”，响应“INSTALL”和“DONE”
                if (nodeText.equals("安装") || nodeText.equals("继续安装") || nodeText.equals("完成")
                        || nodeText.equals("安裝") || nodeText.equals("繼續安裝") || nodeText.equals("完成")
                        || nodeText.equals("INSTALL") || nodeText.equals("DONE")) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            } else if (nodeInfo.getClassName().equals("android.widget.ScrollView")) {
                // 滚动到页面底部
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            }
            int childCount = nodeInfo.getChildCount();
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
                if (iterateNodesAndHandle(childNodeInfo)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onInterrupt() {}

}
