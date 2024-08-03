package com.cholera.eagleeye.models;
import android.os.Parcel;
import android.os.Parcelable;

public class DataEntry implements Parcelable {
    String date;
    public int predictedCases;

    public DataEntry(String date, int predictedCases) {
        this.date = date;
        this.predictedCases = predictedCases;
    }

    protected DataEntry(Parcel in) {
        date = in.readString();
        predictedCases = in.readInt();
    }

    public static final Creator<DataEntry> CREATOR = new Creator<DataEntry>() {
        @Override
        public DataEntry createFromParcel(Parcel in) {
            return new DataEntry(in);
        }

        @Override
        public DataEntry[] newArray(int size) {
            return new DataEntry[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeInt(predictedCases);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

