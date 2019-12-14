package com.lwc.download;

import java.io.IOException;

import io.reactivex.annotations.NonNull;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @Package: com.lwc.download
 * @ClassName: DownloadInterceptor
 * @Description: 带进度 下载 拦截器
 * @Author: liwuchen
 * @CreateDate: 2019/10/12
 */
public class DownloadInterceptor  implements Interceptor {

    private DownloadProgressListener listener;

    public DownloadInterceptor(DownloadProgressListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder()
                .body(new DownloadResponse(response.body(), listener)).build();
    }
}
