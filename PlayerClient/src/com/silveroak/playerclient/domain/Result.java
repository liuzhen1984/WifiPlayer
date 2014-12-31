package com.silveroak.playerclient.domain;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: zliu
 * Date: 14-2-23
 * Time: 下午1:48
 * To change this template use File | Settings | File Templates.
 */
public class Result<T> implements Serializable  {
    /**
     *
     */
    private static final long serialVersionUID = 9115886871092320515L;
    private int what;//发送给Activity中的谁处理
    private int result;
    private long time;
    private T payload;  //可以隐藏

    @Override
    public String toString() {
        return "Result{" +
                "what=" + what +
                ", result=" + result +
                ", time=" + time +
                ", payload=" + payload +
                '}';
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = System.currentTimeMillis();
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

}
