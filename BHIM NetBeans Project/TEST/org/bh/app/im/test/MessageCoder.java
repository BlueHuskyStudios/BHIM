package org.bh.app.im.test;

import bht.tools.util.math.Numbers;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bh.tools.im.err.FailedValidationException;
import org.bh.tools.im.err.IncompleteMessageException;
import org.bh.tools.im.messages.BHIMBody;
import org.bh.tools.im.messages.BHIMHeader;
import org.bh.tools.im.messages.BHIMMessage;
import org.bh.tools.im.messages.Delimiters;
import org.bh.tools.im.messages.RPMessage;
import org.bh.tools.im.messages.RPMessageSegment;
import org.bh.tools.im.struct.MessageType;



/**
 * MessageCoder, made for BHIM, is copyright Blue Husky Programming ©2015 GPLv3 <hr/>
 *
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * - 2015-07-05 (1.0.0) - Kyli created MessageCoder
 * @since 2015-07-05
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class MessageCoder {
	public static void main(String[] args) {
		long start, end;
		start = System.nanoTime();
		RPMessage rpMessage = new RPMessage(new RPMessageSegment(
				"This is a test message. It is quite long and contains several paragraphs of Lorem Ipsum: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi volutpat augue quam, vitae condimentum ex fermentum sit amet. Sed dapibus semper tortor, non fermentum risus porta nec. Phasellus imperdiet nisi erat, eu pulvinar neque condimentum faucibus. Aenean fermentum tempor sem ac ultrices. In hac habitasse platea dictumst. Curabitur viverra massa in nisi tincidunt, quis fringilla ipsum lacinia. Vivamus pulvinar faucibus odio. Cras in turpis dolor. Nam vitae mi varius, eleifend mauris non, lobortis libero. Phasellus pulvinar tincidunt diam nec ultricies. Duis egestas ligula.\r\n"
				+ "\r\nPhasellus bibendum posuere posuere. Proin efficitur quis sem nec maximus. Praesent eget congue dui, vestibulum lacinia turpis. Etiam odio urna, tempor sit amet augue ut, tincidunt imperdiet quam. Sed vitae nisl turpis. Proin sit amet rhoncus odio, sed ultrices magna. Nam ut dignissim purus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce in risus a dui tristique viverra ut id lectus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Pellentesque turpis lorem, egestas non dignissim quis, vehicula at mi. Aenean ac fermentum magna. Curabitur pellentesque, turpis sed posuere ultrices, dui turpis lacinia.\r\n"
				+ "\r\nProin interdum ligula nec nunc ullamcorper consequat. Quisque orci erat, interdum sit amet ligula id, iaculis accumsan est. Etiam vulputate odio vel lacus interdum, pellentesque luctus tellus posuere. Aliquam a gravida orci. Mauris sed diam posuere, consequat nisl vel, tempus sem. Phasellus accumsan lorem ac sapien aliquam ullamcorper. Etiam dictum tellus nec pulvinar auctor. Curabitur vitae lacinia libero. Etiam tincidunt, turpis et placerat posuere, justo velit lacinia arcu, sed faucibus nibh purus vitae velit. Nulla facilisi. Sed feugiat justo mauris, ac semper ante fermentum ac. Morbi dui tortor, faucibus et dolor id, finibus aliquet est. In a vitae.",
				Delimiters.EMPTY));
		end = System.nanoTime();
		logTimespan("Creating RP message", end - start);

		System.out.println();
		start = System.nanoTime();
		BHIMHeader header = new BHIMHeader(
				MessageType.PLAIN_TEXT, // type
				0x1234_5678_CAFE_C0B0L, // sender
				0xBAD_B0B, // recipient
				System.currentTimeMillis() // time
		);
		BHIMBody body = new BHIMBody(rpMessage);
		BHIMMessage sendMessage = new BHIMMessage(header, body);
		end = System.nanoTime();
		logTimespan("Creating IM message", end - start);

		System.out.println();
		start = System.nanoTime();
		byte[] toSend = sendMessage.convertToBytes();
		end = System.nanoTime();
		logTimespan("Serializing message", end - start);

		System.out.println("\tSerialized message is " + Numbers.toPrettyBytes(toSend.length) + " long");

//        System.out.println();
//        start = System.nanoTime();
//        printBytes(toSend);
//        end = System.nanoTime();
//        logTimespan("Printing serialized message", end - start);

		System.out.println();
		start = System.nanoTime();
		BHIMMessage receiveMessage = null;
		try {
			receiveMessage = new BHIMMessage(toSend);
		}
		catch (IncompleteMessageException | FailedValidationException ex) {
			Logger.getLogger(MessageCoder.class.getName()).log(Level.SEVERE, null, ex);
		}
		end = System.nanoTime();
		logTimespan("Deserializing message", end - start);

		System.out.println("\tDeserializing was " + (receiveMessage != null && receiveMessage.isValid() ? "" : "NOT ")
								   + "successful!");

//        System.out.println();
//        start = System.nanoTime();
//        /* System.out.println */
//        s(receiveMessage);
//        end = System.nanoTime();
//        logTimespan("Stringifying message", end - start);

		printAllTimespans();

		printBytes(toSend);
	}

	private static final String BIN_STR_PAD_FMT = "%" + Byte.SIZE + "s";
	private static final int HEX_BYTE_LEN = Byte.BYTES * 2;
	private static final String HEX_STR_PAD_FMT = "%" + HEX_BYTE_LEN + "s";

	private static String toBinaryString(byte b) {
		String intBits = Integer.toBinaryString(b);
		if (intBits.length() > Byte.SIZE) {
			intBits = intBits.substring(intBits.length() - Byte.SIZE);
		}
		return String.format(BIN_STR_PAD_FMT, intBits).replace(' ', '0');
	}

	private static String toHexString(byte b) {
		String intBits = Integer.toHexString(b);
		if (intBits.length() > HEX_BYTE_LEN) {
			intBits = intBits.substring(intBits.length() - HEX_BYTE_LEN);
		}
		return String.format(HEX_STR_PAD_FMT, intBits).replace(' ', '0');
	}

	private static void printBytes(byte[] bytes) {
		final int COLS = Long.SIZE / Byte.SIZE;
		final int ROWS = bytes.length / COLS;
		final int BYTES_IN_SOLID_ROWS = ROWS * COLS;

		for (int row = 0; row < BYTES_IN_SOLID_ROWS; row += COLS) {
			for (int col = 0; col < COLS; col++) {
				System.out.print(toBinaryString(bytes[row + col]) + ' ');
			}
			System.out.print("    ");
			for (int col = 0; col < COLS; col++) {
				System.out.print(toHexString(bytes[row + col]) + ' ');
			}
			System.out.print("    ");
			for (int col = 0; col < COLS; col++) {
				System.out.print(((char) bytes[row + col]));
			}
			System.out.println();
		}
		if (BYTES_IN_SOLID_ROWS < bytes.length) {
			final byte LAST_ROW_LEN = (byte) (bytes.length - BYTES_IN_SOLID_ROWS);

			System.out.print(toBinaryString(bytes[BYTES_IN_SOLID_ROWS]) + ' ');
			if (LAST_ROW_LEN > 1) {
				System.out.print(toBinaryString(bytes[BYTES_IN_SOLID_ROWS + 1]) + ' ');
			}
			if (LAST_ROW_LEN > 2) {
				System.out.print(toBinaryString(bytes[BYTES_IN_SOLID_ROWS + 2]) + ' ');
			}
			if (LAST_ROW_LEN > 3) {
				System.out.print(toBinaryString(bytes[BYTES_IN_SOLID_ROWS + 3]) + ' ');
			}

			System.out.print(String
					.format("%" + (((COLS - LAST_ROW_LEN) * Byte.SIZE) + (COLS - LAST_ROW_LEN) + 4) + "s", ""));

			System.out.print(toHexString(bytes[BYTES_IN_SOLID_ROWS]) + ' ');
			if (LAST_ROW_LEN > 1) {
				System.out.print(toHexString(bytes[BYTES_IN_SOLID_ROWS + 1]) + ' ');
			}
			if (LAST_ROW_LEN > 2) {
				System.out.print(toHexString(bytes[BYTES_IN_SOLID_ROWS + 2]) + ' ');
			}
			if (LAST_ROW_LEN > 3) {
				System.out.print(toHexString(bytes[BYTES_IN_SOLID_ROWS + 3]) + ' ');
			}

			System.out.print(String.format("%" + (((COLS - LAST_ROW_LEN) * HEX_BYTE_LEN) + (COLS - LAST_ROW_LEN) + 4)
												   + "s", ""));

			System.out.print((char) (bytes[BYTES_IN_SOLID_ROWS]));
			if (LAST_ROW_LEN > 1) {
				System.out.print((char) (bytes[BYTES_IN_SOLID_ROWS + 1]));
			}
			if (LAST_ROW_LEN > 2) {
				System.out.print((char) (bytes[BYTES_IN_SOLID_ROWS + 2]));
			}
			if (LAST_ROW_LEN > 3) {
				System.out.print((char) (bytes[BYTES_IN_SOLID_ROWS + 3]));
			}
		}
		System.out.println();
	}

	private static final NumberFormat FMT = NumberFormat.getNumberInstance();
	private static final Map<String, Long> TIMES = new LinkedHashMap<>();

	private static void logTimespan(String name, long span) {
		TIMES.put(name, span);
		printTimespan(name);
	}

	private static String fmt(double val) {
		return FMT.format(val);
	}

	private static void printTimespan(String name) {
		long span = TIMES.get(name);
		System.out.println(formatTimespan(name, span));
	}

	private static String formatTimespan(String name, long span) {
		return name + ": " + fmt(span) + "ns (" + fmt(span / 1_000_000_000D) + "s)";
	}

	private static void printAllTimespans() {
		System.out.println("\r\n\r\n=== TOTALS ===\r\n\r\n");
		TIMES.keySet().stream().forEachOrdered((name) -> printTimespan(name));

		System.out.println(formatTimespan("GRAND TOTAL", TIMES.values().parallelStream().reduce((t, u) -> t + u).get()));
	}
}
