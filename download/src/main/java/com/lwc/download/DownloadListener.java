package com.lwc.download;

import android.widget.TextView;

/**
 * @Package: com.lwc.download
 * @ClassName: DownloadListener
 * @Description: 下载进度回调
 * @Author: liwuchen
 * @CreateDate: 2019/10/12
 */
public interface DownloadListener {

    void onStartDownload(TextView textView);

    void onProgress(long downloaded, long total, TextView textView);

    void onPauseDownload(TextView textView);

    void onCancelDownload(TextView textView);

    void onFinishDownload(String savedFile, TextView textView);

    void onFail(String errorInfo, TextView textView);
}
