package com.silveroak.playerclient.domain;

public class ErrorCode {

    public final static int SUCCESS = 0;
	
	public interface SYSTEM_ERROR{
		public final static int UNKNOWN_ERROR= 7100000;
		public final static int NOT_AUTH = 7100001;
		public final static int AUTH_FAILED = 7100002;
        public final static int ILLEGAL_VALUE = 7100003;
        public final static int MESSENGER_ERROR = 7100004;
        public final static int VALUE_ERROR = 7100005;
	}

    public interface USER_ERROR{
        public final static int URL_INVALID= 9100000;
        public final static int PARAMS_FORMAT= 9100001;
    }

}
