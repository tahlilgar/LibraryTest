package com.tahlilgargroup.commonlibrary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.speech.RecognizerIntent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.microsoft.appcenter.analytics.Analytics;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static android.location.GpsStatus.GPS_EVENT_STARTED;
import static android.location.GpsStatus.GPS_EVENT_STOPPED;


public class CommonClass {
    private static String[] persianNumbers = new String[]{"۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹"};
    private static final int BUFFERSZ = 32768;
    private byte[] buffer = new byte[BUFFERSZ];
    boolean origdeleted;
    int copylength;
    boolean restoredone;

    public static String DB_NAME = "TransportDB";
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

    public static String DeviceProperty="DefaultDevice";

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
    public static final String Transport_Prefrence = "MyPrefers";


    public static final String App_version = "MyAppVersion";
    public static String DeviceIMEI = "";
    public static String DeviceName = "";


    public void ShowToast(Context context, String text, int timeType) {
        try {

            if (context != null)
                Toast.makeText(context, text, timeType == 0 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
        }catch (Exception e) {
            Analytics.trackEvent("CommonClass_" + "ShowToast1 " +  "_" + CommonClass.GetCurrentMDate() + "_" + e.getMessage());
        }
    }

    public void ShowToast(Context context, ToastMessages type, String message) {
        try {
            if (context != null)
                switch (type) {
                    case permission_Denied:
                        ShowToast(context, "لطفا ابتدا دسترسی های لازم را به برنامه اعطا کنید", 0);
                        break;
                    case Network_Problem:
                        ShowToast(context, "مشکل در برقراری ارتباط با شبکه" + "\n " + message, 0);
                        break;
                    case Is_Disconnect:
                        ShowToast(context, "اتصال برقرار نیست! \nبعد از برقراری اتصال تلاش کنید..", 0);
                        break;
                    case Is_InRest:
                        ShowToast(context, "لطفا قبل از انجام عملیات پایان توقف بزنید!", 0);
                        break;
                }
        }catch (Exception e) {
            Analytics.trackEvent("CommonClass_" + "ShowToast2 " +  "_" + CommonClass.GetCurrentMDate() + "_" + e.getMessage());
        }
    }

    //change numbers to persian font
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
        }catch (Exception e) {
            Analytics.trackEvent("CommonClass_" + "PerisanNumber " +  "_" + CommonClass.GetCurrentMDate() + "_" + e.getMessage());
        }
        return out;
    }

    public void setDeviceProperty() {
        if(DeviceProperty.equals("DefaultDevice"))
        {
            int currentapiVersion=0;
            try {
                currentapiVersion = Build.VERSION.SDK_INT;

            }catch (Exception e)
            {
                Analytics.trackEvent("CommonClass_setDeviceProperty " +  "_" + CommonClass.GetCurrentMDate() + "_" + e.getMessage());

            }
            DeviceProperty=getDeviceName()+"_"+currentapiVersion;
        }


    }

    //convert voice to text
    public void voiceTotxt(Context context) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa");

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Learnfiles.com");

        try {
            ((Activity) context).startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException e) {
            ShowToast(context, "دستگاه شما این امکان را پشتیبانی نمی کند!", Toast.LENGTH_SHORT);
        }
    }

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

    public void closeKeyboard(Activity context) {
        View v = context.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

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

    public float CalculateTimeDiffrence(String dateStart, String dateStop) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat myFormat = new SimpleDateFormat("yyyy/MM/dd");

        long diff = 123456789;

        try {
            Date date1 = myFormat.parse(dateStart);
            Date date2 = myFormat.parse(dateStop);
            diff = date2.getTime() - date1.getTime();
            System.out.println("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        float days = (diff / (1000 * 60 * 60 * 24));
        return days;


    }

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

    public void CheckAppVersion(Context context, boolean isFromSplash,boolean LoginIsActive,Class<?> ActivityLogin) {
        int InstallAppVersion;
        //گرفتن ورژن برنامه جاری
        PackageInfo packageInfo = GetAppVersion(context);
        long CurrentAppVersion = packageInfo.versionCode;

        //واکشی ورژن ذخیره شده در دیتابیس(ورژنی از برنامه که در گوشی نصب شده)
        SharedPreferences shPref = context.getSharedPreferences(CommonClass.Transport_Prefrence, Context.MODE_PRIVATE);
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

    public void OpenURL(Context context, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

    public boolean isValidEmailId(String email){
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public String ErrorMessages(int Code) {
        String msg = "";
        switch (Code) {

            case 10:
                msg = "اطلاعات یافت نشد!";
                break;
            case 11:
                msg = "خطا در انجام عملیات! \n";
                break;
            case 12:
                msg = "نقطه جغرافیایی مبدا یافت نشد!";
                break;
            case 13:
                msg = "نقطه جغرافیایی مقصد یافت نشد!";
                break;
            case 14:
                msg = "نقطه جغرافیایی مبدا یا مقصد یافت نشد!";
                break;
            case 15:
                msg = "نقطه جغرافیایی یافت نشد!";
                break;
            case 16:
                msg = "شما به مقصد رسیده اید! قبل از هر عملیاتی وضعیت سفر را مشخص کنید.";
                break;
            case 17:
                msg = "تنظیمات یافت نشد، لطفا پس از دریافت، مجددا امتحان کنید.";
                break;
            case 18:
                msg = "کد وارد شده صحیح نیست.";
                break;



            case 21200101:
                msg = "ویرایش ترتیب آدرس ها تایید شد!";
                break;
            case 21200102:
                msg = "ویرایش ترتیب آدرس ها رد شد!";
                break;
            case 21200100:
                msg = "بروزرسانی ترتیب آدرس ها!";
                break;
            case 11200101:
                msg = "بروزرسانی لیست شهرها!";
                break;
            case 11200201:
                msg = "بروزرسانی لیست استان ها!";
                break;
            case 11200102:
                msg = "بروزرسانی آدرس مشتری!";
                break;
            case 11200103:
                msg = "لطفا برای ورود مجدد تلاش کنید!";
                break;
            case 11200104:
                msg = "بروزرسانی لیست علت توقف!";
                break;
            case 11200204:
                msg = "بروزرسانی لیست علت عدم تحویل!";
                break;
            case 11200304:
                msg = "بروزرسانی لیست عناوین نظرسنجی!";
                break;
            case 11200301:
                msg = "بروزرسانی تنظیمات سیستم!";
                break;
            case 11200401:
                msg = "بروزرسانی اپلیکیشن!";
                break;


            case 200:
                msg = "عملیات با موفقیت انجام شد!";
                break;
            case 500:
                msg = "خطا در انجام عملیات سرور";
                break;
            case 400:
                msg = "سرور یافت نشد";
                break;
            case 404:
                msg = "مشکل در ارسال در خواست به سرور";
                break;
            case 600:
                msg = "کلید ارتباطی وارد نشده است!";
                break;

            case 601:
                msg = "کلید ارتباطی صحیح نیست!";
                break;

            case 602:
                msg = "کاربر با این مشخصات یافت نشد!";
                break;

            case 603:
                msg = "رمز عبور با تکرار آن مطابقت ندارد";
                break;

            case 604:
                msg = "کلمه و یا رمز عبور جدید با مقادیر فعلی آن برابر است";
                break;
            case 605:
                msg = "نام کاربری و یا رمز عبور قبلی را به درستی وارد کنید";
                break;
            case 606:
                msg = "مقادیر پارامترهای ورودی صحیح نیست";
                break;
            case 607:
                msg = "خطا در ثبت اطلاعات!";
                break;
            case 608:
                msg = "رمز باید مطابق الگوی ذکر شده باشد!";
                break;
            case 609:
                msg = "شما قبلا ثبت نام شده اید.\n لطفا در صورت فراموشی، برای بازیابی حسابتان از بازیابی رمز استفاده نمایید";
                break;
            case 610:
                msg = "شما قبلا ثبت نام نکرده اید. لطفا از کلید ثبت نام استفاده کنید";
                break;
            case 611:
                msg = "این نام قبلا ثبت شده است \nاگر مالک آن هستید از بازیابی رمز و در غیر اینصورت با نام جدید ثبت نام نمایید.";
                break;



        }
        return msg;
    }

    public void Share(Context context, String TxtShareBody, TypeToShare typeToShare) {
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
            case Voice:{
                Uri uriToImage = null;
                // String path=FilesPath+TxtShareBody;
                File file = new File(TxtShareBody);
                if (file.exists()) {
                    uriToImage = Uri.fromFile(file);
                    if (uriToImage != null)
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                } else {
                    ShowToast(context, "لطفا ابتدا فایل را دانلود کرده، سپس امتحان کنید", Toast.LENGTH_LONG);
                }
            }
        }
        if (!Type.equals("")) {

            shareIntent.setType(Type);
            // shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            if (shareIntent.getExtras() != null)
                context.startActivity(Intent.createChooser(shareIntent, "اشتراک گذاری با "));
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

    public Intent ShareApp(String Subject,Context context)
    {
        try
        {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 0);
            File srcFile = new File(ai.publicSourceDir);
            Intent share = new Intent();
            share.setAction("android.intent.action.SEND");
            share.setType("application/vnd.android.package-archive");
            share.putExtra("android.intent.extra.STREAM", Uri.fromFile(srcFile));
            return Intent.createChooser(share, Subject);
        }
        catch (Exception e)
        {
            Analytics.trackEvent("Common_ShareApp " +  "_" + CommonClass.GetCurrentMDate() + "_"+DeviceProperty  + "_" + e.toString());
        }
        return null;
    }

    //Check if internet is present or not
    private boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager
                    .getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }

    public void ActiveGPSMessage(final Context context)
    {
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
                    .setMessage("لطفا پس از روشن کردن مکان نمای دستگاه مجددا تلاش کنید!")
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
            Analytics.trackEvent("Common_ActiveGPSMessage " +  "_" + CommonClass.GetCurrentMDate() + "_"+DeviceProperty  + "_" + e.toString());
        }
    }

    public void DetectGPSTurn(Context context)
    {
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
                        case GPS_EVENT_STARTED:
                        {
                            //alertDialog.cancel();
                          //  MainActivity.GpsIsActive=true;
                            break;

                        }
                        case GPS_EVENT_STOPPED:
                        {
                          //  MainActivity.GpsIsActive=false;
                            // alertDialog.show();
                            break;
                        }
                    }
                }
            });
        }
    }

    public boolean GpsIsActive(Context context)
    {
        boolean result=false;
        try {

            final LocationManager manager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );

            if (manager != null) {
                result=  manager.isProviderEnabled( LocationManager.GPS_PROVIDER );
            }
        }
        catch (Exception e)
        {
            Analytics.trackEvent("Common_GpsIsActive " +  "_" + CommonClass.GetCurrentMDate() + "_"+DeviceProperty  + "_" + e.toString());

        }
        return  result;
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

    public boolean validatePasswordType(final String password,String pattern){
        String PASSWORD_PATTERN="";
        if(pattern.equals(""))
        {
            /**
             *  (?=.*\d)      #  must contains one digit from 0-9
             *   (?=.*[a-z])   #   must contains one lowercase characters
             *   (?=.*[A-Z])   #   must contains one uppercase characters
             *   (?=.*[@#$%])  #   must contains one special symbols in the list "@#$%"
             *               . #   match anything with previous condition checking
             *   {6,20}        #   length at least 6 characters and maximum of 20
             */
            PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,12})";
        }else
        {
            PASSWORD_PATTERN=pattern;
        }

        return PASSWORD_PATTERN.matches(password);
    }

    static ProgressDialog pd;

    public  void CancelWaitingDialog() {
        if (pd != null && pd.isShowing())
            pd.cancel();
    }

    public  void ShowWaitingDialog(Context context,String Message) {
        if (context != null) {

            pd = new ProgressDialog(context);
            if(Message.equals(""))
                Message="لطفا تا پایان عملیات منتظر بمانید...";
            pd.setMessage(Message);
            pd.setCancelable(false);
            pd.show();
        }
    }

/*
    public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
        if (ActivityCompat.checkSelfPermission(context, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case 1: {
                    CommonClass.has_FINE_LOCATION_Permission = true;
                    break;
                }
                case 2: {
                    CommonClass.has_CALL_Permission = true;
                    break;
                }
                case 3: {
                    CommonClass.has_WRITE_EXST_Permission = true;
                    break;
                }
                case 4: {
                    CommonClass.has_READ_EXST_Permission = true;
                    break;
                }
                case 5: {
                    CommonClass.has_CAMERA_Permission = true;
                    break;
                }
                case 6: {
                    CommonClass.has_INTERNET_Permission = true;
                    break;
                }
                case 7: {
                    CommonClass.has_GPS_SETTINGS_Permission = true;
                    break;
                }
                case 8: {
                    CommonClass.has_AUDIO_Permission = true;
                    break;
                }
                case 9:{
                    CommonClass.has_COARSE_LOCATION_Permission = true;
                    break;
                }case 10: {
                    CommonClass.has_INTERNET_STATE_Permission = true;
                    break;
                }

            }
        } else {
            MainActivity.ComObj.ShowToast(context, "مجوز دسترسی داده نشده است!", Toast.LENGTH_SHORT);

        }
    }
*/


}
