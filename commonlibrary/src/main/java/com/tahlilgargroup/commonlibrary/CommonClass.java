package com.tahlilgargroup.commonlibrary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.microsoft.appcenter.analytics.Analytics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.sql.Ref;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.util.PersianCalendar;

import static android.location.GpsStatus.GPS_EVENT_STARTED;
import static android.location.GpsStatus.GPS_EVENT_STOPPED;


public class CommonClass {
    private static String[] persianNumbers = new String[]{"۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹"};
    private static final int BUFFERSZ = 32768;
    private byte[] buffer = new byte[BUFFERSZ];
    boolean origdeleted;
    int copylength;
    boolean restoredone;

    public static String DB_NAME = "DistributionDB";
    public static String FilesPath = Environment.getExternalStorageDirectory().toString() + "/tahlilgar";


    public static final Integer LOCATION_FINE = 1;
    public static final Integer CALL = 2;
    public static final Integer WRITE_EXST = 3;
    public static final Integer READ_EXST = 4;
    public static final Integer CAMERA = 5;
    public static final Integer INTERNET = 6;
    public static final Integer GPS_SETTINGS = 7;
    public static final Integer AUDIO = 8;
    public static final Integer LOCATION_COARSE = 9;
    public static final Integer INTERNET_STATE = 10;

    public static String DeviceProperty = "DefaultDevice";

    public static Location mCurrentLocation;


    // public static String WebServiceUrl="http://signalrwebservice.tahlilgargroup.ir/";
    // public static String FilesURL="http://testwebserv.tahlilgargroup.ir/Files/";


    public enum ToastMessages {permission_Denied, Network_Problem, Is_Disconnect, Is_InRest}

    public enum TypeToShare {text, Image, Video, Voice, Apk}

    /*  public static boolean has_AUDIO_Permission = false;
      public static boolean has_FINE_LOCATION_Permission = false;
      public static boolean has_COARSE_LOCATION_Permission = false;
      public static boolean has_CALL_Permission = false;
      public static boolean has_WRITE_EXST_Permission = false;
      public static boolean has_READ_EXST_Permission = false;
      public static boolean has_CAMERA_Permission = false;
      public static boolean has_GPS_SETTINGS_Permission = false;
      public static boolean has_INTERNET_Permission = false;
      public static boolean has_INTERNET_STATE_Permission = false;

      public static final String isLogin = "IsLogin";*/
    public static final String App_Prefrence = "MyPrefers";


    public static final String App_version = "MyAppVersion";
    public static String DeviceIMEI = "";
    public static String DeviceName = "";

