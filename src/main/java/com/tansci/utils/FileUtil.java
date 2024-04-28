package com.tansci.utils;

import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.io.SequenceInputStream;

/**
 * @Author chenyong
 * @Date 2022/12/15 14:51
 * @Version 1.0
 */
public class FileUtil {

  private FileUtil() {
  }

  /**
   * 根据开始时间和结束时间截取MP3文件
   *
   * @param sourceFilename 源文件
   * @param targetFileName 目标文件
   * @param beginTime      截取开始时间
   * @param endTime        截取结束时间
   */
  public static void cutMp3(String sourceFilename, String targetFileName, long beginTime, long endTime) {
    MP3File mp3;
    RandomAccessFile dRaf = null;
    RandomAccessFile sRaf = null;
    try {
      File mSourceMp3File = new File(sourceFilename);
      mp3 = new MP3File(mSourceMp3File);

      MP3AudioHeader header = (MP3AudioHeader) mp3.getAudioHeader();
      long bitRateKbps = header.getBitRateAsNumber();
      int length = header.getTrackLength() * 1000;
      System.out.println("总时长：" + length);

      System.out.println("截取结算时间点-->" + endTime);
      // 1KByte/s=8Kbps, bitRate *1024L / 8L / 1000L 转换为 bps 每毫秒
      // 计算出开始字节位置
      long beginBitRateBpm = (bitRateKbps * 1024L / 8L / 1000L) * beginTime;
      // 返回音乐数据的第一个字节
      long firstFrameByte = header.getMp3StartByte();
      // 获取开始时间所在文件的字节位置
      long beginByte = firstFrameByte + beginBitRateBpm;
      // 计算出结束字节位置
      long endByte = beginByte + (bitRateKbps * 1024L / 8L / 1000L) * (endTime - beginTime);

      File dFile = new File(targetFileName);
      dRaf = new RandomAccessFile(dFile, "rw");
      sRaf = new RandomAccessFile(mSourceMp3File, "rw");
      //先将mp3的头文件写入文件
      for (long i = 0; i < firstFrameByte; i++) {
        int m = sRaf.read();
        dRaf.write(m);
      }
      //跳转到指定的位置
      sRaf.seek(beginByte);
      //开始写入 mp3实体
      for (long i = 0; i <= endByte - beginByte; i++) {
        int m = sRaf.read();
        dRaf.write(m);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      ResourcesUtil.multiClose(sRaf, dRaf);
    }

  }

  /**
   * 获取文件总时长
   *
   * @param sourceFilename 文件地址
   * @return
   */
  public static int getTimeLen(String sourceFilename) {
    MP3File mp3 = null;
    try {
      File mSourceMp3File = new File(sourceFilename);
      mp3 = new MP3File(mSourceMp3File);
    } catch (Exception e) {
      e.printStackTrace();
    }

    MP3AudioHeader header = (MP3AudioHeader) mp3.getAudioHeader();
    long bitRateKbps = header.getBitRateAsNumber();
    int length = header.getTrackLength() * 1000;
    System.out.println("总时长：" + length);
    return length;
  }

  /**
   * 合并文件
   * @param wavFile1 文件1.mp3
   * @param wavFile2 文件2.mp3
   * @param mergeFilePath merge1.mp3
   */
  public static void mergeFile(String wavFile1, String wavFile2, String mergeFilePath) {
    //    String wavFile1 = "E:\\tansci\\tempAudio\\audio_recording_7940E8079E1F448EB810650E697036E6_20221215171144.mp3";
    //    String wavFile2 = "E:\\tansci\\tempAudio\\audio_recording_cut_955462C02D454D8B8679ED215C3BBC32_20221215173532.mp3";
    //    String mergeFilePath = "E:\\tansci\\tempAudio//merge1.mp3";
    FileInputStream fistream1 = null;  // first source file
    FileInputStream fistream2 = null;
    SequenceInputStream sistream = null;
    FileOutputStream fostream = null;

    try {
      fistream1 = new FileInputStream(wavFile1);
      fistream2 = new FileInputStream(wavFile2);//second source file
      sistream = new SequenceInputStream(fistream1, fistream2);
      fostream = new FileOutputStream(mergeFilePath);//destinationfile

      int temp;

      while ((temp = sistream.read()) != -1) {
        fostream.write(temp);   // to write to file
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      ResourcesUtil.multiClose(fostream, sistream, fistream1, fistream2);
    }
  }
}
