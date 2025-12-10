package com.application.ascend_android


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import java.util.Locale


fun gsonNumberPolicyBuilder(): Gson =
    GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create()

fun getPackageName(context: Context): String = context.packageName

fun getPackageInfo(context: Context): PackageInfo = context.packageManager.getPackageInfo(
    getPackageName(context), 0
)

fun getAppVersion(context: Context): String = getPackageInfo(context).versionName ?: "unknown"
fun getVersionCode(context: Context): Int = getPackageInfo(context).versionCode
fun getOperatingSystemName() = "Android"
fun getAppName(context: Context): String {
    val applicationInfo: ApplicationInfo = context.applicationInfo
    val stringId: Int = applicationInfo.labelRes
    return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
        stringId
    )

}

fun getManufacturer(): String = android.os.Build.MANUFACTURER ?: "unknown"

fun getOperatingSystemVersion(): String = android.os.Build.VERSION.RELEASE ?: "unknown"

fun getScreenResolution(context: Context) = context.resources.displayMetrics.let {
    "${it.heightPixels}x${it.widthPixels}"
}

fun getDeviceId(context: Context): String = Settings.Secure.getString(
    context.contentResolver,
    Settings.Secure.ANDROID_ID
)


@SuppressLint("MissingPermission")
fun getIMEI(context: Context): String {
    try {
        val telephonyManager = context.getSystemService(
            TELEPHONY_SERVICE
        ) as TelephonyManager
        return telephonyManager.deviceId
    } catch (ex: Exception) {
        return ""

    }
}


fun getNetworkProvider(context: Context): String? =
    (context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager).networkOperatorName

fun getProduct(): String = Build.PRODUCT ?: "unknown"
fun getModelName(): String = Build.MODEL ?: "unknown"
fun getBrandName(): String = Build.BRAND ?: "unknown"
fun getDeviceBuildNumber(): String = Build.DISPLAY ?: "unknown"

private fun isPermissionGranted(context: Context, permission: String) =
    context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED


fun getLocationFromLatLong(context: Context, latitude: Double, longitude: Double): Address {
    val addresses: List<Address>
    val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
    try {
        addresses = geocoder.getFromLocation(
            latitude,
            longitude,
            1
        )!! // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        return Address(Locale.getDefault()).apply {
            this.locality = addresses[0].locality
            this.adminArea = addresses[0].adminArea
            this.countryName = addresses[0].countryName
            this.postalCode = addresses[0].postalCode
            this.featureName = addresses[0].featureName

        }
    } catch (e: Exception) {
        Log.e("Plugger:::", "getFromLocation failed")
        return Address(Locale.getDefault())
    }
}

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun isNetworkConnected(context: Context): Boolean {
    if (isPermissionGranted(context, Manifest.permission.ACCESS_NETWORK_STATE)) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm.activeNetwork?.let {
                return true
            } ?: return false
        } else {
            return cm.activeNetworkInfo?.isConnectedOrConnecting ?: false
        }
    } else
        return false
}