package com.lwc.download;

import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
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
    private static final String TAG = "DownloadManager";
    private static final int DEFAULT_TIMEOUT = 40;
    private static final String BASE_URL = "http://www.baidu.com/";
    public static final String ROOT_DIR_FILENAME = "download";
    /*正在下载的队列*/
    private HashMap<String, DownloadInfo> downloadMap;


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

    public void download(@NonNull String url, final String fileName, final DownloadListener listener) {
        DownloadInfo tempInfo = downloadMap.get(url);
        long start;
        DownloadService downloadService;
        if (tempInfo != null) {
            if (tempInfo.getState() == DownState.DOWNLOADING) {
                //正在下载则不处理
                return;
            } else if (tempInfo.getState() == DownState.PAUSE){
                //从暂停处继续下载
                start = tempInfo.getReadLength();
                downloadService = tempInfo.getService();
            } else {
                //出错，或者下载已完成
                start = 0;
                downloadService = tempInfo.getService();
            }
        } else {
            //新的下载任务
            tempInfo = new DownloadInfo();
            tempInfo.setUrl(url);
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
        }
        final DownloadInfo downloadInfo = tempInfo;
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
                        FileUtils.writeFileFromIS(fileName, inputStream);
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

    public void stopDownload(String url) {
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
            }
        }
    }

    public void continueDownload(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if(downloadMap.containsKey(url)) {
            DownloadInfo downloadInfo = downloadMap.get(url);
            if (downloadInfo != null && downloadInfo.getState() == DownState.PAUSE) {
                download(url, downloadInfo.getFileName(), downloadInfo.getListener());
            }
        } else {
            // 没有此任务
        }
    }

    public void removeDownload(String url) {
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
            }
            downloadMap.remove(url);
        } else {
            // 没有此任务
        }
    }
}
