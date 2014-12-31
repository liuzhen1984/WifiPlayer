package com.silveroak.wifiplayer.service.business;

import com.silveroak.wifiplayer.domain.Result;

/**
 * Created by zliu on 14/12/26.
 */
public interface IProcessService {
    String getUri();

    Result path(String type, String params);
}
