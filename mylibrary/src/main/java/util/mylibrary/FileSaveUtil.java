package util.mylibrary;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * 文件式具类
 * <p/>
 * Created by yanChunLan
 * @date : 16/10/10.
 */
public class FileSaveUtil {
    private static final String name = "拉拉勾";
    /**
     *保存图片到本地
     * @param context
     * @param b
     * @param path 文件名
     * @return
     */
    public static String saveImage(Context context, byte[] b, String path){
        if(isAvailable()){
            if(isFreeSpace() > b.length) {
                //先保存文件
                File dir = new File(getFilePath(context),name);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                File file = new File(dir, path);
                try {
                    OutputStream out = new FileOutputStream(file);
                    out.write(b, 0, b.length);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 其次把文件插入到系统图库
//                try {
//                    ContentValues values = new ContentValues();
//                    values.put(MediaStore.Images.Media.TITLE, path);
//                    values.put(MediaStore.Images.Media.DESCRIPTION, "");
//                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//                    context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        //调用系统插入方法
//                    MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                            file.getAbsolutePath(), path, null);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                // 最后通知图库更新
//                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
                //扫描媒体库
                if(Double.parseDouble(android.os.Build.VERSION.RELEASE.substring(0, 3)) >= 4.4){
                    MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);
                }else {
                    MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null,null);
                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + file.getAbsolutePath())));
                }
                return file.getAbsolutePath();
            }else{
                try {
                    throw new Exception("内存不足");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "保存失败";
    }
    private static String getFilePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            // MyApplication.getInstance().getFilesDir()返回的路劲为/data/data/PACKAGE_NAME/files，其中的包就是我们建立的主Activity所在的包
            return  context.getFilesDir().getAbsolutePath();
        }
    }
    /**
     * 外置卡空间大小
     * @return
     */
    private static long  isFreeSpace(){
        StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        long freeSize = sf.getBlockSize()*(long)sf.getFreeBlocks();
        return freeSize;
    }

    /**
     * 外置卡状态是否打开
     * @return
     */
    private static boolean isAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
