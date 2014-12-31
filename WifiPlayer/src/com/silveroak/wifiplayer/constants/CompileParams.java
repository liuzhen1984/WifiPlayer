package com.silveroak.wifiplayer.constants;

/**
 * Created by zliu on 2014/6/21.
 */
public interface CompileParams {
    public final boolean IS_REPLY_ACK = true;      //是否恢复ack
    public final boolean IS_RETRY = false;          //是否重发
    public final boolean IS_CRC_CHECK = false;     //是否检测CRC
    public final boolean IS_SEQ_CHECK = false;     //是否检测SEQ

    public final boolean IS_TO_UART = false;     //是否输出到串口

    public final boolean IS_DEBUG = true;
    public final boolean TO_WARN = true;//是否打印调试信息
    public final boolean TO_INFO = true;//是否打印info信息
    public final boolean TO_VERBOSE = true;//是否打印verbose信息
    public final boolean TO_ERROR = true;//是否打印error信息
    public final boolean TO_OPERAT = false;//是否打印error信息

}
