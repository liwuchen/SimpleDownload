package com.lwc.download;

/**
 * @Package: com.lwc.download
 * @ClassName: DownloadListener
 * @Description: 下载进度回调
 * @Author: liwuchen
 * @CreateDate: 2019/10/12
 */
public interface DownloadListener {

    void onStartDownload();

    void onProgress(long downloaded, long total);

    void onPauseDownload();

    void onCancelDownload();

    void onFinishDownload(String savedFile);

    void onFail(String errorInfo);
}
