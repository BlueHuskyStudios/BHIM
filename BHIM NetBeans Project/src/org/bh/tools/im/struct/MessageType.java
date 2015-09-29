package org.bh.tools.im.struct;

import org.bh.app.im.Main;

import static org.bh.tools.im.struct.MessageType.MessageTypeCategory.AUTOMATED_ERROR;
import static org.bh.tools.im.struct.MessageType.MessageTypeCategory.AUTOMATED_INFO;
import static org.bh.tools.im.struct.MessageType.MessageTypeCategory.USER_INITIATED;



/**
 * MessageType, made for BHIM, is copyright Blue Husky Programming ©2015 BH-PS-1 <hr/>
 *
 * An implementation of BHIM Impl Spec § 3.4.3
 *
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * - 2015-07-05 (1.0.0) - Kyli created MessageType
 * @since 2015-07-05
 */
public enum MessageType {

    PLAIN_TEXT(0x00),
    MEDIA(0x01),
    REQ_MSG_CHANGE(0x02),
    ANNOUNCE_INFO_UPDATE(0x10),
    REQ_INFO_CHANGE(0x11),
    ANNOUNCE_RECEIPT_SUCCESS(0x12),
    REQ_MSG_RESEND(0x80),
    SEND_FAILED(0x81),
    SEND_FAILED_PERMANENTLY(0x82);

    public final byte CODE;
    public final MessageTypeCategory CATEGORY;

    private MessageType(int code) {
        CODE = (byte) code;
        byte mask = (byte) (code & 0xF0);
        if (mask == USER_INITIATED.MASK) {
            CATEGORY = USER_INITIATED;
        } else if (mask == AUTOMATED_INFO.MASK) {
            CATEGORY = AUTOMATED_INFO;
        } else if (mask == AUTOMATED_ERROR.MASK) {
            CATEGORY = AUTOMATED_ERROR;
        } else { // this should never happen because it's all hard-coded above. If this happens, you did something wrong.
            throw new IllegalArgumentException(
                    "Message Type Code is invalid. Must match up with a MessageTypeCategory.MASK");
        }
    }

    public static MessageType fromCode(byte typeCode) {
        switch (typeCode) {
            case 0x00: return PLAIN_TEXT;
            case 0x01: return MEDIA;
            case 0x02: return REQ_MSG_CHANGE;

            case 0x10: return ANNOUNCE_INFO_UPDATE;
            case 0x11: return REQ_INFO_CHANGE;
            case 0x12: return ANNOUNCE_RECEIPT_SUCCESS;

            case (byte) 0x80: return REQ_MSG_RESEND;
            case (byte) 0x81: return SEND_FAILED;
            case (byte) 0x82: return SEND_FAILED_PERMANENTLY;

            default: throw new AssertionError(
                        "Type code is unrecognized in " + Main.APP_NAME + " version " + Main.APP_VERSION);
        }
    }



    public static enum MessageTypeCategory {
        USER_INITIATED(0x00),
        AUTOMATED_INFO(0x10),
        AUTOMATED_ERROR(0x80);

        public final byte MASK;

        private MessageTypeCategory(int mask) {
            MASK = (byte) mask;
        }
    }

}
