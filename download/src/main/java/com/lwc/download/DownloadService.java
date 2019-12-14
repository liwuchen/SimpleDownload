package com.lwc.download;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @Package: com.lwc.download
 * @ClassName: DownloadService
 * @Description: 下载请求
 * @Author: liwuchen
 * @CreateDate: 2019/10/12
 */
public interface DownloadService {
    /**
     * @param start 从某个字节开始下载数据
     */
    @Streaming
    @GET
    Observable<ResponseBody> rxDownload(@Header("RANGE") String start, @Url String url);

    @Streaming
    @GET
    Observable<ResponseBody> rxDownload(@Url String url);

}
