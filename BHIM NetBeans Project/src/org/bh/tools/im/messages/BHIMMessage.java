package org.bh.tools.im.messages;

import bht.tools.util.math.Numbers;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bh.tools.im.struct.MessageType;
import org.bh.tools.im.util.DigestionUtils;
import org.bh.tools.im.util.LoggingUtils;

import static org.bh.tools.util.Do.S.s;



/**
 * BHIMMessage, made for BHIM, is copyright Blue Husky Programming ©2015 GPLv3 <hr/>
 *
 * A message ready to be sent across the network. To create a {@link BHIMMessage}, use
 * {@link BHIMMessageFactory#makeFromFactory(CharSequence)}
 *
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * - 2015-07-05 (1.0.0) - Kyli created BHIMMessage
 * @since 2015-07-05
 */
// TODO: implement org.bh.tools.net.msg.Transmittable
public class BHIMMessage {

	//<editor-fold defaultstate="collapsed" desc="constants">
	//<editor-fold defaultstate="collapsed" desc="lengths">
	//<editor-fold defaultstate="collapsed" desc="header">
	/** The size of the Start-Of-Header, in Bytes. (<code>{@value}B</code>) */
	public static final byte SOH_LEN = 1;
	/** The size of the message type, in Bytes. (<code>{@value}B</code>) */
	public static final byte TYPE_LEN = 1;
	/** The size of the futureproofing (unused, reserved bits), in Bytes. (<code>{@value}B</code>) */
	public static final byte FUTUREPROOFING_LEN = 2;
	/** The size of the sender's UUID, in Bytes. (<code>{@value}B</code>) */
	public static final byte SENDER_LEN = 8;
	/** The size of the receiver's UUID, in Bytes. (<code>{@value}B</code>) */
	public static final byte RECEIVER_LEN = SENDER_LEN;
	/** The size of the timestamp, in Bytes. (<code>{@value}B</code>) */
	public static final byte TIMESTAMP_LEN = 8;
	/** The size of the entire header, in Bytes. (<code>{@value}B</code>) */
	public static final byte HEADER_LEN = SOH_LEN + TYPE_LEN + FUTUREPROOFING_LEN + SENDER_LEN + RECEIVER_LEN
												  + TIMESTAMP_LEN;
	//</editor-fold>
	//<editor-fold defaultstate="collapsed" desc="footer">
	/** The length of the MD5 Hash, in Bytes. (<code>{@value}B</code>) */
	public static final byte MD5_LEN = 16;
	/** The length of the End-Of-Transmission, in Bytes. (<code>{@value}B</code>) */
	public static final byte EOT_LEN = 1;
	/** The size of the entire footer, in Bytes. (<code>{@value}B</code>) */
	public static final byte FOOTER_LEN = (byte) (MD5_LEN + EOT_LEN);
	//</editor-fold>
	//</editor-fold>

	/** The {@code 1B} signal of the Start-Of-Header ({@code U+0001}) */
	public static final byte START_OF_HEADER = 0x0001;
	/** The {@code 1B} signal of the End-Of-Transmission ({@code U+0004}) */
	public static final byte END_OF_TRANSMISSION = 0x0004;
	//</editor-fold>

	private String bodyText;
	private MessageType typeCode;
	private long senderUUID, recipientUUID, sendTimeInMillis;
	/** Indicates whether this message has been validated by its checksum */
	private boolean valid;

	public BHIMMessage(CharSequence initBodyText, MessageType initType, long initSenderUUID, long initReceiverUUID,
					   long initSendTimeInMillis) {
		bodyText = s(initBodyText);
		typeCode = initType;
		senderUUID = initSenderUUID;
		recipientUUID = initReceiverUUID;
		sendTimeInMillis = initSendTimeInMillis;
		valid = true;
	}



