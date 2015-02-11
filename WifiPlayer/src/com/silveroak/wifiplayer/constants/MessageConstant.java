package com.silveroak.wifiplayer.constants;

/**
 * Created by zliu on 15/2/9.
 */
public interface MessageConstant {
      public final static String HB_STR = "0-0";

    public enum  COMMAND_TYPE {
        FIND("find",""),
        CONFIG("config===","===");
        private String cmd;
        private String split;
        COMMAND_TYPE(String cmd,String split){
            this.cmd=cmd;
            this.split=split;
        }
    }
}
