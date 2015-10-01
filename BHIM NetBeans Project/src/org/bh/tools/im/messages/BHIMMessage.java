package org.bh.tools.im.messages;

import org.bh.tools.im.err.FailedValidationException;
import org.bh.tools.im.err.IncompleteMessageException;
import org.bh.tools.net.msg.Transmittable;



/**
 * BHIMMessage, made for BHIM, is copyright Blue Husky Programming ©2015 GPLv3 <hr/>
 *
 * A message ready to be sent across the network. To create a {@link BHIMMessage}, use
 * {@link BHIMMessageFactory#makeFromFactory(CharSequence)}. This class conforms to the BHIM Implementation Spec Opus 5.
 *
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * - 2015-07-05 (1.0.0) - Kyli created BHIMMessage
 * @since 2015-07-05
 */
public class BHIMMessage implements Transmittable<BHIMHeader, BHIMBody, BHIMFooter> {

	private BHIMHeader header;
	private BHIMBody body;
	private BHIMFooter footer;
	/** Indicates whether this message has been validated by its checksum */
	private Boolean valid = null;

	/**
	 * Creates a new BHIM message with the given header and body text
	 *
	 * @param initHeader   The initial header of this message
	 * @param initBodyText The initial body text (or body) of this message
	 */
	public BHIMMessage(BHIMHeader initHeader, CharSequence initBodyText) {
		body = BHIMBody.valueOf(initBodyText);
		header = initHeader;
		valid = true;
	}

	/**
	 * Creates a new BHIM message with the given encoded message, which should contain a proper header, body, and
	 * footer.
	 *
	 * @param encodedMessage the bytes of an encoded message
	 *
	 * @throws org.bh.tools.im.err.IncompleteMessageException If the given bytes do not represent a complete BHIM
	 *                                                        message (start byte, header, optional body, footer, end
	 *                                                        byte)
	 * @throws org.bh.tools.im.err.FailedValidationException  If the given message's checksum bytes don't match up with
	 *                                                        a recomputation of the given message's checksum
	 */
	public BHIMMessage(final byte[] encodedMessage) throws IncompleteMessageException, FailedValidationException {
		setFieldsFromBytes(encodedMessage);
	}



	/**
	 * Encodes the message into a series of bytes for transmission over a network. See the BHIM Implementation
	 * Specification § 3.4.2 for full documentation.
	 *
	 * @return the message and all its meta data in a series of bytes as per BHIM Impl Spec § 3.4.2
	 */
	// Note: This has a lot of loop unrolling for maximum speed. If you think you can make this more terse without
	// costing performance, go for it!
	@Override
	public byte[] convertToBytes() {
		byte[] headerBytes, bodyBytes = body.convertToBytes(), footerBytes;
		byte[] ret = new byte[BHIMHeader.SIZE + bodyBytes.length + BHIMFooter.SIZE];
		ret[0] = BHIMHeader.START_OF_HEADER;
		ret[ret.length - 1] = BHIMFooter.END_OF_TRANSMISSION;


		// Header
		headerBytes = header == null ? new byte[BHIMHeader.SIZE] : header.convertToBytes();
		System.arraycopy(headerBytes, 0,
						 ret, 0,
						 headerBytes.length);

		// Body
		System.arraycopy(bodyBytes, 0,
						 ret, BHIMHeader.SIZE - 1,
						 bodyBytes.length);

		// Footer
		footer = BHIMFooter.calculate(header, body);
		footerBytes = footer.convertToBytes();
		System.arraycopy(footerBytes, 0,
						 ret, BHIMHeader.SIZE + bodyBytes.length - 1,
						 footerBytes.length);

		return ret;
	}

	/**
	 * Uses the given array of bytes to set all fields in this message at once
	 *
	 * @param encodedMessage The complete message, including the full header, body, and footer.
	 *
	 * @throws IncompleteMessageException if the end-of-transmission byte is not the last item in the given array
	 * @throws FailedValidationException  if our computed checksum does not match the message's provided checksum
	 */
	private void setFieldsFromBytes(final byte[] encodedMessage) throws IncompleteMessageException,
																		FailedValidationException {
		if (encodedMessage == null
					|| encodedMessage.length == 0
					|| encodedMessage[0] != BHIMHeader.START_OF_HEADER
					|| encodedMessage[encodedMessage.length - 1] != BHIMFooter.END_OF_TRANSMISSION
					|| encodedMessage.length < BHIMHeader.SIZE + BHIMFooter.SIZE) {
			throw new IncompleteMessageException();
		}
		getHeader().setFieldsFromBytes(encodedMessage);

		// Message Body
		byte[] bodyBytes = new byte[encodedMessage.length - BHIMHeader.SIZE - BHIMFooter.SIZE];
		System.arraycopy(encodedMessage, BHIMHeader.SIZE,
						 bodyBytes, 0,
						 bodyBytes.length);
		this.getBody().setText(new String(bodyBytes));


		this.valid = false;
		getFooter().setFieldsFromBytes(encodedMessage);
		this.valid = true;
	}

	//<editor-fold defaultstate="collapsed" desc="getters">
	public boolean isValid() {
		if (valid == null) {
			valid = true;
			try {
				getFooter().setFieldsFromBytes(convertToBytes());
			}
			catch (FailedValidationException ex) {
				valid = false;
			}
		}
		return valid;
	}

	@Override
	public BHIMHeader getHeader() {
		if (header == null) {
			header = new BHIMHeader();
		}
		return header;
	}

	@Override
	public BHIMBody getBody() {
		if (body == null) {
			body = new BHIMBody(null);
		}
		return body;
	}

	@Override
	public BHIMFooter getFooter() {
		if (footer == null) {
			footer = BHIMFooter.calculate(getHeader(), getBody());
		}
		return footer;
	}
	//</editor-fold>

	@Override
	public String toString() {
		return "BHIMMessage={"
					   + "bodyText:\"" + body.toString().replace("\"", "\\\"") + "\""
					   + ";header:" + header
					   + ";valid:" + valid
					   + "}";
	}


	/**
	 * @deprecated Simply returns the text version of the bytes in this message. It's not very useful.
	 */
	@Override
	public CharSequence convertToText() {
		return new String(convertToBytes());
	}

	@Override
	public long size() {
		return BHIMHeader.SIZE + getBody().size() + BHIMFooter.SIZE;
	}

	/**
	 * @deprecated You really ought not to think of this as text. If you want the length of the message in bytes, look
	 * to {@link #size()}
	 * @return the length of the text version of this message
	 */
	@Override
	public int length() {
		return getHeader().length() + getBody().length() + getFooter().length();
	}
}