	/**
	 * Encodes the message into a series of bytes for transmission over a network. See the BHIM Implementation
	 * Specification § 3.4.2 for full documentation.
	 *
	 * @return the message and all its meta data in a series of bytes as per BHIM Impl Spec § 3.4.2
	 */
	// Note: This has a lot of loop unrolling for maximum speed. If you think you can make this more terse without
	// introducing any loops, go for it!
	public byte[] getBytesToSend() {
		byte[] bodyBytes = bodyText.getBytes();
		byte[] senderBytes = Numbers.longToByteArray(senderUUID);
		byte[] receiverBytes = Numbers.longToByteArray(recipientUUID);
		byte[] sendTimeBytes = Numbers.longToByteArray(sendTimeInMillis);
		byte[] ret = new byte[HEADER_LEN + bodyBytes.length + FOOTER_LEN];

		// Start of Header
		ret[0x00] = START_OF_HEADER;

		// Message Type Code
		ret[0x01] = typeCode.CODE;

        // Futureproofing
		// slots 0x02 and 0x03 are reserved for future use.

		// Message Sender
		ret[0x04] = senderBytes[0];
		ret[0x05] = senderBytes[1];
		ret[0x06] = senderBytes[2];
		ret[0x07] = senderBytes[3];
		ret[0x08] = senderBytes[4];
		ret[0x09] = senderBytes[5];
		ret[0x0A] = senderBytes[6];
		ret[0x0B] = senderBytes[7];

		// Message Recipient
		ret[0x0C] = receiverBytes[0];
		ret[0x0D] = receiverBytes[1];
		ret[0x0E] = receiverBytes[2];
		ret[0x0F] = receiverBytes[3];
		ret[0x10] = receiverBytes[4];
		ret[0x11] = receiverBytes[5];
		ret[0x12] = receiverBytes[6];
		ret[0x13] = receiverBytes[7];

		// Time of Message Sending
		ret[0x14] = sendTimeBytes[0];
		ret[0x15] = sendTimeBytes[1];
		ret[0x16] = sendTimeBytes[2];
		ret[0x17] = sendTimeBytes[3];
		ret[0x18] = sendTimeBytes[4];
		ret[0x19] = sendTimeBytes[5];
		ret[0x1A] = sendTimeBytes[6];
		ret[0x1B] = sendTimeBytes[7];

		// Message Body
		System.arraycopy(bodyBytes, 0,
						 ret, HEADER_LEN,
						 bodyBytes.length);
		int offset = bodyBytes.length + 0x1C;

		// Checksum
		try {
			byte[] temp = new byte[offset - 1];
			System.arraycopy(ret, 0,
							 temp, 0,
							 offset - 1);
			byte[] checksum = DigestionUtils.md5(temp);
			ret[offset] = checksum[0x0];
			ret[offset + 0x1] = checksum[0x1];
			ret[offset + 0x2] = checksum[0x2];
			ret[offset + 0x3] = checksum[0x3];
			ret[offset + 0x4] = checksum[0x4];
			ret[offset + 0x5] = checksum[0x5];
			ret[offset + 0x6] = checksum[0x6];
			ret[offset + 0x7] = checksum[0x7];
			ret[offset + 0x8] = checksum[0x8];
			ret[offset + 0x9] = checksum[0x9];
			ret[offset + 0xA] = checksum[0xA];
			ret[offset + 0xB] = checksum[0xB];
			ret[offset + 0xC] = checksum[0xC];
			ret[offset + 0xD] = checksum[0xD];
			ret[offset + 0xE] = checksum[0xE];
			ret[offset + 0xF] = checksum[0xF];
		}
		catch (Exception ex) {
			Logger.getGlobal().log(Level.WARNING, "Could not create MD5 checksum. Checksum bits all set to 0.", ex);
			ret[offset]
			= ret[offset + 0x1]
			  = ret[offset + 0x2]
				= ret[offset + 0x3]
				  = ret[offset + 0x4]
					= ret[offset + 0x5]
					  = ret[offset + 0x6]
						= ret[offset + 0x7]
						  = ret[offset + 0x8]
							= ret[offset + 0x9]
							  = ret[offset + 0xA]
								= ret[offset + 0xB]
								  = ret[offset + 0xC]
									= ret[offset + 0xD]
									  = ret[offset + 0xE]
										= ret[offset + 0xF] = 0;
		}

		// End of Transmission
		ret[ret.length - EOT_LEN] = END_OF_TRANSMISSION;

		return ret;
	}

