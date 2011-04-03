package org.wiztools.checksumcompute;

/**
 *
 * @author subhash
 */
public interface ProgressCallback {
    void start(long length);
    void progress(int partLength);
    void end(Result result);
    void stop();
}
