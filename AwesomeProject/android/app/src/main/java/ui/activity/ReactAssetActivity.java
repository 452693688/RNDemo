package ui.activity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import com.facebook.react.LifecycleState;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.JSBundleLoader;
import com.facebook.react.bridge.JSCJavaScriptExecutor;
import com.facebook.react.bridge.JavaScriptExecutor;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReactAssetActivity extends Activity implements DefaultHardwareBackBtnHandler {
    private final String TAG = "ReactAssetActivity";
    public final String ROOT_PATH = Environment.getExternalStorageDirectory().toString();
    public final String JS_BUNDLE_FILE = "index.android.bundle";
    public final String JS_BUNDLE_DOWN_PATH = ROOT_PATH + File.separator + "temp_" + JS_BUNDLE_FILE;
    public final String JS_BUNDLE_PATH = ROOT_PATH + File.separator + JS_BUNDLE_FILE;

    private ReactInstanceManager mReactInstanceManager;
    private ReactRootView mReactRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniReactRootView();
        setContentView(mReactRootView);
        updateBundle();
    }

    //实例化view
    private void iniReactRootView() {
        ReactInstanceManager.Builder builder = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setJSMainModuleName("index.android")
                .addPackage(new MainReactPackage())
                .setInitialLifecycleState(LifecycleState.RESUMED);

        File file = new File(JS_BUNDLE_PATH);
        //file=null;
        if (file != null && file.exists()) {
            builder.setJSBundleFile(JS_BUNDLE_PATH);
            Log.e(TAG, "读取sd卡的 index.android.bundle文件成功");
        } else {
            builder.setBundleAssetName(JS_BUNDLE_FILE);
            Log.e(TAG, "读取asset下的 index.android.bundle文件成功");
        }

        mReactRootView = new ReactRootView(this);
        mReactInstanceManager = builder.build();
        //加载js
        mReactRootView.startReactApplication(mReactInstanceManager, "AwesomeProject", null);
       // handler.sendEmptyMessageDelayed(1,2*1000);
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && mReactInstanceManager != null) {
            mReactInstanceManager.showDevOptionsDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostResume(this, new DefaultHardwareBackBtnHandler() {
                @Override
                public void invokeDefaultOnBackPressed() {
                    finish();
                }
            });
        }
    }

    //===========更新相关====================================
    private CompleteReceiver mDownloadCompleteReceiver;
    private long mDownloadId;
    private static final String downTestBundle = "https://raw.githubusercontent.com" +
            "/452693688/RNDemo/master/index.android.bundle";
    public static final String JS_BUNDLE_REMOTE_URL = downTestBundle;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mDownloadCompleteReceiver);
    }

    //更新index.android.bundle文件
    private void updateBundle() {
        initDownloadManager();
        File file = new File(JS_BUNDLE_PATH);
        if (file != null && file.exists()) {
            Log.e(TAG, "文件存在不更新!");
            return;
        }
        File downFile = new File(JS_BUNDLE_DOWN_PATH);
        if (downFile != null && downFile.exists()) {
            downFile.delete();
            Log.e(TAG, "下载临时文件存在，删除!");
        }

        //下载index.android.bundle文件
        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        // dm.remove();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(JS_BUNDLE_REMOTE_URL));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setDestinationUri(Uri.parse("file://" + JS_BUNDLE_DOWN_PATH));
        mDownloadId = dm.enqueue(request);
        Log.e(TAG, "下载开始");
    }

    //注册广播
    private void initDownloadManager() {
        mDownloadCompleteReceiver = new CompleteReceiver();
        registerReceiver(mDownloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    //下载完成广播
    private class CompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (completeDownloadId == mDownloadId) {
                //修改文件名称
                File file = new File(JS_BUNDLE_PATH);
                if (file != null && file.exists()) {
                    file.delete();
                    Log.e(TAG, "老文件文件存在，删除!");
                }
                File downFile = new File(JS_BUNDLE_DOWN_PATH);
                boolean isOk = downFile.renameTo(new File(JS_BUNDLE_PATH));
                Log.e(TAG, "件文件名称修改!" + isOk);
                //下载完成重新加载js
               // test();
               // onJSBundleLoadedFromServer();
            }
        }
    }

    private void test() {

    }

    //下载完成重新加载js
    private void onJSBundleLoadedFromServer() {
        File file = new File(JS_BUNDLE_PATH);
        if (file == null || !file.exists()) {
            Log.i(TAG, "sd卡没有index.android.bundle");
            return;
        }
        Log.e(TAG, "开始重新加载js");
        try {
            Class<?> RIManagerClazz = mReactInstanceManager.getClass();
            Method method = RIManagerClazz.getDeclaredMethod("recreateReactContextInBackground",
                    JavaScriptExecutor.class, JSBundleLoader.class);
            method.setAccessible(true);
            method.invoke(mReactInstanceManager,
                    new JSCJavaScriptExecutor(null),
                    JSBundleLoader.createFileLoader(getApplicationContext(), JS_BUNDLE_PATH));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
             onJSBundleLoadedFromServer();
        }
    };
}