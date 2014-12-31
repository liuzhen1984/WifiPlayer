package com.silveroak.wifiplayer.constants;

/**
 * Created by zliu on 14/12/29.
 */
public class SystemConstant {
    public final static String CURRENT_PLAYER_LIST_NAME = "current_player_list";

    public interface PORT{
        public final static int HTTP_SERVER_PORT = 8080;
        public final static int TCP_SERVER_PORT = 9898;
        public final static int UDP_CLIENT_TO_SERVER = 19898;
        public final static int UDP_SERVER_TO_CLIENT = 18989;

    }

    public enum PLAYER_TYPE{
        SINGLE(1),
        RANDOM(2),
        ORDER(3),
        ALL(4);
        private int type;
        PLAYER_TYPE(int type){
            this.type = type;
        }
        public int getType(){
            return type;
        }
    }

    public enum PLAYER_STATUS{
        PLAYER(1),
        STOP(2),
        PAUSED(3);
        private int type;
        PLAYER_STATUS(int type){
            this.type = type;
        }
        public int getType(){
            return type;
        }
    }
}
