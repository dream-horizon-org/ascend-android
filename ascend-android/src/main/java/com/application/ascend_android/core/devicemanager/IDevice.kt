package com.application.ascend_android

import android.content.Context
import android.location.Address
import kotlinx.coroutines.CoroutineScope

interface IDevice {

    fun getContext(): Context?
    fun getApplicationContext(): Context?

    fun getCoroutineScope(): CoroutineScope

    fun getDatabase(): IDatabase

    //******************************************
    fun getDeviceOsVersion(): String?
    fun getDeviceOs(): String?
    fun getBrand(): String
    fun getModel(): String
    fun getAppVersionCode(): Int
    fun getAppVersionName(): String?
    fun getDeviceId(): String?
    fun getApplicationName(): String?
    fun getDevicePlatform(): String?
    fun getBuildNumber(): String?


    fun getAdvertiserId(): String?

    fun getImei(): String?

    fun getMacAddress(): String?

    fun getProductName(): String?

    fun getDeviceVersion(): String?

    fun getDeviceManufacturer(): String?


    fun getDeviceResolution(): String?

    fun getDeviceModel(): String?



    fun getConnectionType(): String?

    fun getOperatorName(): String?

    fun getUserAgent(): String?

    fun isEmulator(): String?

    fun isRooted(): Int

    fun getEncValue(value: String?): String?

    fun getDecryptedValue(encryptedValue: String?): String?

    fun getDecryptedValueUsingAes(encryptedValue: String?): String?

    fun getEncryptedValueUsingAes(plainText: String?): String?

    fun getDecryptedValue(encryptedString: String?, secretKey: String?): String?

    fun intIMEI(): String?

    fun getPackageSHA1(): String?

    fun getLocale(): String?

    fun getLocaleFullName(): String?

    fun getDeviceWidth(): Int

    fun getMD5TextOfString(payload: String?): String?

    fun getProcessorArch(): String?

    fun getPackageName(): String?

    fun setLocation(address: Address)
    fun setLatLong(latitude: Double, longitude: Double)

    fun getLocation(): Address
    fun isNetworkConnected(): Boolean
}