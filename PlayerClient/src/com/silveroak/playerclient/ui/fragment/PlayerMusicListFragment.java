package com.silveroak.playerclient.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.silveroak.playerclient.R;
import com.silveroak.playerclient.constants.SystemConstant;
import com.silveroak.playerclient.domain.Music;
import com.silveroak.playerclient.domain.PlayerInfo;
import com.silveroak.playerclient.service.business.PanelClient;
import com.silveroak.playerclient.service.business.SearchMusic;
import com.silveroak.playerclient.ui.activity.PlayerDeviceMusicPlayActivity;
import com.silveroak.playerclient.ui.activity.PlayerSearchDeviceActivity;
import com.silveroak.playerclient.ui.base.PlayerBaseFragment;
import com.silveroak.playerclient.ui.base.PlayerBaseSearchBarFragment;
import com.silveroak.playerclient.utils.JsonUtils;
import com.silveroak.playerclient.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 2015/2/11.
 */
public class PlayerMusicListFragment extends PlayerBaseSearchBarFragment {
    private final static String TAG = PlayerMusicListFragment.class.getSimpleName();
    private ListView musicListView;

    private PanelClient panelClient;
    private ImageButton playInfoButton;
    private TextView playInfoView;

    public static PlayerMusicListFragment newInstance() {
        return new PlayerMusicListFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(panelClient!=null){
            panelClient.sendTo("/play/list", "");
            syncInfo();
        }
    }
    private SystemConstant.PLAYER_STATUS PLAYER_STATUS= SystemConstant.PLAYER_STATUS.PAUSED;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_music_list, null);

        playInfoButton = (ImageButton) v.findViewById(R.id.into_player_bt);
        playInfoView = (TextView) v.findViewById(R.id.player_info_tv);

        View.OnClickListener von = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()) {
                    case R.id.into_player_bt:
                        if(PLAYER_STATUS.equals(SystemConstant.PLAYER_STATUS.PAUSED)){
                            PanelClient.getClient().sendTo("/play/start",null);
                            PLAYER_STATUS = SystemConstant.PLAYER_STATUS.PLAYER;
                            playInfoButton.setImageDrawable(getResources().getDrawable(R.drawable.pause_song));
                        } else{
                            PanelClient.getClient().sendTo("/play/paused",null);
                            PLAYER_STATUS = SystemConstant.PLAYER_STATUS.PAUSED;
                            playInfoButton.setImageDrawable(getResources().getDrawable(R.drawable.play_song));
                        }
                        break;
                    case R.id.player_info_tv:
                        Intent intent = new Intent();
                        intent.setClass(mActivity, PlayerDeviceMusicPlayActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }


            }
        };
        playInfoView.setOnClickListener(von);
        playInfoButton.setOnClickListener(von);
        panelClient = PanelClient.getClient();
        if(panelClient==null){
            Intent intent = new Intent();
            intent.setClass(mActivity, PlayerSearchDeviceActivity.class);
            startActivity(intent);
            return v;
        }

        musicListView = (ListView) v.findViewById(R.id.music_list_view);

        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String music = MUSIC_LIST.get(position);
                if(panelClient!=null) {
                    panelClient.sendTo("/play/play_name", music);
                }
                Intent intent = new Intent();
                intent.setClass(mActivity, PlayerDeviceMusicPlayActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void handleSearch(final String searchTxt) {
        if (searchTxt.length() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 调用百度获取接口
                    Music music = SearchMusic.getSearchMusic(mActivity.getApplicationContext()).getMusic(searchTxt);
                    if (music != null) {
                        if(panelClient!=null) {
                            panelClient.sendTo("/play/add", JsonUtils.object2String(music));
                            Intent intent = new Intent();
                            intent.setClass(mActivity, PlayerDeviceMusicPlayActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        LogUtils.debug(TAG, "No find ");
                        sendToUIMessage("No find " + searchTxt);
                    }
                }
            }).start();
        }
    }

    private List<String> MUSIC_LIST = new ArrayList<String>();
    @Override
    protected void handleMsg(Message message) {
        super.handleMsg(message);

        switch (message.what) {
            case PROCESSING: // 更新进度
//                try {
//                    String strMsg = msg.getData().getString(MESSAGE_KEY);
//                    Map<String, Object> seekMap = JsonUtils.string2Map(strMsg);
//                    long pos = 0l;
//                    int duration = Integer.parseInt(String.valueOf(seekMap.get("duration")));
//                    int position = Integer.parseInt(String.valueOf(seekMap.get("position")));
//                    if (duration > 0) {
//                        // 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
//                        pos = musicProgress.getMax() * position / duration;
//                        musicProgress.setProgress((int) pos);
//                    }
//                } catch (Exception ex) {
//                    ex.toString();
//                }
                break;
            case UPDATE_LIST:
                LogUtils.info(TAG, "UPDATE_LIST");
                MUSIC_LIST = JsonUtils.string2Object(message.getData().getString(MESSAGE_KEY), ArrayList.class);
                if(MUSIC_LIST!=null){
                    MusicListAdapter musicListAdapter = new MusicListAdapter(MUSIC_LIST);
                    musicListView.setAdapter(musicListAdapter);
                }
                break;
            case CONNECT_DEVICE_ERROR:
                Intent intent = new Intent();
                intent.setClass(mActivity, PlayerSearchDeviceActivity.class);
                startActivity(intent);
                break;
            case MESSAGE: // 消息显示
                LogUtils.debug(TAG, message.getData().getString(MESSAGE_KEY));
                msg(message.getData().getString(MESSAGE_KEY));
                break;
            case UPDATE_PLAY_INFO:
                PlayerInfo playerInfo = JsonUtils.string2Object(message.getData().getString(MESSAGE_KEY), PlayerInfo.class);
                if(playerInfo!=null && playerInfo.getMusic()!=null){
                    playInfoView.setText(playerInfo.getMusic().getArtistName()+": "+playerInfo.getMusic().getSongName());

                }
                PLAYER_STATUS = playerInfo.getStatus();
                if(playerInfo.getStatus().equals(SystemConstant.PLAYER_STATUS.PLAYER)){
                    // 把图片改为暂停的
                    playInfoButton.setImageDrawable(getResources().getDrawable(R.drawable.pause_song));
                } else{
                    // 把图片改为播放的
                    playInfoButton.setImageDrawable(getResources().getDrawable(R.drawable.play_song));
                }
                break;
            default:
                break;
        }
    }

    private void sendToUIMessage(String msg){
        Message toUI = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(PlayerBaseFragment.MESSAGE_KEY, msg);
        toUI.setData(bundle);
        toUI.what = PlayerBaseFragment.MESSAGE;
        PlayerBaseFragment.sendMessages(toUI);
    }

    public class MusicListAdapter extends ArrayAdapter<String> {


        public MusicListAdapter(List<String> musicList) {
            super(getActivity(), android.R.layout.list_content,musicList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_items, null);
            }
            TextView musicInfo = (TextView) convertView.findViewById(R.id.music_info);
            String music = getItem(position);
            musicInfo.setText(music);
            return convertView;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isSync = false;
    }

    private boolean isSync=false;
    private void syncInfo(){
        if(isSync){
            return;
        }
        isSync = true;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (isSync){
                    if(panelClient!=null) {
                        panelClient.sendTo("/play/sync", "");
                    }
                    try {
                        Thread.sleep(5000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        new Thread(runnable).start();
    }

}
