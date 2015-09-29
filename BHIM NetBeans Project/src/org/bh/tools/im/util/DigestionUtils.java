package org.bh.tools.im.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * DigestionUtils, made for BHIM, is copyright Blue Husky Programming ©2015 GPLv3 <hr/>
 *
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * - 2015-07-05 (1.0.0) - Kyli created DigestionUtils
 * @since 2015-07-05
 */
public class DigestionUtils {
    public static final Service MD5_SERVICE;

    static {
        Service md5Service = null;
        Provider[] providers = Security.getProviders();
provs:  for (Provider provider : providers) {
            Set<Service> services = provider.getServices();
            for (Service service : services) {
                if (service.getAlgorithm().equals("MD5")) {
                    md5Service = service;
                    break provs;
                }
            }
        }
        MD5_SERVICE = md5Service;
    }

    /**
     * Returns the 4-byte digest of the given array. If this system does not have a MD5 algorithm installed, 4 empty
     * bytes are returned.
     *
     * @param raw the thing to digest
     *
     * @return the digestion of the thing
     */
    public static byte[] md5(byte[] raw) {
        if (MD5_SERVICE != null) {
            try {
                return MessageDigest.getInstance(MD5_SERVICE.getAlgorithm()).digest(raw);
            }
            catch (NoSuchAlgorithmException ex) {
                Logger.getGlobal().log(Level.WARNING, "MD5 somehow not found", ex);
            }
        }
        return new byte[4];
    }

    public static boolean validateWithMD5Checksum(byte[] possiblyValid, byte[] checksum) {
        if (MD5_SERVICE != null) {
            try {
                return Arrays.equals(checksum,
                                     MessageDigest.getInstance(MD5_SERVICE.getAlgorithm()).digest(possiblyValid));
            }
            catch (NoSuchAlgorithmException ex) {
                Logger.getGlobal().log(Level.WARNING, "MD5 somehow not found", ex);
            }
        }
        return false;
    }

    private DigestionUtils() {
    }
}