    /**
     * show message
     *
     * @param context
     * @param text
     * @param timeType
     */
    public void ShowToast(Context context, String text, int timeType) {
        try {

            if (context != null)
                Toast.makeText(context, text, timeType == 0 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Analytics.trackEvent("CommonClass_" + "ShowToast1 " + "_" + CommonClass.GetCurrentMDate() + "_" + e.getMessage());
        }
    }

    public void ShowToast(Context context, ToastMessages type, String message) {
        try {
            if (context != null)
                switch (type) {
                    case permission_Denied:
                        ShowToast(context, context.getString(R.string.takeAccess), 0);
                        break;
                    case Network_Problem:
                        ShowToast(context, context.getString(R.string.NetworkProblem) + "\n " + message, 0);
                        break;
                    case Is_Disconnect:
                        ShowToast(context, context.getString(R.string.Disconnected), 0);
                        break;
                    case Is_InRest:
                        ShowToast(context, context.getString(R.string.FinshStop), 0);
                        break;
                }
        } catch (Exception e) {
            Analytics.trackEvent("CommonClass_" + "ShowToast2 " + "_" + CommonClass.GetCurrentMDate() + "_" + e.getMessage());
        }
    }

    /**
     * change numbers to persian font
     *
     * @param text
     * @return
     */
    public String PerisanNumber(String text) {
        if (text.length() == 0) {
            return "";
        }
        String out = "";
        try {

            int length = text.length();
            for (int i = 0; i < length; i++) {
                char c = text.charAt(i);
                if ('0' <= c && c <= '9') {
                    int number = Integer.parseInt(String.valueOf(c));
                    out += persianNumbers[number];
                } else if (c == '٫') {
                    out += '،';
                } else {
                    out += c;
                }
            }
        } catch (Exception e) {
            Analytics.trackEvent("CommonClass_" + "PerisanNumber " + "_" + CommonClass.GetCurrentMDate() + "_" + e.getMessage());
        }
        return out;
    }

    /**
     * set DeviceProperty variable
     */
    public void setDeviceProperty() {
        if (DeviceProperty.equals("DefaultDevice")) {
            int currentapiVersion = 0;
            try {
                currentapiVersion = Build.VERSION.SDK_INT;

            } catch (Exception e) {
                Analytics.trackEvent("CommonClass_setDeviceProperty " + "_" + CommonClass.GetCurrentMDate() + "_" + e.getMessage());

            }
            DeviceProperty = getDeviceName() + "_" + currentapiVersion;
        }


    }


    public static final String Language_Pref = "Lan";
    public static final String LanguageType = "LanguageType";

    /**
     * set application language
     *
     * @param myContext
     * @return
     */
    public String GetLanguage(Context myContext) {
        String Lng = "us";
        try {
            SharedPreferences shPref = myContext.getSharedPreferences(Language_Pref, Context.MODE_PRIVATE);
            if (shPref.contains(LanguageType)) {
                Lng = shPref.getString(LanguageType, "us");

            } else {
                //shPref = getSharedPreferences(DistributionClass.Language_Pref, Context.MODE_PRIVATE);
                SharedPreferences.Editor sEdit = shPref.edit();
                sEdit.putString(LanguageType, "us");
                sEdit.apply();

                Lng = "us";
            }
        } catch (Exception ex) {
            Analytics.trackEvent("DistributionClass" + "_" + "GetLanguage" + "_" + DeviceProperty + "_" + CommonClass.GetCurrentMDate() + "_" + ex.getMessage());
        }

        return Lng;
    }

    /**
     * get backup from db
     *
     * @param context
     * @param DBName
     */
    public void GetBackup(Context context, String DBName) {
        // Local database
        InputStream input = null;
        try {
            input = new FileInputStream(context.getDatabasePath(DBName).toString());
            // input = new FileInputStream("/data/data/com.example.signalrchat/databases/DriverMessage.db");


            // create directory for backup
      /*  File dir = new File(DB_BACKUP_PATH);
        dir.mkdir();*/

            // Path to the external backup
            OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/database_copy.db");

            // transfer bytes from the Input File to the Output File
            byte[] buffer = new byte[1024];
            int length;

            try {
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                output.flush();
                output.close();
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * convert voice to text
     */
    public void voiceTotxt(Context context) {

        //MainActivity.ComObj.voiceTotxt(myContext);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);


        final String Lng = GetLanguage(context);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Lng);

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Learnfiles.com");

        try {
            ((Activity) context).startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException e) {
            ShowToast(context, context.getString(R.string.DontSupportYourDevice), Toast.LENGTH_SHORT);
        }



       /* Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa");

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Learnfiles.com");

        try {
            ((Activity) context).startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException e) {
            ShowToast(context, context.getString(R.string.DontSupportYourDevice), Toast.LENGTH_SHORT);
        }*/
    }

    /**
     * restore db backup
     *
     * @param context
     * @param DBName
     */
    public void restore(Context context, String DBName) {

        File dbfile = new File((context.getDatabasePath(DBName).toString()));
        if (dbfile.delete()) {
            origdeleted = true;
        }

        FileInputStream bkp = null;
        try {
            bkp = new FileInputStream(Environment.getExternalStorageDirectory() + "/database_copy.db");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        OutputStream restore = null;
        try {
            restore = new FileOutputStream(context.getDatabasePath(DBName).toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        copylength = 0;

        try {
            while ((copylength = bkp.read(buffer)) > 0) {
                restore.write(buffer, 0, copylength);
            }


            restore.flush();
            restore.close();
            restoredone = true;
            bkp.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean CheckForPermission(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {

            return false;
        } else
            return true;
    }

    public void askForPermission(Context context, String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode);
            }
        }
    }


    public String Shamsi_Date() {

        Calendar cal = Calendar.getInstance();
        int Day = cal.get(Calendar.DAY_OF_MONTH);
        int Month = cal.get(Calendar.MONTH) + 1;
        int Year = cal.get(Calendar.YEAR);
        int Day_Of_Year = cal.get(Calendar.DAY_OF_YEAR);

        //---------------------------------------------

        if (Day_Of_Year <= 80)
            Year -= 622;
        else
            Year -= 621;

        switch (Month) {
            case 1:
                if (Day < 21) {
                    Month = 10;
                    Day += 10;
                } else {
                    Month = 11;
                    Day -= 20;
                }
                break;

            case 2:
                if (Day < 20) {
                    Month = 11;
                    Day += 11;
                } else {
                    Month = 12;
                    Day -= 19;
                }
                break;

            case 3:
                if (Day < 21) {
                    Month = 12;
                    Day += 9;
                } else {
                    Month = 1;
                    Day -= 20;
                }
                break;

            case 4:
                if (Day < 21) {
                    Month = 1;
                    Day += 11;
                } else {
                    Month = 2;
                    Day -= 20;
                }
                break;

            case 5:
            case 6:
                if (Day < 22) {
                    Month -= 3;
                    Day += 10;
                } else {
                    Month -= 2;
                    Day -= 21;
                }
                break;

            case 7:
            case 8:
            case 9:
                if (Day < 23) {
                    Month -= 3;
                    Day += 9;
                } else {
                    Month -= 2;
                    Day -= 22;
                }
                break;

            case 10:
                if (Day < 23) {
                    Month = 7;
                    Day += 8;
                } else {
                    Month = 8;
                    Day -= 22;
                }
                break;

            case 11:
            case 12:
                if (Day < 22) {
                    Month -= 3;
                    Day += 9;
                } else {
                    Month -= 2;
                    Day -= 21;
                }
                break;
        }
        return String.valueOf(Year) + "/" + String.valueOf(Month) + "/" + String.valueOf(Day);

    }


    public static String Shamsi_Date2() {

        Calendar cal = Calendar.getInstance();
        int Day = cal.get(Calendar.DAY_OF_MONTH) + 1;
        int Month = cal.get(Calendar.MONTH) + 1;
        int Year = cal.get(Calendar.YEAR);
        int Day_Of_Year = cal.get(Calendar.DAY_OF_YEAR);

        //---------------------------------------------

        if (Day_Of_Year <= 80)
            Year -= 622;
        else
            Year -= 621;

        switch (Month) {
            case 1:
                if (Day < 21) {
                    Month = 10;
                    Day += 10;
                } else {
                    Month = 11;
                    Day -= 20;
                }
                break;

            case 2:
                if (Day < 20) {
                    Month = 11;
                    Day += 11;
                } else {
                    Month = 12;
                    Day -= 19;
                }
                break;

            case 3:
                if (Day < 21) {
                    Month = 12;
                    Day += 9;
                } else {
                    Month = 1;
                    Day -= 20;
                }
                break;

            case 4:
                if (Day < 21) {
                    Month = 1;
                    Day += 11;
                } else {
                    Month = 2;
                    Day -= 20;
                }
                break;

            case 5:
            case 6:
                if (Day < 22) {
                    Month -= 3;
                    Day += 10;
                } else {
                    Month -= 2;
                    Day -= 21;
                }
                break;

            case 7:
            case 8:
            case 9:
                if (Day < 23) {
                    Month -= 3;
                    Day += 9;
                } else {
                    Month -= 2;
                    Day -= 22;
                }
                break;

            case 10:
                if (Day < 23) {
                    Month = 7;
                    Day += 8;
                } else {
                    Month = 8;
                    Day -= 22;
                }
                break;

            case 11:
            case 12:
                if (Day < 22) {
                    Month -= 3;
                    Day += 9;
                } else {
                    Month -= 2;
                    Day -= 21;
                }
                break;
        }
        String day = "", month = "";
        if (Day < 10) day = "0" + String.valueOf(Day);
        else day = String.valueOf(Day);
        if (Month < 10) month = "0" + String.valueOf(Month);
        else month = String.valueOf(Month);
        return String.valueOf(Year) + "/" + month + "/" + day;

    }


    /**
     * close open keyboard (has error in some apis)
     *
     * @param context
     */
    public void closeKeyboard(Activity context) {
        View v = context.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    /**
     * Check If SD Card is present or not method
     */
    public static class CheckForSDCard {
        //Check If SD Card is present or not method
        public boolean isSDCardPresent() {
            if (Environment.getExternalStorageState().equals(

                    Environment.MEDIA_MOUNTED)) {
                return true;
            }
            return false;
        }
    }

    /**
     * calculate date and time difference
     *
     * @param dateStart
     * @param dateStop
     * @return
     */
    public float CalculateTimeDiffrence(String dateStart, String dateStop) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat myFormat = new SimpleDateFormat("yyyy/MM/dd");

        long diff = 123456789;
        boolean IsNewDay = false;

        try {
            Date date1 = myFormat.parse(dateStart);
            Date date2 = myFormat.parse(dateStop);
            diff = date2.getTime() - date1.getTime();
            System.out.println("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

            if (date2.compareTo(date1) > 0)
                IsNewDay = true;


        } catch (ParseException e) {
            e.printStackTrace();
        }

        float days = (diff / (1000 * 60 * 60 * 24));
        return IsNewDay ? 1 : days;


    }

    /**
     * get device api version
     *
     * @param context
     * @return
     */
    public PackageInfo GetAppVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int version = pInfo.versionCode;
            return pInfo;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    @SuppressLint("HardwareIds")
    public String getDeviceIMEI(Context context) {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                deviceUniqueIdentifier = tm.getDeviceId();
            }

        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    /**
     * if installed app isn't new version , go to login activity to download new version and continue
     *
     * @param context
     * @param isFromSplash
     * @param LoginIsActive
     * @param ActivityLogin
     */
    public void CheckAppVersion(Context context, boolean isFromSplash, boolean LoginIsActive, Class<?> ActivityLogin) {
        int InstallAppVersion;
        //گرفتن ورژن برنامه جاری
        PackageInfo packageInfo = GetAppVersion(context);
        long CurrentAppVersion = packageInfo.versionCode;

        //واکشی ورژن ذخیره شده در دیتابیس(ورژنی از برنامه که در گوشی نصب شده)
        SharedPreferences shPref = context.getSharedPreferences(CommonClass.App_Prefrence, Context.MODE_PRIVATE);
        if (shPref.contains(CommonClass.App_version)) {
            InstallAppVersion = shPref.getInt(CommonClass.App_version, 1);


            //اگر ورژن جدید هست ورژن فعلی ذخیره شود و دیتابیس نوسازی شود
            if (InstallAppVersion != CurrentAppVersion) {
                SharedPreferences.Editor sEdit = shPref.edit();
                sEdit.putInt(CommonClass.App_version, (int) CurrentAppVersion);
                sEdit.apply();
                context.deleteDatabase(CommonClass.DB_NAME);
                if (!LoginIsActive && !isFromSplash) {
                    context.startActivity(new Intent(context, ActivityLogin));
                    ((Activity) context).finishAffinity();
                    Process.killProcess(Process.myPid());
                }
            }
        } else {
            SharedPreferences.Editor sEdit = shPref.edit();
            sEdit.putInt(CommonClass.App_version, (int) CurrentAppVersion);
            sEdit.apply();

            if (!LoginIsActive && !isFromSplash) {
                context.startActivity(new Intent(context, ActivityLogin));
                ((Activity) context).finishAffinity();
                // android.os.Process.killProcess(android.os.Process.myPid());
            }

        }
    }

    public static String GetCurrentMDate() {
        try {
            Date c = Calendar.getInstance().getTime();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            return df.format(c);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * open url (for about us)
     *
     * @param context
     * @param url
     */
    public void OpenURL(Context context, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

    /**
     * check entered email format validate
     *
     * @param email
     * @return
     */
    public boolean isValidEmailId(String email) {
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    /**
     * get error message by code
     *
     * @param Code
     * @param context
     * @return
     */
    public String ErrorMessages(int Code, Context context) {
        String msg = "";
        switch (Code) {

            case 10:
                msg = context.getString(R.string.NoInformationFound);
                break;
            case 11:
                msg = context.getString(R.string.ErrorPerformingOperation);
                break;
            case 12:
                msg = context.getString(R.string.GeoNotFinde);
                break;
            case 13:
                msg = context.getString(R.string.DestinationGeolocationNotFound);
                break;
            case 14:
                msg = context.getString(R.string.NoGeographicPointOfOriginOrDestinationFound);
                break;
            case 15:
                msg = context.getString(R.string.NoGeoPointFound);
                break;
            case 16:
                msg = context.getString(R.string.reachedYourDestination);
                break;
            case 17:
                msg = context.getString(R.string.NoSettingsFound);
                break;
            case 18:
                msg = context.getString(R.string.TheCodeEnteredIsIncorrect);
                break;
            case 19:
                msg = context.getString(R.string.InsertStarFields);
                break;
            case 20:
                msg = context.getString(R.string.PleaseRefresh);
                break;
            case 21:
                msg = context.getString(R.string.InvalidInput);
                break;
            case 22:
                msg = context.getString(R.string.InvalidAuthorization);
                break;
            case 24:
                msg = context.getString(R.string.CheckPermission);
                break;
            case 25:
                msg = context.getString(R.string.HaveNotPermission);
                break;


            case 21200101:
                msg = context.getString(R.string.AddressEditingConfirmed);
                break;
            case 21200102:
                msg = context.getString(R.string.EditOrderOfAddressesRejected);
                break;
            case 21200100:
                msg = context.getString(R.string.UpdateAddressOrder);
                break;
            case 11200101:
                msg = context.getString(R.string.UpdateCitiesList);
                break;
            case 11200201:
                msg = context.getString(R.string.UpdatingTheListOfProvinces);
                break;
            case 1120010:
            case 11200202:
                msg = context.getString(R.string.UpdateCustomerAddress);
                break;
            case 11200103:
                msg = context.getString(R.string.PleaseTryAgain);
                break;
            case 11200104:
                msg = context.getString(R.string.UpdateCauseListStop);
                break;
            case 11200204:
                msg = context.getString(R.string.FailureToUpdateList);
                break;
            case 11200304:
                msg = context.getString(R.string.UpdatingTheListOfPollTitles);
                break;
            case 11200301:
                msg = context.getString(R.string.SystemSettingsUpdate);
                break;
            case 11200401:
                msg = context.getString(R.string.UpdateApp);
                break;
            case 11200105:
                msg = context.getString(R.string.UpdateCauseListStop);
                break;
            case 11200205:
                msg = context.getString(R.string.FailureToUpdateList);
                break;
            case 11200305:
                msg = context.getString(R.string.UpdatingTheListOfPollTitles);
                break;
            case 21200203:
            case 21200205:
                msg = context.getString(R.string.UpdateDistributionList);
                break;
            case 11200405:
                msg = context.getString(R.string.UpdateCostList);
                break;


            case 200:
                msg = context.getString(R.string.DoneSuccessfully);
                break;
            case 500:
                msg = context.getString(R.string.ServerOprationFailed);
                break;
            case 400:
                msg = context.getString(R.string.ServerNotFound);
                break;
            case 404:
                msg = context.getString(R.string.ProblemSendingRequestToServer);
                break;
            case 600:
                msg = context.getString(R.string.CommunicationKeyNotEntered);
                break;

            case 601:
                msg = context.getString(R.string.CommunicationKeyIsNotCorrect);
                break;

            case 602:
                msg = context.getString(R.string.UserNotFoundWithThisProfile);
                break;

            case 603:
                msg = context.getString(R.string.ThePasswordDoesNotMatchTheIteration);
                break;

            case 604:
                msg = context.getString(R.string.TheNewWordOrPasswordIsEqualToItsCurrentValues);
                break;
            case 605:
                msg = context.getString(R.string.EnterTheCorrectUsernameOrPasswordCorrectly);
                break;
            case 606:
                msg = context.getString(R.string.InputParameterValuesAreIncorrect);
                break;
            case 607:
                msg = context.getString(R.string.ErrorRegisteringInformation);
                break;
            case 608:
                msg = context.getString(R.string.ThePasswordMustMatchThePatternMentioned);
                break;
            case 609:
                msg = context.getString(R.string.YouAreAlreadyRegistered);
                break;
            case 610:
                msg = context.getString(R.string.YouHaveNotAlreadyRegistered);
                break;
            case 611:
                msg = context.getString(R.string.ThisNameIsAlreadyRegistered);
                break;
            case 612:
                msg = context.getString(R.string.nvalidDate);
                break;
            case 613: //todo add parameter to set public this message
                msg = context.getString(R.string.IsOkDontAllowOperation);
                break;
            case 614:
                msg = context.getString(R.string.IncorrectCustomerPass);
                break;
            case 615:
                msg = context.getString(R.string.GetFileFailed);
                break;
            case 616:
                msg = context.getString(R.string.KalaNotFound);
                break;
            case 617:
                msg = context.getString(R.string.YouAreStop);
                break;
            case 618:
                msg = context.getString(R.string.YouAreNotStop);
                break;
            case 619:
                msg = context.getString(R.string.Score_Must_be_less_than_5);
                break;
            case 620:
                msg = context.getString(R.string.Payment_Err);
                break;
            case 621:
                msg = context.getString(R.string.Not_Found);
                break;
            case 622: //todo add parameter to set public this message
                msg = context.getString(R.string.Mojoodi_Is_0);
                break;
            case 623:
                msg = context.getString(R.string.Mande_Hesab_Problem);
                break;
            case 624:
                msg = context.getString(R.string.Entesab_Limmit_err);
                break;
            case 625:
                msg = context.getString(R.string.Setting_NotFound);
                break;


        }
        return msg;
    }

    /**
     * share from my app to others
     *
     * @param context
     * @param TxtShareBody
     * @param typeToShare
     */
    public void Share(Context context, String TxtShareBody, TypeToShare typeToShare) {
        try {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            String Type = "";
            switch (typeToShare) {
                case text: {
                    Type = "text/plain";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, TxtShareBody);

                    break;
                }
                case Image: {
                    Type = "image/*";
              /*  Uri uriToImage = null;
                // String path=FilesPath+TxtShareBody;
                File file = new File(TxtShareBody);
                if (file.exists()) {
                    uriToImage = Uri.fromFile(file);
                    if (uriToImage != null)
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                } else {
                    ShowToast(context, "لطفا ابتدا فایل را دریافت سپس امتحان کنید", Toast.LENGTH_LONG);
                }*/

                    break;
                }
                case Video: {
                    Type = "video/*";

                    break;
                }
                case Voice: {
                    Type = "audio/*";
                    break;
                }
                case Apk: {
                    Type = "application/vnd.android.package-archive";
                    break;
                }

            }
            switch (typeToShare) {
                case Image:
                case Video:
                case Voice: {
                    Uri uriToImage = null;
                    // String path=FilesPath+TxtShareBody;
                    File file = new File(TxtShareBody);
                    if (file.exists()) {
                        uriToImage = Uri.fromFile(file);
                        if (uriToImage != null)
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                    } else {
                        ShowToast(context, context.getString(R.string.PleaseDownloadTheFileFirst), Toast.LENGTH_LONG);
                    }
                }
            }
            if (!Type.equals("")) {

                shareIntent.setType(Type);
                // shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                if (shareIntent.getExtras() != null)
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.ShareWith)));
            }
        } catch (Exception e) {
            ShowToast(context, e.getMessage(), Toast.LENGTH_LONG);
        }


        // ActivityChat.context.startActivity(Intent.createChooser(shareIntent, ActivityChat.context.getResources().getText(R.string.send_to)));


        //Multi Selection Share

       /* ArrayList<Uri> imageUris = new ArrayList<Uri>();
        imageUris.add(imageUri1); // Add your image URIs here
        imageUris.add(imageUri2);

      //  Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        shareIntent.setType("image/*");
        ActivityChat.context.startActivity(Intent.createChooser(shareIntent, "Share images to.."));*/


        //Share with specific number in WhatsApp

          /*  PackageManager pm=ActivityChat.context.getPackageManager();
                String mensajillo =  messageItem.getCmdMessageData().getChatAppMsgDTO().getMsgContent();
                String toNumber = "+989138613849"; //The xs are replaced with the number
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + "" + toNumber + "?body=" + ""));
                sendIntent.setPackage("com.whatsapp");
                ActivityChat.context.startActivity(sendIntent);
                Intent texintent = new Intent(Intent.ACTION_VIEW);
                texintent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+mensajillo));
                ActivityChat.context.startActivity(texintent);*/
    }

    /**
     * share my app
     *
     * @param Subject
     * @param context
     * @return
     */
    public Intent ShareApp(String Subject, Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 0);
            File srcFile = new File(ai.publicSourceDir);
            Intent share = new Intent();
            share.setAction("android.intent.action.SEND");
            share.setType("application/vnd.android.package-archive");
            share.putExtra("android.intent.extra.STREAM", Uri.fromFile(srcFile));
            return Intent.createChooser(share, Subject);
        } catch (Exception e) {
            Analytics.trackEvent("Common_ShareApp " + "_" + CommonClass.GetCurrentMDate() + "_" + DeviceProperty + "_" + e.toString());
        }
        return null;
    }

    /**
     * Check if internet is present or not
     */
    public boolean isConnectingToInternet(Context context) {

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager
                        .getActiveNetworkInfo();
            }
            return networkInfo != null && networkInfo.isConnected();

        } catch (Exception e) {
            return true;
        }
    }

    private PersianDatePickerDialog picker;


    /**
     * گرفتن تاریخ از تقویم و ثبت در تکست باکس
     *
     * @param view
     * @param ColorID
     * @param context
     */
    public void getDate(final View view, int ColorID, Context context) {
        try {
            picker = new PersianDatePickerDialog(view.getContext())
                    .setPickerBackgroundColor(ColorID)
                    .setPositiveButtonString(context.getString(R.string.Ok))
                    .setNegativeButton(context.getString(R.string.Cancel))
                    .setTodayButton(context.getString(R.string.Today))
                    .setTodayButtonVisible(true)
                    .setMinYear(1300)
                    .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                    //.setInitDate(initDate)
                    .setActionTextColor(Color.GRAY)
                    //.setTypeFace(typeface)
                    .setListener(new Listener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDateSelected(PersianCalendar persianCalendar) {
                            if (view instanceof EditText) {
                                EditText textView = (EditText) view;

                                textView.setText(DateFormat(persianCalendar.getPersianYear() + "/" + persianCalendar.getPersianMonth() + "/" + persianCalendar.getPersianDay()));
                                //Do your stuff
                            }
                            // Toast.makeText(activity_filter.this, persianCalendar.getPersianYear() + "/" + persianCalendar.getPersianMonth() + "/" + persianCalendar.getPersianDay(), Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onDismissed() {

                        }
                    });

            picker.show();

        } catch (Exception e) {
            Analytics.trackEvent("CommonClass" + "_" + "getDate" + "_" + DeviceProperty + "_" + CommonClass.GetCurrentMDate() + "_" + e.getMessage());

        }


    }

    /**
     * show gps request message
     *
     * @param context
     */
    public void ActiveGPSMessage(final Context context) {
        try {

            SettingsClient mSettingsClient = LocationServices.getSettingsClient(context);
            LocationSettingsRequest mLocationSettingsRequest = null;

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            mLocationSettingsRequest = builder.build();


            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            // ایجاد عنوان برای دیالوگ
            // alertDialogBuilder.setTitle("خروج");

            // ایجاد پیام دیالوگ
            LocationSettingsRequest finalMLocationSettingsRequest = mLocationSettingsRequest;
            alertDialogBuilder
                    .setMessage(context.getString(R.string.PleaseTryAgainAfterTurningOnYourDeviceGPS))
                    .setCancelable(true)
                    .setPositiveButton("روشن کردن", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                            // درصورتی که برروی دکمه بله زده شود از برنامه خارج خواهد شد
                            // بستن اکتیویتی
                        /*
                    if(!MainActivity.GpsIsActive)
                    {
                        mSettingsClient
                                .checkLocationSettings(finalMLocationSettingsRequest)
                                .addOnSuccessListener((Executor) this, new OnSuccessListener<LocationSettingsResponse>() {
                                    @SuppressLint("MissingPermission")
                                    @Override
                                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {


                                        ComObj.askForPermission(context, Manifest.permission.ACCESS_FINE_LOCATION, CommonClass.LOCATION_FINE);
                                        ComObj.askForPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION, CommonClass.LOCATION_COARSE);



                                    }
                                })
                                .addOnFailureListener((Executor) this, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        int statusCode = ((ApiException) e).getStatusCode();
                                        switch (statusCode) {
                                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                                try {
                                                    // Show the dialog by calling startResolutionForResult(), and check the
                                                    // result in onActivityResult().
                                                    ResolvableApiException rae = (ResolvableApiException) e;
                                                    rae.startResolutionForResult((Activity) context, 100);
                                                } catch (IntentSender.SendIntentException sie) {
                                                    Analytics.trackEvent("MainAC_startLocationUpdates " + driverID + "_" + CommonClass.GetCurrentMDate() + "_"+DeviceProperty + "_" + e.getMessage());

                                                }
                                                break;
                                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                                String errorMessage = "تنظیمات مکان نما ناکافیست. لطفا آن را از تنظیمات دستگاه تنظیم کنید.";

                                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                    }*/

                        }
                    });

            // ایجاد دیالوگ
            AlertDialog alertDialog = alertDialogBuilder.create();

            // نمایش دیالوگ
            alertDialog.show();
        } catch (Exception e) {
            Analytics.trackEvent("Common_ActiveGPSMessage " + "_" + CommonClass.GetCurrentMDate() + "_" + DeviceProperty + "_" + e.toString());
        }
    }


    public void DetectGPSTurn(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }
        if (lm != null) {
            lm.addGpsStatusListener(new GpsStatus.Listener() {
                public void onGpsStatusChanged(int event) {
                    switch (event) {
                        case GPS_EVENT_STARTED: {
                            //alertDialog.cancel();
                            //  MainActivity.GpsIsActive=true;
                            break;

                        }
                        case GPS_EVENT_STOPPED: {
                            //  MainActivity.GpsIsActive=false;
                            // alertDialog.show();
                            break;
                        }
                    }
                }
            });
        }
    }

    /**
     * check gps is turn on or not
     *
     * @param context
     */
    public boolean GpsIsActive(Context context) {
        boolean result = false;
        try {

            final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            if (manager != null) {
                result = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
        } catch (Exception e) {
            Analytics.trackEvent("Common_GpsIsActive " + "_" + CommonClass.GetCurrentMDate() + "_" + DeviceProperty + "_" + e.toString());

        }
        return result;
        // buildAlertMessageNoGps();
    }


    private void buildAlertMessageNoGps(Context context) {
      /*  final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();*/
    }

    /**
     * password format is validate
     *
     * @param password
     * @param pattern
     * @return
     */
    public boolean validatePasswordType(final String password, String pattern) {
        String PASSWORD_PATTERN = "";
        if (pattern.equals("")) {
            /**
             *  (?=.*\d)      #  must contains one digit from 0-9
             *   (?=.*[a-z])   #   must contains one lowercase characters
             *   (?=.*[A-Z])   #   must contains one uppercase characters
             *   (?=.*[@#$%])  #   must contains one special symbols in the list "@#$%"
             *               . #   match anything with previous condition checking
             *   {6,20}        #   length at least 6 characters and maximum of 20
             */
            PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,12})";
        } else {
            PASSWORD_PATTERN = pattern;
        }

        return PASSWORD_PATTERN.matches(password);
    }

    static ProgressDialog pd;

    public void CancelWaitingDialog() {

        try {
            if (pd != null && pd.isShowing())
                pd.cancel();
        } catch (final Exception e) {
            // Handle or log or ignore
        } finally {
            pd = null;
        }
    }

    public void ShowWaitingDialog(Context context, String Message) {
        if (context != null) {
            try {
                pd = new ProgressDialog(context);
                if (Message.equals(""))
                    Message = context.getString(R.string.PleaseWaitUntilTheEndOfTheOperation);
                pd.setMessage(Message);
                pd.setCancelable(false);
                pd.show();

            } catch (Exception e) {
                Analytics.trackEvent("CustomerClass" + "_" + "ShowWaitingDialog" + "_" + DeviceProperty + "_" + CommonClass.GetCurrentMDate() + "_" + e.getMessage());
            }
        }
    }

    /**
     * برداشتن صفر اضافی و 3رقمی جدا کردن قیمت ها
     **/
    public void PriceView(String Price, TextView txtPrice, boolean showPersian) {
        try {
            String m = Price.contains(".") ?
                    Price.substring(0, Price.indexOf('.')) :
                    Price;
            String mab = NumberFormat.getNumberInstance(Locale.US).format(BigInteger.valueOf(Long.valueOf(m)));
            txtPrice.setText(showPersian ? PerisanNumber(mab) : mab);
        } catch (Exception e) {
            Analytics.trackEvent("CustomerClass" + "_" + "PriceView" + "_" + DeviceProperty + "_" + CommonClass.GetCurrentMDate() + "_" + e.getMessage());
        }
    }

    /**
     * remove dot from double number
     *
     * @param Price
     * @return
     */
    public String DelDotFromDouble(String Price) {
        String m = "";
        try {
            m = Price.contains(".") ?
                    Price.substring(0, Price.indexOf('.')) :
                    Price;

        } catch (Exception e) {
            Analytics.trackEvent("CustomerClass" + "_" + "DelDotFromDouble" + "_" + DeviceProperty + "_" + CommonClass.GetCurrentMDate() + "_" + e.getMessage());
        }
        return m;
    }

    /**
     * set application language
     *
     * @param context
     */
    public void setLanguage(Context context) {
        String Lng = GetLanguage(context);

        Locale localeNew = new Locale(Lng);

        Locale.setDefault(localeNew);

        Resources res = context.getResources();

        Configuration newConfig = new Configuration(res.getConfiguration());
        newConfig.locale = localeNew;
        newConfig.setLayoutDirection(localeNew);
        res.updateConfiguration(newConfig, res.getDisplayMetrics());

        newConfig.setLocale(localeNew);
        context.createConfigurationContext(newConfig);
    }

    /**
     * set date format yyyy/M/d to yyyy/MM/dd
     *
     * @param date_
     * @return
     */
    public String DateFormat(String date_) {
        String date = "";
        try {

            String[] dateArray = date_.split("/");
            String year = dateArray[0];
            String M = dateArray[1].length() == 2 ? dateArray[1] : "0" + dateArray[1];
            String d = dateArray[2].length() == 2 ? dateArray[2] : "0" + dateArray[2];

            date = year + "/" + M + "/" + d;
        } catch (Exception e) {
            Analytics.trackEvent("CommonClass" + "_" + "DateFormat" + "_" + DeviceProperty + "_" + CommonClass.GetCurrentMDate() + "_" + e.getMessage());
        }

        return date;
    }

    public boolean getOpenApplication(Context context, String PackageName) {
        try {
            final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            final List<ActivityManager.RunningTaskInfo> recentTasks = Objects.requireNonNull(activityManager).getRunningTasks(Integer.MAX_VALUE);
            for (int i = 0; i < recentTasks.size(); i++) {
                String appId = "";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    if (recentTasks.get(i).baseActivity != null) {
                        appId = recentTasks.get(i).baseActivity.toShortString().substring(1, recentTasks.get(i).baseActivity.toShortString().indexOf("/"));
                    }
                }
                if (appId.equals(PackageName.trim())) {
                    return true;
                }
            }
        } catch (Exception e) {
            Analytics.trackEvent("CommonClass" + "_" + "getOpenApplication" + "_" + DeviceProperty + "_" + CommonClass.GetCurrentMDate() + "_" + e.getMessage());

        }

        return false;
    }

    public class AllowOpenCloseAppVM {
        public String ErMsg;
        public byte Res;

        public AllowOpenCloseAppVM() {
        }

        public String getErMsg() {
            return ErMsg;
        }

        public void setErMsg(String erMsg) {
            ErMsg = erMsg;
        }

        public byte getRes() {
            return Res;
        }

        public void setRes(byte res) {
            Res = res;
        }
    }

    public AllowOpenCloseAppVM AllowOpenCloseApp(Context context, String Path, boolean IsCloseRequest, String PackageName) {
        //byte result = 0;
        //0 not allow
        //1 allow
        //2 permission error
        //3 exception
        AllowOpenCloseAppVM result = new AllowOpenCloseAppVM();
        result.setErMsg("");
        result.setRes((byte) 0);

        try {

            if (new CommonClass().CheckForPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                if (new CommonClass().CheckForPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    File myDir = new File(CommonClass.FilesPath);
                    myDir.mkdirs();

                    File file = new File(Path/*CommonClass.FilesPath + "/" + "config.txt"*/);
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    if (IsCloseRequest) {

                        if (file.exists()) {
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(/*context.openFileOutput*/new FileOutputStream(Path/*CommonClass.FilesPath + "/" + "config.txt", Context.MODE_PRIVATE*/));
                            outputStreamWriter.write("0");
                            outputStreamWriter.close();

                        }
                        result.setRes((byte) 1);
                        return result;
                        // return 1;

                    } else {


                        String ret = "";

                        InputStream inputStream = new FileInputStream/*context.openFileInput*/(Path/*CommonClass.FilesPath + "/" + "config.txt"*/);

                        if (inputStream != null) {
                            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                            String receiveString = "";
                            StringBuilder stringBuilder = new StringBuilder();

                            while ((receiveString = bufferedReader.readLine()) != null) {
                                stringBuilder/*.append("\n")*/.append(receiveString);
                            }

                            inputStream.close();
                            ret = stringBuilder.toString().replace(" ", "").trim();

                            // String a= ret.substring(0, ret.length() - 1).trim();
                            if (ret.length() == 0 || ret.endsWith("0")) {
                                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(/*context.openFileOutput*/new FileOutputStream(Path/*CommonClass.FilesPath + "/" + "config.txt", Context.MODE_PRIVATE*/));
                                outputStreamWriter.write("1");
                                outputStreamWriter.close();
                                result.setRes((byte) 1);
                                return result;
                                // return 1;
                            } else {
                                result.setRes((byte) 0);
                                return result;
                                //return 0;
                            }

                        }
                    }


                    return result;

                } else {
                    new CommonClass().ShowToast(context, CommonClass.ToastMessages.permission_Denied, "");

                    new CommonClass().askForPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE, CommonClass.READ_EXST);
                    // return 2;
                    result.setRes((byte) 2);
                    return result;
                }

            } else {
                new CommonClass().ShowToast(context, CommonClass.ToastMessages.permission_Denied, "");

                new CommonClass().askForPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, CommonClass.WRITE_EXST);
                //return 2;
                result.setRes((byte) 2);
                return result;
            }

        } catch (Exception e) {
            new CommonClass().ShowToast(context, e.getMessage(), Toast.LENGTH_LONG);

            Analytics.trackEvent("CommonClass" + "_" + "OpenExitApp" + "_" + DeviceProperty + "_" + CommonClass.GetCurrentMDate() + "_" + e.getMessage());
            //return 3;
            result.setRes((byte) 3);
            result.setErMsg(e.getMessage());

            return result;
        }

    }

}
