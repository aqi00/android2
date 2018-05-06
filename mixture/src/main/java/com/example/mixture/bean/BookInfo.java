package com.example.mixture.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class BookInfo implements Parcelable {
    public long id; // 编号
    public String title = ""; // 书名
    public String author = ""; // 作者
    public String path; // 文件路径
    public int page_number = 0; // 总页数
    public long size; // 文件大小

    public BookInfo() {
    }

    public BookInfo(String path) {
        this.title = path.substring(path.lastIndexOf("/") + 1);
        this.path = path;
    }

    public BookInfo(String title, String author, String path) {
        this.title = title;
        this.author = author;
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(path);
        dest.writeInt(page_number);
        dest.writeLong(size);
    }

    public static final Creator<BookInfo>
            CREATOR = new Creator<BookInfo>() {

        @Override
        public BookInfo[] newArray(int size) {
            return new BookInfo[size];
        }

        @Override
        public BookInfo createFromParcel(Parcel source) {
            BookInfo bookInfo = new BookInfo();
            bookInfo.id = source.readLong();
            bookInfo.title = source.readString();
            bookInfo.author = source.readString();
            bookInfo.path = source.readString();
            bookInfo.page_number = source.readInt();
            bookInfo.size = source.readLong();
            return bookInfo;
        }
    };
}