	public void setFieldsFromBytes(final byte[] encodedMessage) {

        // Start of Header 0x00

		// Message Type Code
		this.typeCode = MessageType.fromCode(encodedMessage[0x01]);

        // Futureproofing
		// slots 0x02 and 0x03 are reserved for future use.

		// Message Sender
		this.senderUUID = Numbers.longFromBytes(
				encodedMessage[0x04],
				encodedMessage[0x05],
				encodedMessage[0x06],
				encodedMessage[0x07],
				encodedMessage[0x08],
				encodedMessage[0x09],
				encodedMessage[0x0A],
				encodedMessage[0x0B]
		);

		// Message Recipient
		this.recipientUUID = Numbers.longFromBytes(
				encodedMessage[0x0C],
				encodedMessage[0x0D],
				encodedMessage[0x0E],
				encodedMessage[0x0F],
				encodedMessage[0x10],
				encodedMessage[0x11],
				encodedMessage[0x12],
				encodedMessage[0x13]
		);

		// Time of Message Sending
		this.sendTimeInMillis = Numbers.longFromBytes(
				encodedMessage[0x14],
				encodedMessage[0x15],
				encodedMessage[0x16],
				encodedMessage[0x17],
				encodedMessage[0x18],
				encodedMessage[0x19],
				encodedMessage[0x1A],
				encodedMessage[0x1B]
		);

		// Message Body
		byte[] bodyBytes = new byte[encodedMessage.length - HEADER_LEN - FOOTER_LEN];
		System.arraycopy(encodedMessage, HEADER_LEN,
						 bodyBytes, 0,
						 bodyBytes.length);
		this.bodyText = new String(bodyBytes);
		int offset = encodedMessage.length - FOOTER_LEN;

		// Checksum
		byte[] temp = new byte[offset - 1];
		System.arraycopy(encodedMessage, 0,
						 temp, 0,
						 offset - 1);
		byte[] checksum = DigestionUtils.md5(temp);
		checksum[0x0] = encodedMessage[offset];
		checksum[0x1] = encodedMessage[offset + 0x1];
		checksum[0x2] = encodedMessage[offset + 0x2];
		checksum[0x3] = encodedMessage[offset + 0x3];
		checksum[0x4] = encodedMessage[offset + 0x4];
		checksum[0x5] = encodedMessage[offset + 0x5];
		checksum[0x6] = encodedMessage[offset + 0x6];
		checksum[0x7] = encodedMessage[offset + 0x7];
		checksum[0x8] = encodedMessage[offset + 0x8];
		checksum[0x9] = encodedMessage[offset + 0x9];
		checksum[0xA] = encodedMessage[offset + 0xA];
		checksum[0xB] = encodedMessage[offset + 0xB];
		checksum[0xC] = encodedMessage[offset + 0xC];
		checksum[0xD] = encodedMessage[offset + 0xD];
		checksum[0xE] = encodedMessage[offset + 0xE];
		checksum[0xF] = encodedMessage[offset + 0xF];

		if (!(this.valid = DigestionUtils.validateWithMD5Checksum(temp, checksum))) {
			LoggingUtils.BACKGROUND.log(Level.SEVERE, "Message MD5 checksum does not match that in its footer!");
		}
	}

	/**
	 * The factory version of {@link #setFieldsFromBytes(byte[])}. See the BHIM Implementation
	 * Specification § 3.4.2 for full documentation.
	 *
	 * @param encodedMesage The message as a set of transmission bytes, as per BHIM Impl Spec § 3.4.2
	 *
	 * @return a {@link BHIMMessage} object with the information contained in the given encoded message
	 */
	public static BHIMMessage decode(final byte[] encodedMesage) {
		BHIMMessage ret = new BHIMMessage(null, null, 0, 0, 0);
		ret.setFieldsFromBytes(encodedMesage);
		return ret;
	}

	//<editor-fold defaultstate="collapsed" desc="getters">
	public String getBodyText() {
		return bodyText;
	}

	public MessageType getTypeCode() {
		return typeCode;
	}

	public long getSenderUUID() {
		return senderUUID;
	}

	public long getReceiverUUID() {
		return recipientUUID;
	}

	public long getSendTimeInMillis() {
		return sendTimeInMillis;
	}

	public boolean isValid() {
		return valid;
	}
    //</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="setters">
	public void setBodyText(String bodyText) {
		this.bodyText = bodyText;
	}

	public void setTypeCode(MessageType typeCode) {
		this.typeCode = typeCode;
	}

	public void setSenderUUID(long senderUUID) {
		this.senderUUID = senderUUID;
	}

	public void setReceiverUUID(long receiverUUID) {
		this.recipientUUID = receiverUUID;
	}

	public void setSendTimeInMillis(long sendTimeInMillis) {
		this.sendTimeInMillis = sendTimeInMillis;
	}
	//</editor-fold>

	@Override
	public String toString() {
		return "BHIMMessage={"
					   + "bodyText:\"" + bodyText.replace("\"", "\\\"") + "\""
					   + ";typeCode:" + typeCode
					   + ";senderUUID:" + Long.toHexString(senderUUID)
					   + ";recipientUUID:" + Long.toHexString(recipientUUID)
					   + ";sendTimeInMillis:" + sendTimeInMillis
					   + ";valid:" + valid
					   + "}";
	}
}
