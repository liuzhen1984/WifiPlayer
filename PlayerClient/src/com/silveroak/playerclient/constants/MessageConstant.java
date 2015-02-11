package com.silveroak.playerclient.constants;

/**
 * Created by zliu on 15/2/9.
 */
public interface MessageConstant {
      public final static String HB_STR = "0-0";

    public enum  SEARCH_DEVICE_CMD {
        NO_FIND_DEVCIE("no_find_device"),
        IN_CONFIG_PAGE("in_config_page"),
        DO_CONFIG_WIFI("do_config_wifi"),
        COMPLETE("complete");
        private String cmd;
        SEARCH_DEVICE_CMD(String cmd){
            this.cmd=cmd;
        }
        public String getCmd(){
            return this.cmd;
        }
    }

}
