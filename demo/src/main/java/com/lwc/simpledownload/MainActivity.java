package com.lwc.simpledownload;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lwc.download.DownState;
import com.lwc.download.DownloadListener;
import com.lwc.download.DownloadManager;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Download";
    public static final int REQUEST_CODE = 100;

    private static TextView tvProgress1;
    private static TextView tvProgress2;
    private DownloadManager downloadManager = DownloadManager.getInstance();
    private String permissions[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final String FOLDER_NAME = "SimpleDownload";
    private String savePath;

    private final String url1 = "http://ccr.csslcloud.net/5D2636511DBBCADD/BBD5D1D6504FF2AD9C33DC5901307461/8DE588DAEE2FE914.ccr";
    private final String fileName1 = "1.file";
    private final String url2 = "http://ccr.csslcloud.net/5D2636511DBBCADD/BBD5D1D6504FF2AD9C33DC5901307461/D030B9064D6442EB.ccr";
    private final String fileName2 = "2.file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvProgress1 = findViewById(R.id.tvProgress1);
        tvProgress2 = findViewById(R.id.tvProgress2);

        setOnClickListeners();
        requestPermissions();
        savePath = getApplication().getExternalCacheDir() + File.separator + FOLDER_NAME;

        restoreDownState(url1, tvProgress1);
        restoreDownState(url2, tvProgress2);
    }

    private void restoreDownState(String url, TextView textView) {
        if (DownloadManager.getInstance().getTaskState(url) == DownState.DOWNLOADING) {
            DownloadManager.getInstance().updateStatusTextView(url, textView);
        } else if (DownloadManager.getInstance().getTaskState(url) == DownState.PAUSE) {
            DownloadManager.getInstance().updateStatusTextView(url, textView);
            textView.setText("暂停");
        }
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE ) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "没有权限保存文件", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void setOnClickListeners() {

        // 第一组
        findViewById(R.id.btnStart1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.download(MainActivity.this, url1, savePath, fileName1, tvProgress1, listener1);
            }
        });

        findViewById(R.id.btnStop1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.pauseDownload(url1);
            }
        });

        findViewById(R.id.btnContinue1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.continueDownload(url1);
//                downloadManager.continueDownload(url1, listener1);
            }
        });

        findViewById(R.id.btnRemove1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.cancelDownload(url1, false);
            }
        });


        // 第二组
        findViewById(R.id.btnStart2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.download(MainActivity.this, url2, savePath, fileName2, listener2);
            }
        });

        findViewById(R.id.btnStop2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.pauseDownload(url2);
            }
        });

        findViewById(R.id.btnContinue2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.continueDownload(url2, listener2);
            }
        });

        findViewById(R.id.btnRemove2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.cancelDownload(url2, true);
            }
        });
    }


    static DownloadListener listener1 = new DownloadListener() {
        @Override
        public void onStartDownload(TextView textView) {
            Log.d(TAG, "1 onStartDownload() called");
        }

        @Override
        public void onProgress(long downloaded, long total, TextView textView) {
            Log.d(TAG, "1 onProgress() called with: downloaded = [" + downloaded + "], total = [" + total + "]");
            textView.setText(getPercentString(downloaded, total));
        }

        @Override
        public void onPauseDownload(TextView textView) {
            Log.d(TAG, "1 onPauseDownload() called");
            textView.setText("暂停");
        }

        @Override
        public void onCancelDownload(TextView textView) {
            Log.d(TAG, "1 onCancelDownload() called");
            textView.setText("已取消");
        }

        @Override
        public void onFinishDownload(String file, TextView textView) {
            Log.d(TAG, "1 onFinishDownload() called:" + file);
            textView.setText("已完成");
        }

        @Override
        public void onFail(String errorInfo, TextView textView) {
            Log.d(TAG, "1 onFail() called with: errorInfo = [" + errorInfo + "]");
            textView.setText("下载出错");
        }
    };


    static DownloadListener listener2 = new DownloadListener() {
        @Override
        public void onStartDownload(TextView textView) {
            Log.d(TAG, "2 onStartDownload() called");
        }

        @Override
        public void onProgress(long downloaded, long total, TextView textView) {
            Log.d(TAG, "2 onProgress() called with: downloaded = [" + downloaded + "], total = [" + total + "]");
            tvProgress2.setText(getPercentString(downloaded, total));
        }

        @Override
        public void onPauseDownload(TextView textView) {
            Log.d(TAG, "2 onPauseDownload() called");
        }

        @Override
        public void onCancelDownload(TextView textView) {
            Log.d(TAG, "2 onCancelDownload() called");
        }

        @Override
        public void onFinishDownload(String file, TextView textView) {
            Log.d(TAG, "2 onFinishDownload() called:" + file);
        }

        @Override
        public void onFail(String errorInfo, TextView textView) {
            Log.d(TAG, "2 onFail() called with: errorInfo = [" + errorInfo + "]");
        }
    };

    private static String getPercentString(long val1, long val2) {
        if (val2 > 0) {
            int val = (int)(val1*100/val2);
            return val+"%";
        } else {
            if (val1 < 1024) {
                return val1+"bytes";
            } else if (val1 < 1024 * 1024) {
                return (val1/1024)+"KB";
            } else if (val1 < 1024 * 1024 * 1024) {
                return (val1/1024/1024)+"MB";
            }  else {
                return (val1/1024/1024/1024)+"GB";
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        downloadManager.pauseDownload(url1);
//        downloadManager.pauseDownload(url2);
    }
}
