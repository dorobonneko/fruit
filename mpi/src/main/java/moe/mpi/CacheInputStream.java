package moe.mpi;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CacheInputStream extends InputStream {
    private InputStream in;
    private BufferedOutputStream out;
    private File file,tmp;
    private boolean success;
    private long length;
    public CacheInputStream(InputStream in, File out,long length) throws FileNotFoundException {
        this.in = in;
        this.length=length;
        File parent=out.getParentFile();
        if (parent!=null&&!parent.exists())
            parent.mkdirs();
        this.file = out;
        tmp = new File(out.getAbsolutePath() + ".tmp");
        this.out = new BufferedOutputStream(new FileOutputStream(tmp, false), 8092);
    }
    @Override
    public int read() throws IOException {
        int i = in.read();
        if (i == -1||file.length()==length) {
            success();
        } else {
            out.write(i);
        }
        return i;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int l= in.read(b, off, len);
        if (l == -1||file.length()==length) {
            success();
        } else {
            out.write(b, off, l);
        }
        return l;
    }

    @Override
    public void close() throws IOException {
        out.flush();
        out.close();
        in.close();
        if (!success) {
            tmp.delete();
        }
    }

    private void success() throws IOException {
        success = true;
        out.flush();
        out.close();
        tmp.renameTo(file);
    }
}

