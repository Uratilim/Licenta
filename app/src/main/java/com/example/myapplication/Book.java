package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {
    private String name;
    private String booklink;
    private float price;
    private int bookid;

    public Book(String name, String booklink, float price, int bookid) {
        this.name = name;
        this.booklink = booklink;
        this.price = price;
        this.bookid = bookid;
    }

    public String getName() {
        return name;
    }

    public String getBookLink() {
        return booklink;
    }

    public Float getPrice() {
        return price;
    }

    public int getBookid() { return bookid; }


    protected Book(Parcel in) {
        name = in.readString();
        booklink = in.readString();
        price = in.readFloat();
        bookid = in.readInt();
    }

    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(booklink);
        dest.writeFloat(price);
        dest.writeInt(bookid);
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>()
    {
        public Book createFromParcel(Parcel in)
        {
            return new Book(in);
        }
        public Book[] newArray(int size)
        {
            return new Book[size];
        }
    };

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    public static final Creator<Book> CREATOR = new Creator<Book>() {
//        @Override
//        public Book createFromParcel(Parcel in) {
//            return new Book(in);
//        }
//
//        @Override
//        public Book[] newArray(int size) {
//            return new Book[size];
//        }
//    };

}



