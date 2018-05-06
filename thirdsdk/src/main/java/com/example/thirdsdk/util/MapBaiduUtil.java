package com.example.thirdsdk.util;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.model.LatLng;

// 该工具类主要实现三个功能：
// 1、计算地面两点之间的距离
// 2、计算地面一个多边形的面积
// 3、计算某点是否在多边形内部
public class MapBaiduUtil {
    static double DEF_PI = 3.14159265359; // PI
    static double DEF_2PI = 6.28318530712; // 2*PI
    static double DEF_PI180 = 0.01745329252; // PI/180.0
    static double DEF_R = 6370693.5; // radius of earth

    public static double getShortDistance(LatLng pos1, LatLng pos2) {
        return getShortDistance(pos1.longitude, pos1.latitude, pos2.longitude, pos2.latitude);
    }

    public static double getLongDistance(LatLng pos1, LatLng pos2) {
        return getLongDistance(pos1.longitude, pos1.latitude, pos2.longitude, pos2.latitude);
    }

    public static double getShortDistance(double lon1, double lat1, double lon2, double lat2) {
        double ew1, ns1, ew2, ns2;
        double dx, dy, dew;
        double distance;
        // 角度转换为弧度
        ew1 = lon1 * DEF_PI180;
        ns1 = lat1 * DEF_PI180;
        ew2 = lon2 * DEF_PI180;
        ns2 = lat2 * DEF_PI180;
        // 经度差
        dew = ew1 - ew2;
        // 若跨东经和西经180 度，进行调整
        if (dew > DEF_PI) {
            dew = DEF_2PI - dew;
        } else if (dew < -DEF_PI) {
            dew = DEF_2PI + dew;
        }
        dx = DEF_R * Math.cos(ns1) * dew; // 东西方向长度(在纬度圈上的投影长度)
        dy = DEF_R * (ns1 - ns2); // 南北方向长度(在经度圈上的投影长度)
        // 勾股定理求斜边长
        distance = Math.sqrt(dx * dx + dy * dy);
        return distance;
    }

