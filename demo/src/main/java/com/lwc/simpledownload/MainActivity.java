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

import com.lwc.download.DownloadListener;
import com.lwc.download.DownloadManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Download";
    public static final int REQUEST_CODE = 100;

    private TextView tvProgress1;
    private TextView tvProgress2;
    private TextView tvProgress3;
    private DownloadManager downloadManager = DownloadManager.getInstance();
    private String permissions[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

//    private String url1 = "https://codeload.github.com/square/okhttp/zip/master";
//    private String fileName1 = "okhttp.zip";
//    private String url2 = "https://codeload.github.com/square/picasso/zip/master";
//    private String fileName2 = "picasso.zip";
//    private String url3 = "https://codeload.github.com/square/retrofit/zip/master";
//    private String fileName3 = "retrofit.zip";
    private String url1 = "http://ccr.csslcloud.net/5D2636511DBBCADD/BBD5D1D6504FF2AD9C33DC5901307461/8DE588DAEE2FE914.ccr";
    private String fileName1 = "1.file";
    private String url2 = "http://ccr.csslcloud.net/5D2636511DBBCADD/BBD5D1D6504FF2AD9C33DC5901307461/D030B9064D6442EB.ccr";
    private String fileName2 = "2.file";
    private String url3 = "http://ccr.csslcloud.net/5D2636511DBBCADD/BBD5D1D6504FF2AD9C33DC5901307461/D6A539AB16CD5B8B.ccr";
    private String fileName3 = "3.file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvProgress1 = findViewById(R.id.tvProgress1);
        tvProgress2 = findViewById(R.id.tvProgress2);
        tvProgress3 = findViewById(R.id.tvProgress3);

        setOnClickListeners();
        requestPermissions();
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
                downloadManager.download(url1, fileName1, listener1);
            }
        });

        findViewById(R.id.btnStop1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.stopDownload(url1);
            }
        });

        findViewById(R.id.btnContinue1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.continueDownload(url1);
            }
        });

        findViewById(R.id.btnRemove1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.stopDownload(url1);
            }
        });


        // 第二组
        findViewById(R.id.btnStart2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.download(url2, fileName2, listener2);
            }
        });

        findViewById(R.id.btnStop2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.stopDownload(url2);
            }
        });

        findViewById(R.id.btnContinue2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.continueDownload(url2);
            }
        });

        findViewById(R.id.btnRemove2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.stopDownload(url2);
            }
        });


        // 第三组
        findViewById(R.id.btnStart3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.download(url3, fileName3, listener3);
            }
        });

        findViewById(R.id.btnStop3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.stopDownload(url3);
            }
        });

        findViewById(R.id.btnContinue3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.continueDownload(url3);
            }
        });

        findViewById(R.id.btnRemove3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadManager.stopDownload(url3);
            }
        });
    }


    DownloadListener listener1 = new DownloadListener() {
        @Override
        public void onStartDownload() {
            Log.d(TAG, "1 onStartDownload() called");
        }

        @Override
        public void onProgress(long downloaded, long total) {
            Log.d(TAG, "1 onProgress() called with: downloaded = [" + downloaded + "], total = [" + total + "]");
            tvProgress1.setText(getPercentString(downloaded, total));
        }

        @Override
        public void onFinishDownload() {
            Log.d(TAG, "1 onFinishDownload() called");
        }

        @Override
        public void onFail(String errorInfo) {
            Log.d(TAG, "1 onFail() called with: errorInfo = [" + errorInfo + "]");
        }
    };


    DownloadListener listener2 = new DownloadListener() {
        @Override
        public void onStartDownload() {
            Log.d(TAG, "2 onStartDownload() called");
        }

        @Override
        public void onProgress(long downloaded, long total) {
            Log.d(TAG, "2 onProgress() called with: downloaded = [" + downloaded + "], total = [" + total + "]");
            tvProgress2.setText(getPercentString(downloaded, total));
        }

        @Override
        public void onFinishDownload() {
            Log.d(TAG, "2 onFinishDownload() called");
        }

        @Override
        public void onFail(String errorInfo) {
            Log.d(TAG, "2 onFail() called with: errorInfo = [" + errorInfo + "]");
        }
    };


    DownloadListener listener3 = new DownloadListener() {
        @Override
        public void onStartDownload() {
            Log.d(TAG, "3 onStartDownload() called");
        }

        @Override
        public void onProgress(long downloaded, long total) {
            Log.d(TAG, "3 onProgress() called with: downloaded = [" + downloaded + "], total = [" + total + "]");
            tvProgress3.setText(getPercentString(downloaded, total));
        }

        @Override
        public void onFinishDownload() {
            Log.d(TAG, "3 onFinishDownload() called");
        }

        @Override
        public void onFail(String errorInfo) {
            Log.d(TAG, "3 onFail() called with: errorInfo = [" + errorInfo + "]");
        }
    };

    private String getPercentString(long val1, long val2) {
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
}
