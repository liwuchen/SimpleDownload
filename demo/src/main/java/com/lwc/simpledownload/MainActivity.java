package com.lwc.simpledownload;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lwc.download.DownloadListener;
import com.lwc.download.DownloadManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Download";

    private TextView tvProgress1;
    private TextView tvProgress2;
    private TextView tvProgress3;
    private DownloadManager downloadManager = DownloadManager.getInstance();

    private String url1 = "https://codeload.github.com/square/okhttp/zip/master";
    private String fileName1 = "okhttp.zip";
    private String url2 = "https://codeload.github.com/square/picasso/zip/master";
    private String fileName2 = "picasso.zip";
    private String url3 = "https://codeload.github.com/square/retrofit/zip/master";
    private String fileName3 = "retrofit.zip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvProgress1 = findViewById(R.id.tvProgress1);
        tvProgress2 = findViewById(R.id.tvProgress2);
        tvProgress3 = findViewById(R.id.tvProgress3);

        setOnClickListeners();
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
        public void onProgress(int progress) {
            Log.d(TAG, "1 onProgress() called with: progress = [" + progress + "]");
            tvProgress1.setText(""+progress);
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
        public void onProgress(int progress) {
            Log.d(TAG, "2 onProgress() called with: progress = [" + progress + "]");
            tvProgress2.setText(""+progress);
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
        public void onProgress(int progress) {
            Log.d(TAG, "3 onProgress() called with: progress = [" + progress + "]");
            tvProgress3.setText(""+progress);
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
}
