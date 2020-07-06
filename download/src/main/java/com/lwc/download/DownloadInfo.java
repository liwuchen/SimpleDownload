package com.lwc.download;

import android.widget.TextView;

import io.reactivex.disposables.Disposable;

/**
 * @Package: com.lwc.download
 * @ClassName: DownloadInfo
 * @Description: 下载内容信息
 * @Author: liwuchen
 * @CreateDate: 2019/10/18
 */
public class DownloadInfo {
    /* 文件存储名 */
    private String fileName;
    /* 存储位置 */
    private String savePath;
    /* 文件总长度 */
    private long contentLength;
    /* 已下载长度 */
    private long readLength;
    /* 下载该文件的url */
    private String url;
    /* 复用Retrofit对象 */
    private DownloadService service;
    /* 被观察对象，用于取消下载 */
    private Disposable disposable;
    /* 下载状态 */
    private DownState state;
    /* 下载回调 */
    private DownloadListener listener;
    /* 可显示下载状态的TextView */
    private TextView stateTextView;

    public TextView getStateTextView() {
        return stateTextView;
    }

    public void setStateTextView(TextView stateTextView) {
        this.stateTextView = stateTextView;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public DownloadService getService() {
        return service;
    }

    public void setService(DownloadService service) {
        this.service = service;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public long getReadLength() {
        return readLength;
    }

    public void setReadLength(long readLength) {
        this.readLength = readLength;
    }

    public Disposable getDisposable() {
        return disposable;
    }

    public void setDisposable(Disposable disposable) {
        this.disposable = disposable;
    }

    public DownState getState() {
        return state;
    }

    public void setState(DownState state) {
        this.state = state;
    }

    public DownloadListener getListener() {
        return listener;
    }

    public void setListener(DownloadListener listener) {
        this.listener = listener;
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "fileName='" + fileName + '\'' +
                ", savePath='" + savePath + '\'' +
                ", contentLength=" + contentLength +
                ", readLength=" + readLength +
                ", url='" + url + '\'' +
                ", service=" + service +
                ", disposable=" + disposable +
                ", state=" + state +
                ", listener=" + listener +
                ", stateTextView=" + stateTextView +
                '}';
    }
}
