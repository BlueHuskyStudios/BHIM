package org.bh.tools.im.messages;

import org.bh.tools.net.msg.Body;



/**
 * BHIMBody, made for BHIM, is copyright Blue Husky Programming ©2015 GPLv3 <hr/>
 *
 * Represents the body of a BHIM message. This class conforms to the BHIM Implementation Spec Opus 5.
 *
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * - 2015-09-29 (1.0.0) - Kyli created BHIMBody
 * @since 2015-09-29
 */
public class BHIMBody extends Body<CharSequence> {

	public static BHIMBody valueOf(Object o) {
		if (o == null) {
			return null;
		}
		if (o instanceof BHIMBody) {
			return (BHIMBody) o;
		}
		if (o instanceof CharSequence) {
			return new BHIMBody((CharSequence) o);
		}
		return new BHIMBody(o.toString());
	}

	public BHIMBody(CharSequence initText) {
		super(initText);
	}

	public void setText(CharSequence newText) {
		super.text = RPMessage.valueOf(newText);
	}

	public CharSequence getText() {
		return super.text;
	}

}
