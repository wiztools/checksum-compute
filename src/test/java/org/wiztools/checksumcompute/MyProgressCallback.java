package org.wiztools.checksumcompute;

import org.wiztools.checksumcompute.ProgressCallback;
import org.wiztools.checksumcompute.Result;

/**
 *
 * @author subhash
 */
class MyProgressCallback implements ProgressCallback {

    private long totalLength;
    private long progressLength = 0;

    @Override
    public void start(long length) {
        totalLength = length;
        System.out.println("Start: " + totalLength);
    }

    @Override
    public void progress(int partLength) {
        progressLength += partLength;
        System.out.println("Progress length: " + progressLength);
        System.out.println("In percentage: " + ((progressLength*100L)/totalLength) + "%");
    }

    @Override
    public void end(Result result) {
        System.out.println("Result: " + result);;
    }

    @Override
    public void stop() {
        System.out.println("End!");
    }

}
