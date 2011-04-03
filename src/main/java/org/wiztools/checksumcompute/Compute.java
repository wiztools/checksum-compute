package org.wiztools.checksumcompute;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author subhash
 */
class Compute {
    private static final Logger logger = Logger.getLogger(Compute.class.getName());

    private FileInputStream fis = null;
    private boolean interrupted = false;
    private boolean isFinished = false;

    void compute(File f, ProgressCallback progressCallback) throws IOException {
        final long fileLength =  f.length();
        progressCallback.start(fileLength);

        try{
            fis = new FileInputStream(f);

            ChecksumCompute md = new ChecksumCompute();

            byte[] buf = new byte[1024*8];
            int len = -1;
            while((len=fis.read(buf))!=-1) {
                progressCallback.progress(len);
                md.update(buf, len);
            }
            progressCallback.end(md);
        }
        catch(IOException ex) {
            if(!interrupted)
                throw new IOException(ex);
        }
        finally {
            if(fis != null) {
                try{
                    fis.close();
                }
                catch(IOException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
            
            progressCallback.stop();
            isFinished = true;
        }
    }

    void interrupt() {
        if(isFinished) return;
        
        interrupted = true;
        try {
            fis.close();
        }
        catch(IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    boolean isFinished() {
        return isFinished;
    }
}
