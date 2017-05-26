package util.mylibrary;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioRecordManager
{
    public final static String SUCCESS
            = "success";
    public final static String E_NOSDCARD
            = "没有SD卡，无法存储录音数据";
    public final static String E_STATE_RECODING
            = "正在录音中，请先停止录音";
    //音频输入-麦克风
    public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    //采用频率
    //44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    public final static int AUDIO_SAMPLE_RATE = 44100;//44.1KHz,普遍使用的频率
    //缓冲区字节大小
    private int bufferSizeInBytes = 0;
    //AudioName裸音频数据文件麦克风
    private String AudioName = "";
    //NewAudioName可播放的音频文件
    private String NewAudioName = "";
    private AudioRecord audioRecord;
    private boolean isRecord = false;//设置正在录制的状态
    private static volatile AudioRecordManager mInstance;
    private Context context;
    private AudioRecordManager(Context context) {
        this.context = context.getApplicationContext();
    }
    public static AudioRecordManager getInstance(Context context)
    {
        if(mInstance == null) {
            synchronized (AudioRecordManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioRecordManager(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }
    public String startRecordAndFile()
    {
        //判断是否有外部存储设备sdcard
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            if(isRecord)
            {
                return E_STATE_RECODING;
            }
            else
            {
                if(audioRecord == null)
                    creatAudioRecord();
                audioRecord.startRecording();
                //让录制状态为true
                isRecord = true;
                //开启音频文件写入线程
                new Thread(new AudioRecordThread()).start();
                return SUCCESS;
            }
        }
        else
        {
            return E_NOSDCARD;
        }

    }

    public void deleteAudioFile() {
        File file = new File(NewAudioName);
        if (file.exists()) {
            file.delete();
        }
    }
    public String getNewAudioName() {
        return NewAudioName;
    }

    public String stopRecordAndFile()
    {
        if (audioRecord != null)
        {
            isRecord = false;//停止文件写入
            audioRecord.stop();
            audioRecord.release();//释放资源
            audioRecord = null;
//            save(NewAudioName);
            return NewAudioName;
        }

        return NewAudioName;
    }
    private void save(String path) {
        MediaScannerConnection.scanFile(context,
                new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }
    private void creatAudioRecord() {
        String mDir = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mDir = Environment.getExternalStorageDirectory() + "/DCIM/Camera";
        } else {
            mDir = context.getFilesDir().getPath() + "/DCIM/Camera";
        }
        File dir = new File(mDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
        String filename = format.format(date) + ".raw";
        String filename2 = format.format(date) + ".wav";

        File file = new File(dir, filename);
        File file2 = new File(dir, filename2);
        //获取音频文件路径
        AudioName = file.getAbsolutePath();
        NewAudioName = file2.getAbsolutePath();

        //获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(AudioRecordManager.AUDIO_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        //创建AudioRecord对象
        audioRecord = new AudioRecord(AudioRecordManager.AUDIO_INPUT,
                AudioRecordManager.AUDIO_SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes);
    }
    class AudioRecordThread implements Runnable
    {
        @Override
        public void run() {
            writeDateTOFile();//往文件中写入裸数据
            copyWaveFile(AudioName, NewAudioName);//给裸数据加上头文件
        }
    }

    /**
     这里将数据写入文件，但是并不能播放，因为AudioRecord获得的音频是原始的裸音频，
     如果需要播放就必须加入一些格式或者编码的头信息。但是这样的好处就是你可以对音频的 裸数据进行处理，比如你要做一个爱说话的TOM
     猫在这里就进行音频的处理，然后重新封装 所以说这样得到的音频比较容易做一些音频的处理。
     */
    private void writeDateTOFile()
    {
        //new一个byte数组用来存一些字节数据，大小为缓冲区大小
        byte[]
                audiodata = new byte[bufferSizeInBytes];
        FileOutputStream
                fos = null;
        int readsize
                = 0;
        try {
            File
                    file = new File(AudioName);
            if (file.exists())
            {
                file.delete();
            }
            fos
                    = new FileOutputStream(file);//建立一个可存取字节的文件
        }
        catch (Exception
                e) {
            e.printStackTrace();
        }
        while (isRecord
                == true)
        {
            readsize
                    = audioRecord.read(audiodata, 0,
                    bufferSizeInBytes);
            if (AudioRecord.ERROR_INVALID_OPERATION
                    != readsize && fos!=null)
            {
                try {
                    fos.write(audiodata);
                }
                catch (IOException
                        e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            if(fos
                    != null)
                fos.close();//关闭写入流
        }
        catch (IOException
                e) {
            e.printStackTrace();
        }
    }

    //这里得到可播放的音频文件
    private void copyWaveFile(String
                                      inFilename, String outFilename) {
        FileInputStream
                in = null;
        FileOutputStream
                out = null;
        long totalAudioLen
                = 0;
        long totalDataLen
                = totalAudioLen + 36;
        long longSampleRate
                = AudioRecordManager.AUDIO_SAMPLE_RATE;
        int channels
                = 2;
        long byteRate
                = 16 *
                AudioRecordManager.AUDIO_SAMPLE_RATE * channels / 8;
        byte[]
                data = new byte[bufferSizeInBytes];
        try {
            in
                    = new FileInputStream(inFilename);
            out
                    = new FileOutputStream(outFilename);
            totalAudioLen
                    = in.getChannel().size();
            totalDataLen
                    = totalAudioLen + 36;
            WriteWaveFileHeader(out,
                    totalAudioLen, totalDataLen,
                    longSampleRate,
                    channels, byteRate);
            while (in.read(data)
                    != -1)
            {
                out.write(data);
            }
            in.close();
            out.close();
        }
        catch (FileNotFoundException
                e) {
            e.printStackTrace();
        }
        catch (IOException
                e) {
            e.printStackTrace();
        }
    }

    /**
     *
     这里提供一个头信息。插入这些信息就可以得到可以播放的文件。
     *
     为我为啥插入这44个字节，这个还真没深入研究，不过你随便打开一个wav
     *
     音频的文件，可以发现前面的头文件可以说基本一样哦。每种格式的文件都有
     *
     自己特有的头文件。
     */
    private void WriteWaveFileHeader(FileOutputStream
                                             out, long totalAudioLen,
                                     long totalDataLen,
                                     long longSampleRate,
                                     int channels,
                                     long byteRate)
            throws IOException
    {
        byte[]
                header = new byte[44];
        header[0]
                = 'R';
//RIFF/WAVE header
        header[1]
                = 'I';
        header[2]
                = 'F';
        header[3]
                = 'F';
        header[4]
                = (byte)
                (totalDataLen & 0xff);
        header[5]
                = (byte)
                ((totalDataLen >> 8)
                        & 0xff);
        header[6]
                = (byte)
                ((totalDataLen >> 16)
                        & 0xff);
        header[7]
                = (byte)
                ((totalDataLen >> 24)
                        & 0xff);
        header[8]
                = 'W';
        header[9]
                = 'A';
        header[10]
                = 'V';
        header[11]
                = 'E';
        header[12]
                = 'f';
//'fmt ' chunk
        header[13]
                = 'm';
        header[14]
                = 't';
        header[15]
                = ' ';
        header[16]
                = 16;
//4 bytes: size of 'fmt ' chunk
        header[17]
                = 0;
        header[18]
                = 0;
        header[19]
                = 0;
        header[20]
                = 1;
//format = 1
        header[21]
                = 0;
        header[22]
                = (byte)
                channels;
        header[23]
                = 0;
        header[24]
                = (byte)
                (longSampleRate & 0xff);
        header[25]
                = (byte)
                ((longSampleRate >> 8)
                        & 0xff);
        header[26]
                = (byte)
                ((longSampleRate >> 16)
                        & 0xff);
        header[27]
                = (byte)
                ((longSampleRate >> 24)
                        & 0xff);
        header[28]
                = (byte)
                (byteRate & 0xff);
        header[29]
                = (byte)
                ((byteRate >> 8)
                        & 0xff);
        header[30]
                = (byte)
                ((byteRate >> 16)
                        & 0xff);
        header[31]
                = (byte)
                ((byteRate >> 24)
                        & 0xff);
        header[32]
                = (byte)
                (2 *
                        16 /
                        8);
//block align
        header[33]
                = 0;
        header[34]
                = 16;
//bits per sample
        header[35]
                = 0;
        header[36]
                = 'd';
        header[37]
                = 'a';
        header[38]
                = 't';
        header[39]
                = 'a';
        header[40]
                = (byte)
                (totalAudioLen & 0xff);
        header[41]
                = (byte)
                ((totalAudioLen >> 8)
                        & 0xff);
        header[42]
                = (byte)
                ((totalAudioLen >> 16)
                        & 0xff);
        header[43]
                = (byte)
                ((totalAudioLen >> 24)
                        & 0xff);
        out.write(header,
                0,
                44);
    }
}
