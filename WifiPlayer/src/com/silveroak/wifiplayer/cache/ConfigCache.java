package com.silveroak.wifiplayer.cache;

import com.silveroak.wifiplayer.domain.muisc.MusicAPI;
import com.silveroak.wifiplayer.service.InitService;

import java.util.List;

/**
 * Created by zliu on 14/12/10.
 */
public class ConfigCache {
    public static List<MusicAPI> getMusicAPIs(){
        return InitService.MUSIC_API_LIST;
    }
}
