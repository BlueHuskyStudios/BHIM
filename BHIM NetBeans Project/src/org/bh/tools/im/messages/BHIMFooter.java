package org.bh.tools.im.messages;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bh.tools.im.err.FailedValidationException;
import org.bh.tools.im.messages.BHIMFooter.BHIMFooterField;
import org.bh.tools.im.util.DigestionUtils;
import org.bh.tools.net.msg.Field;
import org.bh.tools.net.msg.Footer;



/**
 * BHIMFooter, made for BHIM, is copyright Blue Husky Programming ©2015 GPLv3 <hr/>
 *
 * The footer for a BHIM message. This class conforms to the BHIM Implementation Spec Opus 5.
 *
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * - 2015-09-29 (1.0.0) - Kyli created BHIMFooter
 * @since 2015-09-29
 */
public class BHIMFooter extends Footer<BHIMFooterField> {
	//<editor-fold defaultstate="collapsed" desc="lengths">
	/** The length of the MD5 Hash, in Bytes. (<code>{@value}B</code>) */
	// ALERT: When updating this, also update this class' unrolled loops that assume 0xF
	public static final byte MD5_LEN = 0xF;
	/** The length of the End-Of-Transmission, in Bytes. (<code>{@value}B</code>) */
	public static final byte EOT_LEN = 1;
	/** The size of the entire footer, in Bytes. (<code>{@value}B</code>) */
	public static final byte SIZE = (byte) (MD5_LEN + EOT_LEN);
	//</editor-fold>

	/** The {@code 1B} signal of the End-Of-Transmission ({@code U+0004}) */
	public static final byte END_OF_TRANSMISSION = 0x0004;

	private byte[] checksum;

	private BHIMFooter() {
	}

	public static BHIMFooter calculate(BHIMHeader basisHeader, BHIMBody basisBody) {
		BHIMFooter ret = new BHIMFooter();

		try {
			byte[] headerBytes = basisHeader.convertToBytes(),
					bodyBytes = basisBody.convertToBytes(),
					headerBodyBytes = new byte[headerBytes.length + bodyBytes.length];

			System.arraycopy(headerBytes, 0,
							 headerBodyBytes, 0,
							 headerBytes.length);
			System.arraycopy(bodyBytes, 0,
							 headerBodyBytes, headerBytes.length - 1,
							 bodyBytes.length);
			ret.checksum = DigestionUtils.md5(headerBodyBytes);
		}
		catch (Exception ex) {
			Logger.getGlobal().log(Level.WARNING, "Could not create MD5 checksum. Checksum bits all set to 0.", ex);
			ret.checksum = new byte[MD5_LEN];
		}

		return ret;
	}

	@Override
	public long size() {
		return SIZE;
	}

	@Override
	public byte[] convertToBytes() {
		byte[] ret = new byte[MD5_LEN + EOT_LEN];

		// Checksum
		System.arraycopy(checksum, 0, ret, 0, checksum.length);

		// End of Transmission
		ret[ret.length - 1] = END_OF_TRANSMISSION;

		return ret;
	}

	public void setFieldsFromBytes(byte[] fullEncodedMessage) throws FailedValidationException {
		int offset = fullEncodedMessage.length - SIZE;
		// Checksum
		byte[] temp = new byte[offset - 1];
		System.arraycopy(fullEncodedMessage, 0,
						 temp, 0,
						 offset - 1);
		checksum = DigestionUtils.md5(temp);

		if (!DigestionUtils.validateWithMD5Checksum(temp, checksum)) {
			throw new FailedValidationException();
		}
	}



	public abstract static class BHIMFooterField extends Field<Object, CharSequence> {

		public BHIMFooterField(CharSequence initName, Object initValue) {
			super(initName, initValue);
		}
	}

}
