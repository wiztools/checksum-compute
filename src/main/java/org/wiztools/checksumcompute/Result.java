package org.wiztools.checksumcompute;

/**
 *
 * @author subhash
 */
public interface Result {
    String getMD5Hex();
    String getSHA1Hex();
    String getSHA256Hex();
}
