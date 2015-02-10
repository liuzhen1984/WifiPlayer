package com.silveroak.wifiplayer.service.business;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.SeekBar;
import com.silveroak.wifiplayer.constants.SystemConstant;
import com.silveroak.wifiplayer.database.MusicHelper;
import com.silveroak.wifiplayer.domain.ErrorCode;
import com.silveroak.wifiplayer.domain.PlayerInfo;
import com.silveroak.wifiplayer.domain.Result;
import com.silveroak.wifiplayer.domain.muisc.DownloadMusicFile;
import com.silveroak.wifiplayer.domain.muisc.Music;
import com.silveroak.wifiplayer.preference.StorageUtils;
import com.silveroak.wifiplayer.preference.data.CurrentPlayer;
import com.silveroak.wifiplayer.service.IHandlerWhatAndKey;
import com.silveroak.wifiplayer.service.tcpserver.ServerCache;
import com.silveroak.wifiplayer.utils.JsonUtils;
import com.silveroak.wifiplayer.utils.LogUtils;
import com.silveroak.wifiplayer.utils.NetworkUtils;
import io.netty.channel.Channel;

import java.util.*;

/**
 * Created by zliu on 14/12/27.
 * *  play:
 * all, 获取当前CurrentPlayer 信息
 * info,获取当前的信息，payload，对象：PlayerInfo
 * start,
 * paused,
 * stop,
 * next,
 * previous,
 * delete,  url= all清空， 删除新的
 * play  当前播放的操作
 * volume
 */
