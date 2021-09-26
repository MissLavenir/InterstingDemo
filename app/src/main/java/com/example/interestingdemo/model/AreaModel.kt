package com.example.interestingdemo.model

import android.os.Parcel
import android.os.Parcelable

class AreaModel() : Parcelable {
    // 区域ID
    var StoreID: String? = ""
    //区域名称
    var StoreName: String? = ""
    //区域地址
    var Address: String? = ""
    //纬度
    var Lat: Float? = 0.0f
    //经度
    var Lon: Float? = 0.0f
    //图标地址
    var Img: String? = ""
    //距离
    var Distance: Float? = 0.0f

    /**local param*/
    var selected = false

    constructor(parcel: Parcel) : this() {
        StoreID = parcel.readString()
        StoreName = parcel.readString()
        Address = parcel.readString()
        Lat = parcel.readValue(Float::class.java.classLoader) as? Float
        Lon = parcel.readValue(Float::class.java.classLoader) as? Float
        Img = parcel.readString()
        Distance = parcel.readValue(Float::class.java.classLoader) as? Float
        selected = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(StoreID)
        parcel.writeString(StoreName)
        parcel.writeString(Address)
        parcel.writeValue(Lat)
        parcel.writeValue(Lon)
        parcel.writeString(Img)
        parcel.writeValue(Distance)
        parcel.writeByte(if (selected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AreaModel> {
        override fun createFromParcel(parcel: Parcel): AreaModel {
            return AreaModel(parcel)
        }

        override fun newArray(size: Int): Array<AreaModel?> {
            return arrayOfNulls(size)
        }
    }
}