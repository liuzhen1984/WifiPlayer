package com.silveroak.playerclient.service;

/**
 * Created by zliu on 14/12/30.
 */
public interface IHandlerWhatAndKey {
    public static final int PROCESSING = 0x1;
    public static final int UPDATE_INFO = 0x2;
    public static final int UPDATE_LIST = 0x3;
    public static final int UPDATE_MUSIC = 0x4;
    public static final int UPDATE_PLAY_INFO = 0x5;
    public static final int MESSAGE = -1;


    public static final int SEARCH_DEVICE_MESSAGE = 101;
    public static final int CONFIG_DEVICE_MESSAGE = 102;
    public static final int CONNECT_DEVICE_ERROR = 103;



    public static final String MESSAGE_KEY = "message_key";
}
