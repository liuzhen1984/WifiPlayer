package com.silveroak.playerclient.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.jacp.util.Utils;
import com.silveroak.playerclient.R;
import com.silveroak.playerclient.constants.SystemConstant;
import com.silveroak.playerclient.domain.Music;
import com.silveroak.playerclient.domain.PlayerInfo;
import com.silveroak.playerclient.service.business.PanelClient;
import com.silveroak.playerclient.service.business.SearchMusic;
import com.silveroak.playerclient.ui.activity.PlayerSearchDeviceActivity;
import com.silveroak.playerclient.ui.base.PlayerBaseFragment;
import com.silveroak.playerclient.ui.base.PlayerBaseSearchBarFragment;
import com.silveroak.playerclient.utils.JsonUtils;
import com.silveroak.playerclient.utils.LogUtils;
import io.netty.channel.Channel;

import java.util.Map;

/**
 * Created by John on 2015/2/11.
 */
public class PlayerMusicPlayFragment extends PlayerBaseSearchBarFragment {
    private final static String TAG = PlayerMusicPlayFragment.class.getSimpleName();

    public static PlayerMusicPlayFragment newInstance() {
        return new PlayerMusicPlayFragment();
    }

    private SeekBar processSeekBar;
    private TextView musicInfo;

    private ImageView mMusicCover;

    private ImageButton nextButton;
    private ImageButton previousButton;
    private ImageButton playButton;
    private ImageButton playTypeButton;
    private PanelClient panelClient;

    private SystemConstant.PLAYER_TYPE[] TYPE_LIST={
            SystemConstant.PLAYER_TYPE.SINGLE,
            SystemConstant.PLAYER_TYPE.RANDOM,
            SystemConstant.PLAYER_TYPE.ALL,
    };

    private int PLAY_TYPE_STATUS = 0;

