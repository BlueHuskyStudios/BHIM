package org.bh.tools.im.err;

import org.bh.tools.net.err.CorruptedMessageException;



/**
 * FailedValidationException, made for BHIM, is copyright Blue Husky Programming ©2015 GPLv3 <hr/>
 *
 * Represents that a computed checksum did not match a cached checksum.
 *
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * - 2015-09-30 (1.0.0) - Kyli created FailedValidationException
 * @since 2015-09-30
 */
public class FailedValidationException extends CorruptedMessageException {
	/**
	 * Creates a new instance of {@link FailedValidationException} without detail message.
	 */
	public FailedValidationException() {
		super();
	}
}
