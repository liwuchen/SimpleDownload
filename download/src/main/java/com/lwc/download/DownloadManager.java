package com.lwc.download;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * @Package: com.lwc.download
 * @ClassName: DownloadManager
 * @Description: 下载管理
 * @Author: liwuchen
 * @CreateDate: 2019/10/12
 */
public class DownloadManager {
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 99;
    private static final String TAG = "DownloadManager";
    private static final int DEFAULT_TIMEOUT = 40;
    private static final String BASE_URL = "http://www.baidu.com/";
    /*正在下载的队列*/
    private static HashMap<String, DownloadInfo> downloadMap;

    private volatile static DownloadManager INSTANCE;

    public static DownloadManager getInstance() {
        if (INSTANCE == null) {
            synchronized (DownloadManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DownloadManager();
                }
            }
        }
        return INSTANCE;
    }

    private DownloadManager() {
        downloadMap = new HashMap<>();
    }

    /**
     * 判断有无存储权限，没有的话就申请权限
     * @param context
     * @param url
     * @param filePath
     * @param fileName
     * @param listener
     */
    public void download(Activity context, @NonNull String url, final String filePath, final String fileName, final DownloadListener listener) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "请先获取存储权限，然后点击下载", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            startDownload(url, filePath, fileName, listener);
        }
    }

    /**
     * 下载文件
     * @param url 文件下载地址
     * @param filePath 文件保存路径（不以“/”结尾）
     * @param fileName 文件保存名称
     * @param listener 下载监听
     */
    private void startDownload(@NonNull String url, final String filePath, final String fileName, final DownloadListener listener) {
        DownloadInfo tempInfo = downloadMap.get(url);
        long start;
        boolean newTask;
        DownloadService downloadService;
        if (tempInfo != null) {
            if (tempInfo.getState() == DownState.DOWNLOADING) {
                //正在下载则不处理
                return;
            } else if (tempInfo.getState() == DownState.PAUSE){
                //从暂停处继续下载
                start = tempInfo.getReadLength();
                downloadService = tempInfo.getService();
                newTask = false;
            } else {
                //出错，或者下载已完成
                start = 0;
                downloadService = tempInfo.getService();
                newTask = true;
            }
        } else {
            //新的下载任务
            tempInfo = new DownloadInfo();
            tempInfo.setUrl(url);
            tempInfo.setSavePath(filePath);
            tempInfo.setFileName(fileName);
            tempInfo.setListener(listener);

            DownloadInterceptor interceptor = new DownloadInterceptor(new DownloadProgressListener(tempInfo, listener));
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .retryOnConnectionFailure(true)
                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .build();

            ExecutorService executorService = Executors.newFixedThreadPool(1);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .callbackExecutor(executorService) //设置CallBack回调在子线程进行
                    .build();
            downloadService = retrofit.create(DownloadService.class);
            tempInfo.setService(downloadService);
            start = 0;
            newTask = true;
        }
        final DownloadInfo downloadInfo = tempInfo;
        final boolean newFile = newTask;
        downloadService
                .rxDownload("bytes=" + start + "-", url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<ResponseBody, InputStream>() {
                    @Override
                    public InputStream apply(ResponseBody responseBody) throws Exception {
                        return responseBody.byteStream();
                    }
                })
                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) throws Exception {
                        FileUtils.writeFileFromIS(filePath, fileName, inputStream, newFile);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InputStream>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (listener != null) {
                            listener.onStartDownload();
                        }
                        downloadInfo.setDisposable(d);
                    }

                    @Override
                    public void onNext(InputStream stream) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (listener != null) {
                            listener.onFail(e.getMessage());
                            downloadInfo.setState(DownState.ERROR);
                        }
                    }

                    @Override
                    public void onComplete() {
                        downloadMap.remove(downloadInfo.getUrl());
                    }
                });
        downloadInfo.setState(DownState.DOWNLOADING);
        downloadMap.put(url, downloadInfo);
    }

    /**
     * 暂停下载
     * @param url 文件下载地址
     */
    public void pauseDownload(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if(downloadMap.containsKey(url)) {
            DownloadInfo downloadInfo = downloadMap.get(url);
            Disposable disposable = null;
            if (downloadInfo != null) {
                disposable = downloadInfo.getDisposable();
            }
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
                downloadInfo.setState(DownState.PAUSE);

                //延迟回调，解决disposable.dispose()调用后任务会继续执行若干时间的问题
                final DownloadListener listener = downloadInfo.getListener();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listener.onPauseDownload();
                    }
                }, 1000);
            }
        }
    }

    /**
     * 继续下载
     * @param url 文件下载地址
     */
    public void continueDownload(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if(downloadMap.containsKey(url)) {
            DownloadInfo downloadInfo = downloadMap.get(url);
            if (downloadInfo != null && downloadInfo.getState() == DownState.PAUSE) {
                startDownload(url, downloadInfo.getSavePath(), downloadInfo.getFileName(), downloadInfo.getListener());
            }
        } else {
            // 没有此任务
        }
    }

    /**
     * 继续下载
     * @param url 文件下载地址
     */
    public void continueDownload(final String url, final DownloadListener listener) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if(downloadMap.containsKey(url)) {
            DownloadInfo downloadInfo = downloadMap.get(url);
            if (downloadInfo != null && downloadInfo.getState() == DownState.PAUSE) {
                startDownload(url, downloadInfo.getSavePath(), downloadInfo.getFileName(), listener);
            }
        } else {
            // 没有此任务
        }
    }

    /**
     * 取消下载
     * @param url 文件下载地址
     * @param deleteFile 是否删除已下载的文件
     */
    public void cancelDownload(String url, boolean deleteFile) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if(downloadMap.containsKey(url)) {
            DownloadInfo downloadInfo = downloadMap.get(url);
            Disposable disposable = null;
            if (downloadInfo != null) {
                disposable = downloadInfo.getDisposable();
            }
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();

                //延迟回调，解决disposable.dispose()调用后任务会继续执行若干时间的问题
                final DownloadListener listener = downloadInfo.getListener();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listener.onCancelDownload();
                    }
                }, 1000);

            }
            downloadMap.remove(url);

            if (deleteFile && downloadInfo != null) {
                FileUtils.deleteFile(downloadInfo.getSavePath() + File.separator + downloadInfo.getFileName());
            }
        } else {
            // 没有此任务
        }
    }
}
