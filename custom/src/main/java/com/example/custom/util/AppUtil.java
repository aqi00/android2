package com.example.custom.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.custom.bean.AppInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.SparseIntArray;

public class AppUtil {

    // 获取已安装的应用信息队列
    public static ArrayList<AppInfo> getAppInfo(Context ctx, int type) {
        ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
        SparseIntArray siArray = new SparseIntArray();
        // 获得应用包管理器
        PackageManager pm = ctx.getPackageManager();
        // 获取系统中已经安装的应用列表
        List<ApplicationInfo> installList = pm.getInstalledApplications(
                PackageManager.PERMISSION_GRANTED);
        for (int i = 0; i < installList.size(); i++) {
            ApplicationInfo item = installList.get(i);
            // 去掉重复的应用信息
            if (siArray.indexOfKey(item.uid) >= 0) {
                continue;
            }
            // 往siArray中添加一个应用编号，以便后续的去重校验
            siArray.put(item.uid, 1);
            try {
                // 获取该应用的权限列表
                String[] permissions = pm.getPackageInfo(item.packageName,
                        PackageManager.GET_PERMISSIONS).requestedPermissions;
                if (permissions == null) {
                    continue;
                }
                boolean isQueryNetwork = false;
                for (String permission : permissions) {
                    // 过滤那些具备上网权限的应用
                    if (permission.equals("android.permission.INTERNET")) {
                        isQueryNetwork = true;
                        break;
                    }
                }
                // 类型为0表示所有应用，为1表示只要联网应用
                if (type == 0 || (type == 1 && isQueryNetwork)) {
                    AppInfo app = new AppInfo();
                    app.uid = item.uid; // 获取应用的编号
                    app.label = item.loadLabel(pm).toString(); // 获取应用的名称
                    app.package_name = item.packageName; // 获取应用的包名
                    app.icon = item.loadIcon(pm); // 获取应用的图标
                    appList.add(app);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return appList;  // 返回去重后的应用包队列
    }

    // 填充应用的完整信息。主要做两个事情：其一是补充应用的图标字段，其二是将列表按照流量排序
    public static ArrayList<AppInfo> fillAppInfo(Context ctx, ArrayList<AppInfo> originArray) {
        ArrayList<AppInfo> fullArray = (ArrayList<AppInfo>) originArray.clone();
        PackageManager pm = ctx.getPackageManager();
        // 获取系统中已经安装的应用列表
        List<ApplicationInfo> installList = pm.getInstalledApplications(PackageManager.PERMISSION_GRANTED);
        for (int i = 0; i < fullArray.size(); i++) {
            AppInfo app = fullArray.get(i);
            for (ApplicationInfo item : installList) {
                if (app.uid == item.uid) {
                    // 填充应用的图标信息。因为数据库没保存图标的位图，所以取出数据库记录之后还要补上图标数据
                    app.icon = item.loadIcon(pm);
                    break;
                }
            }
            fullArray.set(i, app);
        }
        // 各应用按照流量大小降序排列
        Collections.sort(fullArray, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo o1, AppInfo o2) {
                return (o1.traffic < o2.traffic) ? 1 : -1;
            }
        });
        return fullArray;
    }

}
