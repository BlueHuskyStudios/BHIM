package org.bh.tools.im.messages;



public class RPMessage extends RPMessageSegment {

	public static RPMessage valueOf(Object o) {
		if (o == null) {
			return null;
		}
		if (o instanceof RPMessage) {
			return (RPMessage) o;
		}
		if (o instanceof RPMessageSegment) {
			return new RPMessage((RPMessageSegment) o);
		}
		if (o instanceof CharSequence) {
			return makeRPMessage((CharSequence) o, new Delimiters[0]);
		}
		return makeRPMessage(o.toString(), new Delimiters[0]);
	}

	public RPMessage(RPMessageSegment root) {
		super(root, root.getOriginalDelimiters());
	}

	public static RPMessage makeRPMessage(CharSequence rawMessage, Delimiters[] delimiterses) {
		return makeRPMessage(rawMessage, delimiterses, Type.SPEECH);
	}

	public static RPMessage makeRPMessage(CharSequence rawMessage, Delimiters[] delimiterses,
										  RPMessageSegment.Type defaultType) {
		if (rawMessage instanceof RPMessage) {
			return (RPMessage) rawMessage;
		}
		if (rawMessage instanceof RPMessageSegment) {
			return new RPMessage((RPMessageSegment) rawMessage);
		}
		return new RPMessage(split(rawMessage, delimiterses, defaultType));
	}
}
