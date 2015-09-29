package org.bh.tools.im.messages;



public class RPMessage extends RPMessageSegment {

    public RPMessage(RPMessageSegment root) {
        super(root, root.getOriginalDelimiters());
    }

    public static RPMessage makeRPMessage(String rawMessage, Delimiters[] delimiterses) {
        return new RPMessage(split(rawMessage, delimiterses, Type.SPEECH));
    }

    public static RPMessage makeRPMessage(String rawMessage, Delimiters[] delimiterses,
                                          RPMessageSegment.Type defaultType) {
        return new RPMessage(split(rawMessage, delimiterses, defaultType));
    }
}
