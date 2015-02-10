package com.silveroak.wifiplayer.service.parser;

import com.silveroak.wifiplayer.domain.muisc.Music;
import com.silveroak.wifiplayer.utils.ByteUtils;

import java.io.IOException;

/**
 * <b>MP3的ID3V2信息解析类</b>
 *
 * @QQ QQ:951868171
 * @version 1.0
 * @email xi_yf_001@126.com
 * */
public class Mp3ReadId3v2 {

    private byte[] mp3ips;
    public String charset = "UTF-8"; // 预设编码为GBK
    private Music info=null;

    public Mp3ReadId3v2(byte[] data) {
        this.mp3ips = data;
    }

    public Music readId3v2() throws Exception {
        try {
           return readId3v2(1024*100);		//读取前100KB
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     *
     * */
    public Music readId3v2(int buffSize) throws Exception {
        try {
            if(buffSize > mp3ips.length){
                buffSize = mp3ips.length;
            }
            byte[] buff = ByteUtils.subByte(mp3ips,0,buffSize);

            if (ByteUtils.indexOf("ID3".getBytes(), buff, 1, 512) == -1)
                throw new Exception("未发现ID3V2");
            //获取头像
            info = new Music();

            if (ByteUtils.indexOf("TIT2".getBytes(), buff, 1, 512) != -1) {
                info.setSongName(new String(readInfo(buff, "TIT2"),charset));
                System.out.println("info:" + info.getSongName());
            }
            if (ByteUtils.indexOf("TPE1".getBytes(), buff, 1, 512) != -1) {
                info.setArtistName(new String(readInfo(buff, "TPE1"), charset));
                System.out.println("info:" + info.getArtistName());

            }
            if (ByteUtils.indexOf("TALB".getBytes(), buff, 1, 512) != -1) {
                info.setAlbumName(new String(readInfo(buff, "TALB"), charset));
                System.out.println("info:" + info.getAlbumName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     *读取文本标签
     **/
    private byte[] readInfo(byte[] buff, String tag) {
        int len = 0;
        int offset = ByteUtils.indexOf(tag.getBytes(), buff);
        len = buff[offset + 4] & 0xFF;
        len = (len << 8) + (buff[offset + 5] & 0xFF);
        len = (len << 8) + (buff[offset + 6] & 0xFF);
        len = (len << 8) + (buff[offset + 7] & 0xFF);
        len = len - 1;
        return ByteUtils.cutBytes(ByteUtils.indexOf(tag.getBytes(), buff) + 11,
                ByteUtils.indexOf(tag.getBytes(), buff) + 11 + len, buff);

    }


    public Music getInfo() {
        return info;
    }
}
