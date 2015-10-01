package org.bh.tools.im.messages;

import bht.tools.util.math.Numbers;

import org.bh.tools.im.struct.MessageType;
import org.bh.tools.net.msg.Header;



/**
 * BHIMHeader, made for BHIM, is copyright Blue Husky Programming ©2015 GPLv3 <hr/>
 *
 * The header for a BHIM message. This class conforms to the BHIM Implementation Spec Opus 5.
 *
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * - 2015-09-29 (1.0.0) - Kyli created BHIMHeader
 * @since 2015-09-29
 */
public class BHIMHeader extends Header {
	//<editor-fold defaultstate="collapsed" desc="lengths">
	/** The size of the Start-Of-Header, in Bytes. (<code>{@value}B</code>) */
	public static final byte SOH_LEN = 1;
	/** The size of the message type, in Bytes. (<code>{@value}B</code>) */
	public static final byte TYPE_LEN = 1;
	/** The size of the futureproofing (unused, reserved bits), in Bytes. (<code>{@value}B</code>) */
	public static final byte FUTUREPROOFING_LEN = 6;
	/** The size of the sender's UUID, in Bytes. (<code>{@value}B</code>) */
	public static final byte SENDER_LEN = 8;
	/** The size of the receiver's UUID, in Bytes. (<code>{@value}B</code>) */
	public static final byte RECEIVER_LEN = SENDER_LEN;
	/** The size of the timestamp, in Bytes. (<code>{@value}B</code>) */
	public static final byte TIMESTAMP_LEN = 8;
	/** The size of the entire header, in Bytes. (<code>{@value}B</code>) */
	public static final byte SIZE = SOH_LEN + TYPE_LEN + FUTUREPROOFING_LEN + SENDER_LEN + RECEIVER_LEN
											+ TIMESTAMP_LEN;
	//</editor-fold>
	/** The {@code 1B} signal of the Start-Of-Header ({@code U+0001}) */
	public static final byte START_OF_HEADER = 0x0001;

	static {
		assert SIZE == 0x1C; // SIZE must be the same as the number of bytes we're manually putting into the array in {@link #convertToBytes()}
	}

	private MessageType typeCode;
	private long senderUUID, recipientUUID, sendTimeInMillis;

	public BHIMHeader(MessageType initTypeCode, long initSenderUUID, long initRecipientUUID, long initSendTimeInMillis) {
		typeCode = initTypeCode;
		senderUUID = initSenderUUID;
		recipientUUID = initRecipientUUID;
		sendTimeInMillis = initSendTimeInMillis;
	}

	/**
	 * Creates a new header with default values (plain text, UUIDs of {@code 0}, and the current time).
	 *
	 * @deprecated it's always recommended you manually set the values of a header
	 */
	public BHIMHeader() {
		this(MessageType.PLAIN_TEXT, 0L, 0L, System.currentTimeMillis());
	}



	@Override
	public long size() {
		return SIZE;
	}

	@Override
	public byte[] convertToBytes() {
		byte[] senderBytes = Numbers.longToByteArray(senderUUID);
		byte[] receiverBytes = Numbers.longToByteArray(recipientUUID);
		byte[] sendTimeBytes = Numbers.longToByteArray(sendTimeInMillis);
		byte[] ret = new byte[SIZE];

		// Start of Header
		ret[0x00] = START_OF_HEADER;

		// Message Type Code
		ret[0x01] = typeCode.CODE;

        // Futureproofing
		// slots 0x02 through 0x07 are reserved for future use.

		// Message Sender
		ret[0x08] = senderBytes[0];
		ret[0x09] = senderBytes[1];
		ret[0x0A] = senderBytes[2];
		ret[0x0B] = senderBytes[3];
		ret[0x0C] = senderBytes[4];
		ret[0x0D] = senderBytes[5];
		ret[0x0E] = senderBytes[6];
		ret[0x0F] = senderBytes[7];

		// Message Recipient
		ret[0x10] = receiverBytes[0];
		ret[0x11] = receiverBytes[1];
		ret[0x12] = receiverBytes[2];
		ret[0x13] = receiverBytes[3];
		ret[0x14] = receiverBytes[4];
		ret[0x15] = receiverBytes[5];
		ret[0x16] = receiverBytes[6];
		ret[0x17] = receiverBytes[7];

		// Time of Message Sending
		ret[0x18] = sendTimeBytes[0];
		ret[0x19] = sendTimeBytes[1];
		ret[0x1A] = sendTimeBytes[2];
		ret[0x1B] = sendTimeBytes[3];
		ret[0x1C] = sendTimeBytes[4];
		ret[0x1D] = sendTimeBytes[5];
		ret[0x1E] = sendTimeBytes[6];
		ret[0x1F] = sendTimeBytes[7];

		return ret;
	}

	public void setFieldsFromBytes(final byte[] encodedMessage) {

        // Start of Header 0x00

		// Message Type Code
		this.typeCode = MessageType.fromCode(encodedMessage[0x01]);

        // Futureproofing
		// slots 0x02 through 0x07 are reserved for future use.

		// Message Sender
		this.senderUUID = Numbers.longFromBytes(
				encodedMessage[0x08],
				encodedMessage[0x09],
				encodedMessage[0x0A],
				encodedMessage[0x0B],
				encodedMessage[0x0C],
				encodedMessage[0x0D],
				encodedMessage[0x0E],
				encodedMessage[0x0F]
		);

		// Message Recipient
		this.recipientUUID = Numbers.longFromBytes(
				encodedMessage[0x10],
				encodedMessage[0x11],
				encodedMessage[0x12],
				encodedMessage[0x13],
				encodedMessage[0x14],
				encodedMessage[0x15],
				encodedMessage[0x16],
				encodedMessage[0x17]
		);

		// Time of Message Sending
		this.sendTimeInMillis = Numbers.longFromBytes(
				encodedMessage[0x18],
				encodedMessage[0x19],
				encodedMessage[0x1A],
				encodedMessage[0x1B],
				encodedMessage[0x1C],
				encodedMessage[0x1D],
				encodedMessage[0x1E],
				encodedMessage[0x1F]
		);
	}

	@Override
	public String toString() {
		return "BHIMHeader={"
					   + "typeCode:" + typeCode
					   + ";senderUUID:" + Long.toHexString(senderUUID)
					   + ";recipientUUID:" + Long.toHexString(recipientUUID)
					   + ";sendTimeInMillis:" + sendTimeInMillis
					   + "}";
	}
}
