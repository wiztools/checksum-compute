package org.wiztools.checksumcompute;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.wiztools.commons.DigestAlgorithm;
import org.wiztools.commons.HexEncodeUtil;

/**
 *
 * @author subhash
 */
class ChecksumCompute implements Result {
    private MessageDigest md5;
    private MessageDigest sha1;
    private MessageDigest sha256;

    ChecksumCompute() {
        try{
            md5 = MessageDigest.getInstance(DigestAlgorithm.MD5);
            sha1 = MessageDigest.getInstance(DigestAlgorithm.SHA_1);
            sha256 = MessageDigest.getInstance(DigestAlgorithm.SHA_256);

            md5.reset();
            sha1.reset();
            sha256.reset();
        }
        catch(NoSuchAlgorithmException ex) {
            assert true: "Should never come here!";
            Logger.getLogger(ChecksumCompute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void update(byte[] buf, int last) {
        md5.update(buf, 0, last);
        sha1.update(buf, 0, last);
        sha256.update(buf, 0, last);
    }

    @Override
    public String getMD5Hex() {
        return HexEncodeUtil.bytesToHex(md5.digest());
    }

    @Override
    public String getSHA1Hex() {
        return HexEncodeUtil.bytesToHex(sha1.digest());
    }

    @Override
    public String getSHA256Hex() {
        return HexEncodeUtil.bytesToHex(sha256.digest());
    }
}
