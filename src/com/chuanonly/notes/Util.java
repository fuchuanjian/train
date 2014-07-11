package com.chuanonly.notes;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Util
{
    public static final String SPF_SETTING = "setting";
    public static final String SOUND = "sound";
    private static final String LOGIN_TIME = "login";
    
	private final static String name1 = "com";
	private final static String name2 = "chuanonly";
	private final static String name3 = "notes";
	private final static String namedot = ".";
	private final static String SIGN_DEBUG ="OpenSSLRSAPublicKey{modulus=a9884abe59d76edf8000746835fa4917d006ada2094d10f3ce7d2ec6a2a9e81690632b7d064928df72b596235b1eb30f059e0cb34c44505c90ee1fb896590d99461a8eb3f57d15a54551c66aa155c48bb3b3903b7c6a6125a721713f6bb803a0ae540680160b617fced388485a08b8d91bf4c1bdd74ce499927aaf7a48bec00a225d049827a1d6439fede9c38ccd549ef95deeb62ecbbb361340e4eef6817ea25dadece136f9dbbfe7d402aae84eb373951845fe2544d8d67b8e0258bb03916d92229e44f37b31df784ab76fa1f6598e3e743515980bcccf52f6ab2be53aabb5c9ca72b505b02651c230ef717004e4fe49845e576a866ca04fb00633a48554eb,publicExponent=10001}";
	private final static String SIGN_PUBLISH = "OpenSSLRSAPublicKey{modulus=a5101c4a7a8cb8cdda2974ad3faf2dead7e27b08baade7269388d07be4b87c41b068866a9b2026f341ce1c9ffbad5b54f004897e6f8013c03d3df328550a23aef256ca4d465ec3f203370ad9f2bd83a5e299e338a442506ca297f44f03f51ce63e1874b915414e390a0b780d921dd5631b365a97cbd2391f70afcb227eebb9b861080ad55212c3659632a4f3ed8fbb46286036aec64cb2b0208e42933072c3bc2462511e23b59223faf5c90c165a83e01b0c435da0df58354cffb0601df0aeeebc02cd66b88d28fda157a992b298d45195e88defe31320777e1305c5ee453df852ea879840a0ab1fa5cb262beb769ff7e988ce7660e44c991c46af12ec93e325,publicExponent=10001}";
	private final static String X5 ="X";
	private final static String X5end = "509";
    private static Toast toast;
    public static void showToast(String str) {
       showToast(str, 0);
    }
    public static void showToast(String str, int n) {
      int time = Math.min(1, Math.max(n, 0));
      try {
          if (toast == null) {
              toast = Toast.makeText(APP.getContext(), str, Toast.LENGTH_SHORT);
          }
          toast.setText(str);
          toast.setDuration(n);
          toast.show();
      } catch (Exception e) {
          // TODO: handle exception
      }
    }
    
    public static void release() {
        toast = null;
    }
    
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null) {
                return false;
            }
            NetworkInfo[] networkInfos = connectivity.getAllNetworkInfo();
            if (networkInfos == null) {
                return false;
            }
            for (NetworkInfo networkInfo : networkInfos) {
                if (networkInfo.isConnectedOrConnecting()) {
                    return true;
                }
            }
            return false;
        } catch (Throwable e) {
            return false;
        }
    }
    
    public static void setSoundSettingON(boolean isSoundON)
    {
    	boolean soundBool = isSoundSettingOn();
    	if (soundBool == isSoundON ) return;
    	SharedPreferences sp = APP.getContext().getSharedPreferences(SPF_SETTING, Context.MODE_PRIVATE );
    	Editor editor =  sp.edit();
    	editor.putBoolean(SOUND, isSoundON);
    	editor.commit();
    }
    public static boolean isSoundSettingOn()
    {
    	SharedPreferences sp = APP.getContext().getSharedPreferences(SPF_SETTING, Context.MODE_PRIVATE );
    	return sp.getBoolean(SOUND, true);
    }
    
    public static void setStringToSharedPref(String key, String value)
    {
    	SharedPreferences sp = APP.getContext().getSharedPreferences(SPF_SETTING, Context.MODE_PRIVATE );
    	Editor editor =  sp.edit();
    	editor.putString(key, value);
    	editor.commit();
    }
    
    
    public static void setIntToSharedPref(String key, int value)
    {
    	SharedPreferences sp = APP.getContext().getSharedPreferences(SPF_SETTING, Context.MODE_PRIVATE );
    	Editor editor =  sp.edit();
    	editor.putInt(key, value);
    	editor.commit();
    }
    public static void setLongToSharedPref(String key, long value)
    {
    	SharedPreferences sp = APP.getContext().getSharedPreferences(SPF_SETTING, Context.MODE_PRIVATE );
    	Editor editor =  sp.edit();
    	editor.putLong(key, value);
    	editor.commit();
    }
    
    public static String getStringFromSharedPref(String key, String defValue)
    {
    	SharedPreferences sp = APP.getContext().getSharedPreferences(SPF_SETTING, Context.MODE_PRIVATE );
    	return sp.getString(key, defValue);
    }
    public static int getIntFromSharedPref(String key, int defValue)
    {
    	SharedPreferences sp = APP.getContext().getSharedPreferences(SPF_SETTING, Context.MODE_PRIVATE );
    	return sp.getInt(key, defValue);
    }
	public static int getLoginCnt() {
		SharedPreferences sp = APP.getContext().getSharedPreferences(SPF_SETTING, Context.MODE_PRIVATE );
    	int cnt = sp.getInt(LOGIN_TIME, 0);
    	setLoginCnt(cnt + 1);
		return cnt;
	}
	public static void setLoginCnt(int cnt) 
	{
	   	SharedPreferences sp = APP.getContext().getSharedPreferences(SPF_SETTING, Context.MODE_PRIVATE );
    	Editor editor =  sp.edit();
    	editor.putInt(LOGIN_TIME, cnt);
    	editor.commit();
	}
	public static void checkSign() {
		PackageManager pm = APP.getContext().getPackageManager();
		PackageInfo packageinfo;
		boolean ok = false;
		try {
			packageinfo = pm.getPackageInfo(APP.getContext()
					.getPackageName(), PackageManager.GET_SIGNATURES);
			  Signature[] signs = packageinfo.signatures;      
		      Signature sign = signs[0];  
		      CertificateFactory certFactory = CertificateFactory
						.getInstance("X.509");
				X509Certificate cert = (X509Certificate) certFactory
						.generateCertificate(new ByteArrayInputStream(sign.toByteArray()));
				String pubKey = cert.getPublicKey().toString();
				String signNumber = cert.getSerialNumber().toString();   
				if (pubKey.equals(SIGN_PUBLISH) || pubKey.equals(SIGN_DEBUG))
				{					
				}else {
					System.exit(0);
				}
		} catch (Exception e) {
		}
	}
	public static void checkPkg() {
		PackageManager pm = APP.getContext().getPackageManager();
		PackageInfo packageinfo;
		String packageName = null;
		try {
			packageinfo = pm.getPackageInfo(APP.getContext()
					.getPackageName(), PackageManager.GET_SIGNATURES);
			packageName = packageinfo.packageName;			  
			if(!packageName.equals(name1+namedot+name2+namedot+name3))
			{
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
}
