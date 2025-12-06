package com.application.ascend_android

import android.Manifest
import android.content.Context
import android.location.Address
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.lang.ref.WeakReference
import java.util.*

class Device(applicationContext: WeakReference<Context>) : IDevice {
    var context: WeakReference<Context>

    val mainScope = CoroutineScope(SupervisorJob())

    private lateinit var deviceLocale: String
    private lateinit var deviceLocaleDisplay: String
    private lateinit var userLocation: Address

    init {
        context = applicationContext
    }

    override fun getContext(): Context? {
        return context.get()
    }

    override fun getApplicationContext(): Context? {
        return context.get()?.applicationContext
    }

    override fun getCoroutineScope(): CoroutineScope {
        return mainScope
    }

    override fun getDatabase(): IDatabase {
        TODO("Not yet implemented")
    }


    override fun getBrand(): String {
        return getBrandName()
    }

    override fun getModel(): String {
        return getModelName()
    }

    override fun getAppVersionCode(): Int {
        context.get()?.let {
            return getVersionCode(context = it)
        } ?: return 0
    }

    override fun getAppVersionName(): String {
        context.get()?.let {
            return getAppVersion(context = it)
        } ?: return ""
    }

    override fun getDeviceId(): String? {
        context.get()?.let {
            return getDeviceId(context = it)
        } ?: return ""
    }

    override fun getApplicationName(): String {
        return context.get()?.let(::getAppName) ?: ""
    }


    override fun getAdvertiserId(): String? {
        TODO("Not yet implemented")
    }

    override fun getImei(): String {
        context.get()?.let {
            return getIMEI(context = it)
        } ?: return ""
    }

    override fun getMacAddress(): String? {
        TODO("Not yet implemented")
    }

    override fun getProductName(): String {
        return getProduct()
    }

    override fun getDeviceVersion(): String {
        return Build.VERSION.SDK_INT.toString()
    }

    override fun getDeviceManufacturer(): String {
        return getManufacturer()
    }

    override fun getDevicePlatform(): String {
        return getOperatingSystemName()
    }

    override fun getBuildNumber(): String {
        return getDeviceBuildNumber()
    }

    override fun getDeviceResolution(): String {
        context.get()?.let {
            return getScreenResolution(context = it)
        } ?: return ""
    }

    override fun getDeviceModel(): String? {
        return "Sample"//TODO("Not yet implemented")
    }

    override fun getDeviceOsVersion(): String? {
        val fields = Build.VERSION_CODES::class.java.fields
        for (field in fields) {
            val fieldName = field.name
            var fieldValue = -1
            try {
                fieldValue = field.getInt(Any())
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
            if (fieldValue == Build.VERSION.SDK_INT) {
                return fieldName
            }
        }
        return Build.VERSION.RELEASE
    }

    override fun getDeviceOs(): String {
        return getOperatingSystemName()
    }


    override fun getConnectionType(): String? {
        TODO("Not yet implemented")
    }

    override fun getOperatorName(): String? {
        TODO("Not yet implemented")
    }

    override fun getUserAgent(): String? {
        TODO("Not yet implemented")
    }

    override fun isEmulator(): String? {
        TODO("Not yet implemented")
    }

    override fun isRooted(): Int {
        TODO("Not yet implemented")
    }

    override fun getEncValue(value: String?): String? {
        TODO("Not yet implemented")
    }

    override fun getDecryptedValue(encryptedValue: String?): String? {
        TODO("Not yet implemented")
    }

    override fun getDecryptedValue(encryptedString: String?, secretKey: String?): String? {
        TODO("Not yet implemented")
    }

    override fun getDecryptedValueUsingAes(encryptedValue: String?): String? {
        TODO("Not yet implemented")
    }

    override fun getEncryptedValueUsingAes(plainText: String?): String? {
        TODO("Not yet implemented")
    }

    override fun intIMEI(): String? {
        TODO("Not yet implemented")
    }

    override fun getPackageSHA1(): String? {
        TODO("Not yet implemented")
    }

    override fun getLocale(): String {
        if (!::deviceLocale.isInitialized)
            deviceLocale = Locale.getDefault().country
        return deviceLocale
    }

    override fun getLocaleFullName(): String {
        if (!::deviceLocaleDisplay.isInitialized)
            deviceLocaleDisplay = Locale.getDefault().displayCountry
        return deviceLocaleDisplay
    }

    override fun getDeviceWidth(): Int {
        TODO("Not yet implemented")
    }

    override fun getMD5TextOfString(payload: String?): String? {
        TODO("Not yet implemented")
    }

    override fun getProcessorArch(): String? {
        TODO("Not yet implemented")
    }

    override fun getPackageName(): String {
        return context.get()?.let {
            getPackageName(it)
        } ?: ""
    }

    override fun setLocation(address: Address) {
        userLocation = address
    }

    override fun setLatLong(latitude: Double, longitude: Double) {
        //TODO: check this function
        try {
            getContext()?.let {
                getLocationFromLatLong(it, latitude, longitude)
            }
                ?.let {
                    setLocation(it)
                }
        } catch (e: Exception) {
            Log.d("IDevice", "setLatLong call error with exception : ${e.message}")
        }

    }

    override fun getLocation(): Address {
        return userLocation
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun isNetworkConnected(): Boolean {
        return isNetworkConnected(context.get()!!)
    }
}