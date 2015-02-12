package com.silveroak.playerclient.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.silveroak.playerclient.R;
import com.silveroak.playerclient.domain.Music;
import com.silveroak.playerclient.service.business.PanelClient;
import com.silveroak.playerclient.service.business.SearchMusic;
import com.silveroak.playerclient.ui.activity.PlayerDeviceMusicPlayActivity;
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

    public static PlayerMusicListFragment newInstance() {
        return new PlayerMusicListFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_music_list, null);

        musicListView = (ListView) v.findViewById(R.id.music_list_view);

        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Music music = MUSIC_LIST.get(position);
                PanelClient.init(mActivity.getApplicationContext()).sendTo("/play/play",String.valueOf(music.get_id()));
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
                    //todo 调用百度获取接口
                    Music music = SearchMusic.getSearchMusic(mActivity.getApplicationContext()).getMusic(searchTxt);
                    if (music != null) {
                        PanelClient.init(mActivity.getApplicationContext()).sendTo("/play/add",JsonUtils.object2String(music));
                    } else {
                        LogUtils.debug(TAG, "No find ");
                        sendToUIMessage("No find " + searchTxt);
                    }
                }
            }).start();
        }
    }

    private List<Music> MUSIC_LIST = new ArrayList<Music>();
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
            case UPDATE_INFO:
                LogUtils.debug(TAG, "UPDATE_INFO");
                MUSIC_LIST = JsonUtils.string2Object(message.getData().getString(MESSAGE_KEY), ArrayList.class);
                MusicListAdapter musicListAdapter = new MusicListAdapter(mActivity,MUSIC_LIST);

                musicListView.setAdapter(musicListAdapter);

                break;
            case MESSAGE: // 消息显示
                LogUtils.debug(TAG, message.getData().getString(MESSAGE_KEY));
                msg(message.getData().getString(MESSAGE_KEY));
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

    public class MusicListAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        private List<Music> musicList;

        public MusicListAdapter(Context context,List<Music> musicList){
            this.mInflater = LayoutInflater.from(context);
            this.musicList = musicList;
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return musicList.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_items, null);
            }
            TextView musicInfo = (TextView) convertView.findViewById(R.id.music_info);
            Music music = musicList.get(position);
            musicInfo.setText(music.getSongName()+"   "+music.getArtistName());
            return convertView;
        }

    }


}
