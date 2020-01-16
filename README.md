# SimpleDownload
简介：Retrofit+OkHttp+RxJava实现的下载工具类

### 一、本框架实现的功能特性
本下载框架实现了这样的特性：
<font color=#770000 >
1. 支持断点续传。即下载断开后，支持从已完成的部分开始继续下载。
2. 支持多个任务同时下载。
3. 有各种情况的回调。如下载进度回调，任务取消、完成等回调。
4. 下载任务在子线程进行，回调在主线程。不需要用户自己切换线程。
5. 用户可以自定义文件保存路径和文件名。
</font>

### 二、最终效果图
<img src=https://img-blog.csdnimg.cn/20191218203757945.gif width=200></img>

### 三、使用方法
如果想在Android Studio项目中使用本框架，下面是使用步骤：

1.项目根目录下的Build.gradle文件中修改：

```
allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://www.jitpack.io' } // 添加这行
    }
}
```
2.模块Build.gradle文件添加：

```
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    api 'com.github.liwuchen:SimpleDownload:1.1.2' 	// 添加这行
}
```
请使用最新版本。

3.添加权限（**读写文件的权限还需动态申请**，这里就不贴出了）：

```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

4.代码中使用起来也很简单：

```java
// 自定义文件夹名
private final String FOLDER_NAME = "SimpleDownload";
// 文件保存路径
private final String SAVE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + FOLDER_NAME;
// 文件下载地址
private final String url = "http://ccr.csslcloud.net/5D2636511DBBCADD/BBD5D1D6504FF2AD9C33DC5901307461/8DE588DAEE2FE914.ccr";
// 保存文件名
private final String fileName = "1.file";
```

```java
// 定义下载回调
DownloadListener listener = new DownloadListener() {
    @Override
    public void onStartDownload() {}
    
    @Override
    public void onProgress(long downloaded, long total) {}

    @Override
    public void onPauseDownload() {}

    @Override
    public void onCancelDownload() {}

    @Override
    public void onFinishDownload() {}

    @Override
    public void onFail(String errorInfo) {}
};
```

```java
DownloadManager downloadManager = DownloadManager.getInstance();
// 开始下载
downloadManager.download(url, SAVE_PATH, fileName, listener);
// 暂停下载
downloadManager.pauseDownload(url);
// 继续下载
downloadManager.continueDownload(url);
// 取消下载，不删除文件
downloadManager.cancelDownload(url, false);
// 取消下载，并删除文件
downloadManager.cancelDownload(url, true);
```
