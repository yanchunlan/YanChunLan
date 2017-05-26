package util.mylibrary;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 只能录制amr格式音频，暂时不用
 * Created by acer on 2016/9/9.
 */
public class AudioManager {
    private MediaPlayer mMediaPlayer;
    private MediaRecorder mMediaRecorder;
    private String mCurrentFilePath;
    private static volatile AudioManager mInstance;
    private Boolean IsPrepared = false;
    private Context context;

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }
    public static AudioManager getmInstance() {
        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioManager();
                }
            }
        }
        return mInstance;
    }
    public void pauseAudio() {
        if (mMediaPlayer != null&&mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public String getmCurrentFilePath() {
        return mCurrentFilePath;
    }

    public void prepareAudio() {
       try {
           IsPrepared = false;
           String mDir = Environment.getExternalStorageDirectory() + "/DCIM/Camera";
           File dir = new File(mDir);
           if (!dir.exists()) {
               dir.mkdirs();

           }
//           String fileName = UUID.randomUUID().toString() + ".mp3";
           Date date = new Date();
           SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
           String filename;
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
               filename = format.format(date) + ".aac";
           } else {
               filename = format.format(date) + ".amr";
           }
           File file = new File(dir, filename);
           mMediaRecorder = new MediaRecorder();
           mCurrentFilePath = file.getAbsolutePath();
           mMediaRecorder.setOutputFile(mCurrentFilePath);
           //设置麦克风为音频源
           mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                //设置音频格式
               mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
               //设置音频编码amr
               mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC_ELD);
           } else {
               //设置音频格式
               mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
               //设置音频编码amr
               mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
           }
           mMediaRecorder.prepare();
           mMediaRecorder.start();
           IsPrepared = true;
       } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public int  getVoiceLevel(int maxLevel) {
        if (IsPrepared) {
            try {
                    //getMaxAmplitude 1-32767
                return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
        return 1;
    }

    public void release() {
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
        IsPrepared = false;
        save();
    }

    public void save() {
        MediaScannerConnection.scanFile(context,
                new String[]{mCurrentFilePath}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
//                        LogUtil.Log_I("", "Scanned " + path + ":");
//                        LogUtil.Log_I("", "-> uri=" + uri);
                    }
                });
    }
    public void cancle() {
        release();
        File file = new File(mCurrentFilePath);
        file.delete();
        mCurrentFilePath = null;
    }
}
