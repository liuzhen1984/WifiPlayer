package com.silveroak.wifiplayer.service;

import android.content.Context;
import android.util.Xml;
import com.silveroak.wifiplayer.constants.SystemConstant;
import com.silveroak.wifiplayer.database.MusicHelper;
import com.silveroak.wifiplayer.database.PlayerListHelper;
import com.silveroak.wifiplayer.domain.muisc.MusicAPI;
import com.silveroak.wifiplayer.domain.muisc.PlayerList;
import com.silveroak.wifiplayer.service.business.*;
import com.silveroak.wifiplayer.utils.LogUtils;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zliu on 14/12/10.
 */
public class InitService {
    private final static String TAG = InitService.class.getSimpleName();
    public final static List<MusicAPI> MUSIC_API_LIST = new ArrayList<MusicAPI>();
    private final static List<IProcessService> PROCESS_SERVICES = new ArrayList<IProcessService>();//根据路径分发处理
    public static List<IProcessService> processServices(){
        return PROCESS_SERVICES;
    }
    public static void init(Context context){
        initMusicAPIs(context);
        PROCESS_SERVICES.add(MusicPlayerServer.getService(null,context));
        PROCESS_SERVICES.add(ListService.getService(context));
        PROCESS_SERVICES.add(MusicService.getService(context));
        PROCESS_SERVICES.add(APIService.getService(context));
         LogUtils.debug(TAG,"PS size:"+PROCESS_SERVICES.size());
        MusicHelper musicHelper = new MusicHelper(context);

        ConfigService.getConfigService(context).monitor();

    }

    public static void initPlayerList(Context context){
        PlayerListHelper playerListHelper = new PlayerListHelper(context);
        List<PlayerList> playerLists = playerListHelper.queryLast();
        if(playerLists.size()<1){
             PlayerList playerList = new PlayerList();
            playerList.setAliasName(SystemConstant.CURRENT_PLAYER_LIST_NAME);
            playerListHelper.insert(playerList);
        }
    }

    private static void initMusicAPIs(Context context){
        XmlPullParser parser = Xml.newPullParser();
        try {
            InputStream inputStream = context.getAssets().open("music_api.xml");
            parser.setInput(inputStream,"utf-8");

            int eventType = parser.getEventType();
            MusicAPI config = new MusicAPI();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("api")) {
                            config = new MusicAPI();
                        } else if (parser.getName().equals("alias_name")) {
                            config.setAliasName(parser.nextText());
                        } else if (parser.getName().equals("url")) {
                            config.setApiUrl(parser.nextText());
                        } else if (parser.getName().equals("ico_url")) {
                            config.setIcoUrl(parser.nextText());
                        } else if (parser.getName().equals("organizer_url")) {
                            config.setOrganizeUrl(parser.nextText());
                        }  else if (parser.getName().equals("organizer_name")) {
                            config.setOrganizeName(parser.nextText());
                        } else if (parser.getName().equals("organizer_desc")) {
                            config.setOrganizeDesc(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("book")) {
                            MUSIC_API_LIST.add(config);
                            config = null;
                        }
                        break;
                }
                eventType = parser.next();
            }


        } catch (Exception e) {
            LogUtils.error(TAG, e);
        }
    }

}
