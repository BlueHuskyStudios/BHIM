package org.bh.tools.im.messages;

import org.bh.tools.util.ArrayPP;



public class RPMessageSegment implements CharSequence {
    public static RPMessageSegment split(CharSequence text, Delimiters[] delimiterses, Type defaultType) {
        ArrayPP<CharSequence> majorSegments = new ArrayPP<>();
        Integer firstSegmentStart = null, lastSegmentEnd = null;
        final int LENGTH = text.length();
scan:   for (int i = 0, l = LENGTH - 1; i < l; i++) {
dels:       for (Delimiters d : delimiterses) {
                if (d.isPrefixFor(text, i)) {
                    if (firstSegmentStart == null) {
                        firstSegmentStart = i;
                    }
                    RPMessageSegment newSegment = d.extractSegment(text, i);
                    if (!d.closes()) {
                        if (firstSegmentStart > 0) { // we didn't parse the first section of text
                            majorSegments.prepend(text.subSequence(0, firstSegmentStart));
                        }
                        return new RPMessageSegment(majorSegments.append(split(newSegment, delimiterses, defaultType)),
                                                    null);
                    }
                    i += newSegment.lengthWithOriginalDelimiters();
                    lastSegmentEnd = i;
                    majorSegments.add(newSegment);
                    continue scan;
                }
            }
        }
        if (majorSegments.length() == 0) { // We haven't found any matches
            return valueOf(text, defaultType.firstIn(delimiterses));
        }
        for (int i = 0, l = majorSegments.length(); i < l; i++) {
            majorSegments.set(i, split(majorSegments.get(i), delimiterses, defaultType));
        }
        if (firstSegmentStart > 0) { // we didn't parse the first section of text
            majorSegments.prepend(text.subSequence(0, firstSegmentStart));
        }
        if (lastSegmentEnd != null && lastSegmentEnd < LENGTH) { // we didn't parse the last section of text
            majorSegments.append(text.subSequence(lastSegmentEnd, LENGTH));
        }
        return new RPMessageSegment(majorSegments, null);
    }

    public static RPMessageSegment valueOf(CharSequence wholeText, Delimiters originalDelimiters) {
        if (wholeText instanceof RPMessageSegment) {
            RPMessageSegment rpms = (RPMessageSegment) wholeText;
            if (rpms.originalDelimiters == null) {
                rpms.originalDelimiters = originalDelimiters;
            }
            return rpms;
        }
        return new RPMessageSegment(wholeText, originalDelimiters);
    }



    private Delimiters originalDelimiters;
    private ArrayPP<CharSequence> segments;

    public RPMessageSegment(CharSequence wholeText, Delimiters initOriginalDelimiters) {
        this(wholeText instanceof RPMessageSegment
             ? ((RPMessageSegment) wholeText).segments
             : new ArrayPP<>(wholeText),
             initOriginalDelimiters == null ? Delimiters.EMPTY : initOriginalDelimiters);
    }

    public RPMessageSegment(ArrayPP<CharSequence> initSegments, Delimiters initOriginalDelimiters) {
        segments = initSegments;
        originalDelimiters = initOriginalDelimiters == null ? Delimiters.EMPTY : initOriginalDelimiters;
    }

    public Delimiters getOriginalDelimiters() {
        return originalDelimiters;
    }

    public ArrayPP<CharSequence> getSegments() {
        return segments;
    }

    public String getInnerText() {
        if (segments.length() == 1) {
            return segments.get(0).toString();
        }
        StringBuilder ret = new StringBuilder();
        for (CharSequence segment : segments) {
            ret.append(segment);
        }
        return ret.toString();
    }

    @Override
    public char charAt(int index) {
        return toStringWithOriginalDelimiters().charAt(index);
    }


    @Override
    public int length() {
        return toString().length();
    }

    public long lengthWithOriginalDelimiters() {
        return toStringWithOriginalDelimiters().length();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    public String toStringWithOriginalDelimiters() {
        if (segments.length() == 1) {
            if (segments.get(0) instanceof RPMessageSegment) {
                RPMessageSegment rpms = (RPMessageSegment) segments.get(0);
                return rpms.originalDelimiters == Delimiters.EMPTY
                       ? rpms.getInnerText()
                       : rpms.toStringWithOriginalDelimiters();
            }
            return segments.get(0).toString();
        }
        StringBuilder sb = new StringBuilder();
        for (CharSequence segment : segments) {
            if (segment instanceof RPMessageSegment) {
                RPMessageSegment rpms = (RPMessageSegment) segment;
                sb.append(rpms.originalDelimiters == Delimiters.EMPTY
                          ? rpms.getInnerText()
                          : rpms.toStringWithOriginalDelimiters());
                continue;
            }
            sb.append(segment);
        }
        return originalDelimiters.getOpen().toString() + sb + originalDelimiters.getClose();
    }

    public String toStringWithGenericDelimiters() {
        StringBuilder str = new StringBuilder();
        for (CharSequence segment : segments) {
            str.append(segment instanceof RPMessageSegment
                       ? ((RPMessageSegment) segment).toStringWithGenericDelimiters()
                       : segment);
        }
        if (originalDelimiters.getImplication() == null) { // if this is not special text.
            return str.toString();
        }
        return originalDelimiters.getImplication().openTag()
                       + str
                       + originalDelimiters.getImplication().closeTag();
    }

    @Override
    public String toString() {
        return toStringWithOriginalDelimiters();
    }



    public static enum Type {
        SPEECH,
        MOVE,
        OOC,
        THOUGHT;

        public String openTag() {
            return '<' + toString() + '>';
        }

        public String closeTag() {
            return "</" + toString() + '>';
        }

        private Delimiters firstIn(Delimiters... delimiterses) {
            for (Delimiters delimiters : delimiterses) {
                if (delimiters.getImplication() == this) {
                    return delimiters;
                }
            }
            return Delimiters.EMPTY;
        }
    }

}