    public static double getLongDistance(double lon1, double lat1, double lon2, double lat2) {
        double ew1, ns1, ew2, ns2;
        double distance;
        // 角度转换为弧度
        ew1 = lon1 * DEF_PI180;
        ns1 = lat1 * DEF_PI180;
        ew2 = lon2 * DEF_PI180;
        ns2 = lat2 * DEF_PI180;
        // 求大圆劣弧与球心所夹的角(弧度)
        distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1)
                * Math.cos(ns2) * Math.cos(ew1 - ew2);
        // 调整到[-1..1]范围内，避免溢出
        if (distance > 1.0) {
            distance = 1.0;
        } else if (distance < -1.0) {
            distance = -1.0;
        }
        // 求大圆劣弧长度
        distance = DEF_R * Math.acos(distance);
        return distance;
    }

    public static LatLng getCenterPos(ArrayList<LatLng> posArray) {
        double totalLatitude = 0;
        double totalLongitude = 0;
        int count = posArray.size();
        for (int i = 0; i < count; i++) {
            totalLatitude += posArray.get(i).latitude;
            totalLongitude += posArray.get(i).longitude;
        }
        LatLng centerPos = new LatLng(totalLatitude / count, totalLongitude / count);
        return centerPos;
    }

    public static ArrayList<MapPoint> calculatePoint(ArrayList<LatLng> posArray) {
        LatLng centerPos = getCenterPos(posArray);
        ArrayList<MapPoint> pointArray = new ArrayList<MapPoint>();
        for (int i = 0; i < posArray.size(); i++) {
            LatLng pos = posArray.get(i);
            MapPoint point = new MapPoint();
            point.x = getShortDistance(centerPos.longitude, centerPos.latitude,
                    pos.longitude, centerPos.latitude);
            if (pos.longitude < centerPos.longitude) {
                point.x *= -1;
            }
            point.y = getShortDistance(centerPos.longitude, centerPos.latitude,
                    centerPos.longitude, pos.latitude);
            if (pos.latitude < centerPos.latitude) {
                point.y *= -1;
            }
            pointArray.add(point);
        }
        return pointArray;
    }

    public static double getArea(List<MapPoint> list) {
        // S = 0.5 * ( (x0*y1-x1*y0) + (x1*y2-x2*y1) + ... + (xn*y0-x0*yn) )

        double area = 0.00;
        for (int i = 0; i < list.size(); i++) {
            if (i < list.size() - 1) {
                MapPoint p1 = list.get(i);
                MapPoint p2 = list.get(i + 1);
                area += p1.x * p2.y - p2.x * p1.y;
            } else {
                MapPoint pn = list.get(i);
                MapPoint p0 = list.get(0);
                area += pn.x * p0.y - p0.x * pn.y;
            }
        }
        area = area / 2.00;

        return area;
    }

    public static double getArea(ArrayList<LatLng> posArray) {
        ArrayList<MapPoint> pointArray = calculatePoint(posArray);
        return getArea(pointArray);
    }


    private static Cross mCross;

    private static void checkCross(LatLng pos, LatLng firstPos, LatLng secondPos) {
        double x = pos.longitude;
        double y = pos.latitude;
        double x1 = firstPos.longitude;
        double y1 = firstPos.latitude;
        double x2 = secondPos.longitude;
        double y2 = secondPos.latitude;
        if (x1 == x2) {
            if (y >= Math.min(y1, y2) && y <= Math.max(y1, y2)) {
                if (x > x1) {
                    mCross.leftCross++;
                } else if (x < x1) {
                    mCross.rightCross++;
                }
            }
        } else if (y1 == y2) {
            if (x >= Math.min(x1, x2) && x <= Math.max(x1, x2)) {
                if (y > y1) {
                    mCross.bottomCross++;
                } else if (y < y2) {
                    mCross.topCross++;
                }
            }
        } else {
            double a = (y1 - y2) / (x1 - x2);
            double b = (x1 * y2 - x2 * y1) / (x1 - x2);
            double crossY = a * x + b;
            if (crossY >= Math.min(y1, y2) && crossY <= Math.max(y1, y2)) {
                if (y > crossY) {
                    mCross.bottomCross++;
                } else if (y < crossY) {
                    mCross.topCross++;
                }
            }
            double crossX = (y - b) / a;
            if (crossX >= Math.min(x1, x2) && crossX <= Math.max(x1, x2)) {
                if (x > crossX) {
                    mCross.leftCross++;
                } else if (x < crossX) {
                    mCross.rightCross++;
                }
            }
        }
    }

    public static boolean isInsidePolygon(LatLng pos, ArrayList<LatLng> posList) {
        mCross = new Cross();
        for (int i = 0; i < posList.size(); i++) {
            LatLng thisPos = posList.get(i);
            LatLng nextPos = posList.get((i + 1) % posList.size());
            checkCross(pos, thisPos, nextPos);
        }
        boolean is_in = false;
        if (mCross.leftCross % 2 == 1 && mCross.rightCross % 2 == 1
                && mCross.topCross % 2 == 1 && mCross.bottomCross % 2 == 1) {
            is_in = true;
        }
//		Toast.makeText(this, "leftCross="+mCross.leftCross+", rightCross="+mCross.rightCross
//				+", topCross="+mCross.topCross+", bottomCross="+mCross.bottomCross, Toast.LENGTH_SHORT).show();
        return is_in;
    }


    public static class Cross {
        int leftCross;
        int rightCross;
        int topCross;
        int bottomCross;

        Cross() {
            leftCross = 0;
            rightCross = 0;
            topCross = 0;
            bottomCross = 0;
        }
    }

    public static class MapPoint {
        double x;
        double y;

        MapPoint() {
            x = 0.0;
            y = 0.0;
        }
    }

}
