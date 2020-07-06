package com.lwc.download;

/**
 * @Package: com.lwc.download
 * @ClassName: DownState
 * @Description: 下载状态枚举类
 * @Author: liwuchen
 * @CreateDate: 2019/10/15
 */
public enum  DownState {
    DEFAULT(0),
    DOWNLOADING(1),
    PAUSE(2),
    ERROR(3),
    FINISH(4);

    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    DownState(int state) {
        this.state = state;
    }
}
