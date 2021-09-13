package net.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class PDFEntity implements Parcelable {
    String name;
    String path;

    public PDFEntity() {

    }

    public PDFEntity(String name, String path) {
        this.name = name;
        this.path = path;
    }

    protected PDFEntity(Parcel in) {
        name = in.readString();
        path = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(path);
    }

    public static final Creator<PDFEntity> CREATOR = new Creator<PDFEntity>() {
        @Override
        public PDFEntity createFromParcel(Parcel parcel) {
            return new PDFEntity(parcel);
        }

        @Override
        public PDFEntity[] newArray(int i) {
            return new PDFEntity[i];
        }
    };
}
