package com.example.network.bean;

public class ApkInfo {
    public String file_name;
    public String package_name;
    public int version_code;
    public String file_path;
    public int file_size;
    public String version_name;

    public ApkInfo() {
        file_name = "";
        package_name = "";
        version_code = 0;
        file_path = "";
        file_size = 0;
        version_name = "";
    }

    public ApkInfo(String title, String path, int size, String pkg_name, String vs_name, int vs_code) {
        file_name = title;
        package_name = pkg_name;
        version_code = vs_code;
        file_path = path;
        file_size = size;
        version_name = vs_name;
    }

}