public class MusicPlayerServer implements IProcessService, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private final String TAG = MusicPlayerServer.class.getSimpleName();
    private final String uri = "play";

    private MusicHelper musicHelper;
    private StorageUtils storageUtils;
    private AudioManager audioManager;

    private Context context;


    private static MusicPlayerServer playService = null;

    public synchronized static MusicPlayerServer getService(SeekBar seekBar, Context context) {
        if (playService == null) {
            playService = new MusicPlayerServer(seekBar);
            playService.musicHelper = new MusicHelper(context);
            playService.storageUtils = StorageUtils.getInstance(context);
            playService.audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
            playService.context = context;
        }
        return playService;
    }

    private MediaPlayer mediaPlayer; // 媒体播放器
    private SeekBar seekBar; // 拖动条
    private Timer mTimer = new Timer(); // 计时器

    // 初始化播放器
    private MusicPlayerServer(SeekBar seekBar) {
        super();
        this.seekBar = seekBar;
//        initMediaPlayer();
        // 每一秒触发一次
        mTimer.schedule(timerTask, 0, 1000);
    }

    private void initMediaPlayer() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
        } catch (Exception e) {
            LogUtils.error(TAG, e);
        }
    }

    // 计时器
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (mediaPlayer == null || !mediaPlayer.isPlaying())
                return;
            if (mediaPlayer.isPlaying() && seekBar.isPressed() == false) {
                handler.sendEmptyMessage(0); // 发送消息
            }
        }
    };
    private Result<Map> resultSeek = new Result<Map>();
    private Map<String, Integer> seekMap = new HashMap<String, Integer>();


    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int position = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            long pos = 0l;
            if (duration > 0) {
                // 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
                pos = seekBar.getMax() * position / duration;
                seekBar.setProgress((int) pos);
            }
            seekMap.put("position", position);
            seekMap.put("duration", duration);
            resultSeek.setWhat(IHandlerWhatAndKey.PROCESSING);
            resultSeek.setResult(ErrorCode.SUCCESS);
            resultSeek.setPayload(seekMap);
            //根据这个定时器，给所有客户端发送当前进度
            for (Channel channel : ServerCache.getChannels()) {
                LogUtils.debug(TAG, "channel " + String.valueOf(channel));
                if (channel != null && channel.isOpen()) {
                    channel.writeAndFlush(JsonUtils.object2String(resultSeek));
                }
            }
        }

        ;
    };

    /**
     * 缓冲更新
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
        if (mediaPlayer.isPlaying()) {
            seekBar.setSecondaryProgress(percent);
            int currentProgress = seekBar.getMax()
                    * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();

            if (currentProgress >= 100) {
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtils.debug(TAG, "After 2 sec player next");
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        autoNext();
                    }
                }.run();
            }
            LogUtils.debug(TAG, currentProgress + "% play " + percent + " buffer");
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        LogUtils.debug(TAG, "onCompletion");
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        LogUtils.debug(TAG, "onPrepared");
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public Result path(String type, String params) {
        LogUtils.debug(TAG, "type:" + type + " params:" + params);
        Result result = new Result();
        result.setWhat(IHandlerWhatAndKey.UPDATE_INFO);
        try {
            if (type == null || "".equals(type.trim())) {
                return all();
            } else if ("info".equals(type)) {
                return all();
            } else if ("start".equals(type)) {
                return start();
            } else if ("paused".equals(type)) {
                return paused();
            } else if ("stop".equals(type)) {
                return stop();
            } else if ("next".equals(type)) {
                return next();
            } else if ("previous".equals(type)) {
                return previous();
            } else if ("play".equals(type)) {
                return playUrl(params);
            } else if ("delete".equals(type)) {
                return delete(params);
            } else if ("volume".equals(type)) {
                return volume(params);
            } else {
                result.setResult(ErrorCode.USER_ERROR.URL_INVALID);
            }
        } catch (Exception ex) {
            LogUtils.error(TAG, ex);
            result.setResult(ErrorCode.USER_ERROR.PARAMS_FORMAT);
        }
        return result;
    }

    public Result volume(String params) {
        LogUtils.debug(TAG, "volume");
        Result result = new Result();
        result.setWhat(IHandlerWhatAndKey.UPDATE_INFO);
        if (params == null) {
            result.setResult(ErrorCode.USER_ERROR.PARAMS_FORMAT);
            return result;

        }
        try {
            int value = Integer.valueOf(params);
            int current = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current+value, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
        } catch (Exception ex) {
            result.setResult(ErrorCode.USER_ERROR.PARAMS_FORMAT);
            return result;
        }
        result.setResult(ErrorCode.SUCCESS);
        return result;

    }

    public Result all() {
        LogUtils.debug(TAG, "get all");
        Result result = new Result();
        result.setWhat(IHandlerWhatAndKey.UPDATE_INFO);
        PlayerInfo playerInfo = new PlayerInfo();
        CurrentPlayer currentPlayer = getCurrentPlayer();
        playerInfo.setStatus(currentPlayer.getStatus());
        playerInfo.setType(currentPlayer.getType());
        playerInfo.setMusic(musicHelper.findByUrl(currentPlayer.getPlayerMusic()));
        playerInfo.setVolume(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        result.setPayload(playerInfo);
        LogUtils.debug(TAG, "CurrentPlayer:" + getCurrentPlayer());
        result.setResult(ErrorCode.SUCCESS);
        return result;
    }

    public Result playList(){
        return null;
    }

    public Result start() {
        LogUtils.debug(TAG, " start");
        Result result = new Result();
        result.setWhat(IHandlerWhatAndKey.UPDATE_INFO);
        boolean isPlayer = false;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.start();
                isPlayer = true;
            } catch (Exception ex) {
                LogUtils.warn(TAG, "media player error");
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
        if (!isPlayer) {
            initMediaPlayer();
            CurrentPlayer currentPlayer = getCurrentPlayer();
            if (currentPlayer != null) {
                String music = currentPlayer.getPlayerMusic();
                if (music == null) {
                    if (currentPlayer.getPlayerList().size() > 0) {
                        music = currentPlayer.getPlayerList().get(0);
                    }
                }
                if (music != null) {
                    playUrl(music);
                    currentPlayer.setStatus(SystemConstant.PLAYER_STATUS.PLAYER);
                    saveCurrentPlayer(currentPlayer);
                    result.setResult(ErrorCode.SUCCESS);
                }
            }
        }
        return result;
    }

    public Result stop() {
        LogUtils.debug(TAG, " stop:");
        Result result = new Result();
        result.setWhat(IHandlerWhatAndKey.UPDATE_INFO);

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            CurrentPlayer currentPlayer = getCurrentPlayer();
            currentPlayer.setStatus(SystemConstant.PLAYER_STATUS.STOP);
            saveCurrentPlayer(currentPlayer);
        }
        result.setResult(ErrorCode.SUCCESS);
        return result;
    }

    public Result paused() {
        LogUtils.debug(TAG, " paused:");
        Result result = new Result();
        result.setWhat(IHandlerWhatAndKey.UPDATE_INFO);

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        CurrentPlayer currentPlayer = getCurrentPlayer();
        currentPlayer.setStatus(SystemConstant.PLAYER_STATUS.PAUSED);
        saveCurrentPlayer(currentPlayer);
        result.setResult(ErrorCode.SUCCESS);

        return result;
    }

    public Result next() {
        LogUtils.debug(TAG, " next:");
        CurrentPlayer currentPlayer = getCurrentPlayer();
        List<String> musicList = currentPlayer.getPlayerList();
        Result result = new Result();
        result.setWhat(IHandlerWhatAndKey.UPDATE_INFO);

        result.setResult(ErrorCode.SUCCESS);
        if (musicList.size() < 1) {
            return result;
        }
        if (currentPlayer.getPlayerMusic() == null || "".equalsIgnoreCase(currentPlayer.getPlayerMusic())) {
            playUrl(musicList.get(0));
            return result;
        }


        for (int i = 0; i < musicList.size(); i++) {
            if (musicList.get(i).equalsIgnoreCase(currentPlayer.getPlayerMusic())) {
                if (i + 1 >= musicList.size()) {
                    if (currentPlayer.getType().equals(SystemConstant.PLAYER_TYPE.ALL)) {
                        playUrl(musicList.get(0));
                    } else {
                        return result;
                    }
                } else {
                    playUrl(musicList.get(i + 1));
                }
                return result;
            }
        }
        return result;
    }

    public Result previous() {
        LogUtils.debug(TAG, " previous:");

        Result result = new Result();
        result.setResult(ErrorCode.SUCCESS);
        result.setWhat(IHandlerWhatAndKey.UPDATE_INFO);

        CurrentPlayer currentPlayer = getCurrentPlayer();
        List<String> musicList = currentPlayer.getPlayerList();
        if (musicList.size() < 1) {
            return result;
        }
        if (currentPlayer.getPlayerMusic() == null || "".equalsIgnoreCase(currentPlayer.getPlayerMusic())) {
            playUrl(musicList.get(0));
            return result;
        }

        for (int i = 0; i < musicList.size(); i++) {
            if (musicList.get(i).equalsIgnoreCase(currentPlayer.getPlayerMusic())) {
                if (i - 1 < 0) {
                    return result;
                }
                playUrl(musicList.get(i - 1));
                return result;
            }
        }
        return result;
    }

    public Result delete(String url) {
        LogUtils.debug(TAG, " delete  :"+url);

        Result result = new Result();
        result.setWhat(IHandlerWhatAndKey.UPDATE_INFO);

        result.setResult(ErrorCode.SUCCESS);
        CurrentPlayer currentPlayer = getCurrentPlayer();
        List<String> musicList = currentPlayer.getPlayerList();
        if ("all".equalsIgnoreCase(url)) {
            currentPlayer.setPlayerList(new ArrayList<String>());
            //todo 清楚内容
        } else {
            for (String music : musicList) {
                if (music.equalsIgnoreCase(url)) {
                    musicList.remove(music);
                    currentPlayer.setPlayerList(musicList);
                    break;
                }
            }
        }
        saveCurrentPlayer(currentPlayer);
        return result;
    }

    /**
     * @param url url地址
     */
    public Result playUrl(String url) {
        Result result = new Result();
        result.setResult(ErrorCode.SUCCESS);
        result.setWhat(IHandlerWhatAndKey.UPDATE_INFO);

        try {
            if (mediaPlayer == null) {
                initMediaPlayer();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url); // 设置数据源
            mediaPlayer.prepare(); // prepare自动播放

            CurrentPlayer currentPlayer = getCurrentPlayer();
            currentPlayer.setPlayerMusic(url);
            currentPlayer.setStatus(SystemConstant.PLAYER_STATUS.PLAYER);
            List<String> musics = getCurrentPlayer().getPlayerList();
            boolean isHave = false;
            for (String mu : musics) {
                if (url.equalsIgnoreCase(mu)) {
                    isHave = true;
                }
            }
            if (!isHave) {
                currentPlayer.getPlayerList().add(url);
            }
            //todo url 解析内容
            parserUrl(url);
            saveCurrentPlayer(currentPlayer);
            result.setPayload(currentPlayer);
        } catch (Exception e) {
            LogUtils.error(TAG, e);
            result.setResult(ErrorCode.SYSTEM_ERROR.UNKNOWN_ERROR);
        }
        return result;
    }

    public int getCurrentRate() {
        return mediaPlayer.getDuration();
    }

    public void seekTo(int rate) {
        mediaPlayer.seekTo(rate);
    }


    private void saveCurrentPlayer(CurrentPlayer currentPlayer) {
        storageUtils.pushSettingData(StorageUtils._CURRENT_PLAYER_LIST, currentPlayer);
    }

    private CurrentPlayer getCurrentPlayer() {
        CurrentPlayer currentPlayer = storageUtils.pullSettingData(StorageUtils._CURRENT_PLAYER_LIST, CurrentPlayer.key(), CurrentPlayer.class);
        if (currentPlayer == null) {
            currentPlayer = new CurrentPlayer();
            currentPlayer.setStatus(SystemConstant.PLAYER_STATUS.STOP);
            currentPlayer.setType(SystemConstant.PLAYER_TYPE.ALL);
            currentPlayer.setPlayerList(new ArrayList<String>());
        } else {
            if (currentPlayer.getPlayerList() == null) {
                currentPlayer.setPlayerList(new ArrayList<String>());
            }
            if (currentPlayer.getType() == null) {
                currentPlayer.setType(SystemConstant.PLAYER_TYPE.ALL);
            }
            if (currentPlayer.getStatus() == null) {
                currentPlayer.setStatus(SystemConstant.PLAYER_STATUS.STOP);
            }
        }
        return currentPlayer;
    }

    public void autoNext() {
        LogUtils.debug(TAG, "Player complete");
        CurrentPlayer currentPlayer = getCurrentPlayer();
        if (currentPlayer.getType().equals(SystemConstant.PLAYER_TYPE.SINGLE)) {
            playUrl(currentPlayer.getPlayerMusic());
        } else {
            next();
        }
    }

    private void parserUrl(final String url){
        Music music = musicHelper.findByUrl(url);
        if(music==null || music.getName()==null || "".equals(music.getName())){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    DownloadMusicFile dmf =   NetworkUtils.downloadMusicFile(context,url);
                    LogUtils.debug(TAG,dmf.toString());
                }
            };
            new Thread(runnable).start();

        }
    }

}
