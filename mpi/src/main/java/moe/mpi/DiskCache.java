package moe.mpi;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import java.io.FileFilter;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.CompletableFuture;

public class DiskCache {
    private File cachePath;

    private long size;
    private static Map<String,DiskCache> mCache=new HashMap<>();
    private DiskCache(File path, int size) {
        cachePath = path;
        if (!path.exists())
            path.mkdirs();
        this.size = size;
        trimToSize();
    }
    public static DiskCache getInstance(File path, int size) {
        if (mCache.containsKey(path.getAbsolutePath()))
            return mCache.get(path.getAbsolutePath());
        synchronized (File.class) {
            if (mCache.containsKey(path.getAbsolutePath())) {
                return mCache.get(path.getAbsolutePath());
            }
            mCache.put(path.getAbsolutePath(), new DiskCache(path, size));
            return mCache.get(path.getAbsolutePath());
        }
    }
    public void trimToSize() {
		if(size>0)
        CompletableFuture.runAsync(new Runnable(){
                public void run() {
                    synchronized (DiskCache.class) {
                        final AtomicLong totalLength=new AtomicLong();
                        final long time=System.currentTimeMillis();
                        File[] list=cachePath.listFiles(new FileFilter(){

                                @Override
                                public boolean accept(File file) {
                                    if(time-file.lastModified()>2592000000l)
                                    {//超过30天，删除
                                        file.delete();
                                        return false;
                                    }
                                    totalLength.addAndGet(file.length());
                                    return true;
                                }
                            });
                        if (size<=0||totalLength.longValue() < size)return;
                        Arrays.sort(list, new Comparator<File>(){

                                @Override
                                public int compare(File p1, File p2) {
                                    return (int)(p2.lastModified()-p1.lastModified());
                                }
                            });
                        long length=size / 3 * 2;
                        for (File file:list) {
                            if (file == null || !file.exists())continue;
                            totalLength.addAndGet(-file.length());
                            file.delete();
                            if (totalLength.longValue() < length)
                                break;
                        }

                    }
                }
            });
    }
    public void clearCache() {
        File[] files=cachePath.listFiles();
        if (files != null)
            for (File f:files)
                f.delete();
    }
    public InputStream getInputStream(String key, InputStream input,long length) throws FileNotFoundException {
        return new CacheInputStream(input, getDirty(key),length);
    }
    public File getCache(String key) {
        if (key == null)return null;
        File cache=new File(cachePath, key);
        return cache;
    }
    public File getDirty(String key) {
        if (!cachePath.exists())cachePath.mkdirs();
        return new File(cachePath, key+".tmp");
    }
    
}