    private SystemConstant.PLAYER_STATUS PLAYER_STATUS= SystemConstant.PLAYER_STATUS.PAUSED;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_music_play, null);
        processSeekBar = (SeekBar) v.findViewById(R.id.sbSongProcess);
        musicInfo = (TextView) v.findViewById(R.id.tvSongName);
        mMusicCover = (ImageView) v.findViewById(R.id.imageCover);
        nextButton = (ImageButton) v.findViewById(R.id.imgBtnNextSong);
        previousButton = (ImageButton) v.findViewById(R.id.imgBtnPreviousSong);
        playButton = (ImageButton) v.findViewById(R.id.imgBtnPlayPause);
        playTypeButton = (ImageButton) v.findViewById(R.id.imgBtnPlayType);

        ButtonClickListener buttonClickListener = new ButtonClickListener();
        nextButton.setOnClickListener(buttonClickListener);
        previousButton.setOnClickListener(buttonClickListener);
        playButton.setOnClickListener(buttonClickListener);
        playTypeButton.setOnClickListener(buttonClickListener);

        musicInfo.setText("No music");
        processSeekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        panelClient = PanelClient.getClient();
        if(panelClient==null){
            Intent intent = new Intent();
            intent.setClass(mActivity, PlayerSearchDeviceActivity.class);
            startActivity(intent);
        }
        syncInfo();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMusicCover.setImageDrawable(getResources().getDrawable(R.drawable.no_music));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isSync=false;
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
                        if(panelClient!=null){
                            panelClient.sendTo("/play/add", JsonUtils.object2String(music));
                        }
                    } else {
                        LogUtils.debug(TAG, "No find ");
                        sendToUIMessage("No find " + searchTxt);
                    }
                }
            }).start();
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
    @Override
    protected void handleMsg(Message message) {
        super.handleMsg(message);

        switch (message.what) {
            case PROCESSING: // 更新进度
                try {
                    String strMsg = message.getData().getString(MESSAGE_KEY);
                    Map<String, Object> seekMap = JsonUtils.string2Map(strMsg);
                    long pos = 0l;
                    int duration = Integer.parseInt(String.valueOf(seekMap.get("duration")));
                    int position = Integer.parseInt(String.valueOf(seekMap.get("position")));
                    if (duration > 0) {
                        // 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
                        pos = processSeekBar.getMax() * position / duration;
                        processSeekBar.setProgress((int) pos);
                    }
                } catch (Exception ex) {
                    ex.toString();
                }
                break;
            case UPDATE_PLAY_INFO:
                LogUtils.debug(TAG, "UPDATE_INFO");
                PlayerInfo playerInfo = JsonUtils.string2Object(message.getData().getString(MESSAGE_KEY), PlayerInfo.class);

                if(playerInfo!=null){

                     Utils.loadImage(playerInfo.getMusic().getSongPicBig(), new Utils.OnLoadImageListener() {
                         @Override
                         public void onLoadImage(Bitmap bm, String imageUrl) {
                             if (bm == null) {
                                 mMusicCover.setImageDrawable(getResources().getDrawable(R.drawable.no_music));
                             } else {
                                 mMusicCover.setImageBitmap(bm);
                             }
                         }
                     });

                    PLAYER_STATUS = playerInfo.getStatus();
                    if(playerInfo.getStatus().equals(SystemConstant.PLAYER_STATUS.PLAYER)){
                        // 把图片改为暂停的
                        playButton.setImageDrawable(getResources().getDrawable(R.drawable.pause_song));
                    } else{
                        // 把图片改为播放的
                        playButton.setImageDrawable(getResources().getDrawable(R.drawable.play_song));
                    }
                    musicInfo.setText(playerInfo.getMusic().getSongName()+"  Artiste:"+playerInfo.getMusic().getArtistName());

                    if(playerInfo.getType()!=null){
                        //todo 获取循环方式修改图片
                        if(playerInfo.getType().equals(SystemConstant.PLAYER_TYPE.RANDOM)){
                            playTypeButton.setImageDrawable(getResources().getDrawable(R.drawable.rang_play));
                            PLAY_TYPE_STATUS = 1;
                        }else if(playerInfo.getType().equals(SystemConstant.PLAYER_TYPE.SINGLE)){
                            playTypeButton.setImageDrawable(getResources().getDrawable(R.drawable.one_play));
                            PLAY_TYPE_STATUS = 0;
                        }else{
                            playTypeButton.setImageDrawable(getResources().getDrawable(R.drawable.all_play));
                            PLAY_TYPE_STATUS = 2;
                        }
                    }
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
            default:
                break;
        }
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

    private final class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final Channel channel = PanelClient.init(mActivity.getApplicationContext()).getChannel();
            switch (v.getId()) {
                case R.id.imgBtnPlayPause:
                    if(PLAYER_STATUS.equals(SystemConstant.PLAYER_STATUS.PAUSED)){
                        PanelClient.getClient().sendTo("/play/start",null);
                        PLAYER_STATUS = SystemConstant.PLAYER_STATUS.PLAYER;
                        playButton.setImageDrawable(getResources().getDrawable(R.drawable.pause_song));
                    } else{
                        PanelClient.getClient().sendTo("/play/paused",null);
                        PLAYER_STATUS = SystemConstant.PLAYER_STATUS.PAUSED;
                        playButton.setImageDrawable(getResources().getDrawable(R.drawable.play_song));
                    }
                    break;
                case R.id.imgBtnNextSong:
                    PanelClient.getClient().sendTo("/play/next",null);
                    break;
                case R.id.imgBtnPreviousSong:
                    PanelClient.getClient().sendTo("/play/previous",null);
                    break;
                case R.id.imgBtnPlayType:
                    //todo 修改循环播放类型
                    PLAY_TYPE_STATUS = PLAY_TYPE_STATUS+1;
                    if(PLAY_TYPE_STATUS>= SystemConstant.PLAYER_TYPE.values().length){
                        PLAY_TYPE_STATUS = 0;
                    }
                    if(PLAY_TYPE_STATUS==0){
                        playTypeButton.setImageDrawable(getResources().getDrawable(R.drawable.one_play));
                    }else if(PLAY_TYPE_STATUS == 1){
                        playTypeButton.setImageDrawable(getResources().getDrawable(R.drawable.rang_play));
                    }  else{
                        playTypeButton.setImageDrawable(getResources().getDrawable(R.drawable.all_play));
                    }
                    PanelClient.getClient().sendTo("/play/type",String.valueOf(PLAY_TYPE_STATUS));
                    break;
                default:
                    break;
            }
        }
    }


    // 进度改变
    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
//            this.progress = progress * player.getCurrentRate()
//                    / seekBar.getMax();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
//            player.seekTo(progress);
        }
    }
}
