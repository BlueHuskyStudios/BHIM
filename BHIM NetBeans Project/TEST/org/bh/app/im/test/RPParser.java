package org.bh.app.im.test;

import org.bh.tools.flow.FlowState;
import org.bh.tools.im.messages.Delimiters;
import org.bh.tools.im.messages.RPMessage;

import static org.bh.app.im.Main.FLOW;
import static org.bh.app.im.flow.BHIMMainFlow.MainStateEnum.APP_WILL_START;
import static org.bh.tools.im.messages.RPMessageSegment.Type.*;



/**
 * RPParser, made for BHIM, is copyright Blue Husky Programming ©2015 GPLv3 <hr/>
 *
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * - 2015-08-30 (1.0.0) - Kyli created RPParser
 * @since 2015-08-30
 */
public class RPParser {

    /**
     * The main launcher for BHIM
     *
     * Test cases: (. = To do, ! = working fine)
     * <pre>
     * ! Hello, World!
     * ! /me says "Hello, World!"
     * ! /me waves to you "How are you doing?" [it's been a while]
     * . /me pokes // are you there?
     * </pre>
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        FLOW.forceState(new FlowState<>(APP_WILL_START, false));

        System.out.println("Starting...");
        Delimiters[] delimiterses = new Delimiters[]{
            new Delimiters("\"", "\"", SPEECH),
            new Delimiters("/me ", MOVE),
            new Delimiters("*", "*", MOVE),
            new Delimiters("[", "]", OOC),
            new Delimiters("//", OOC),
            new Delimiters("<", ">", THOUGHT)
        };

        System.out.println("Creating and splitting tests...");
        RPMessage[] tests = {
            RPMessage.makeRPMessage("/me pokes // you there?", delimiterses, SPEECH),
            RPMessage.makeRPMessage("Finally!", delimiterses, SPEECH),
            RPMessage.makeRPMessage("/me giggles and jumps \"YAY!\"", delimiterses, SPEECH)
        };

        System.out.println("Printing results...");
        for (RPMessage rpm : tests) {
            System.out.println(rpm.toString() + "\n\t"
                                       + rpm.getSegments() + "\n\t"
                                       + rpm.toStringWithOriginalDelimiters() + "\n\t"
                                       + rpm.toStringWithGenericDelimiters());
        }

//        Scanner scan = new Scanner(System.in);
//        String in = null;
//        do {
//            System.out.print("> ");
//            in = scan.nextLine();
//            if (in == null || in.isEmpty()) {
//                break;
//            }
//            System.out.println(RPMessage.makeRPMessage(in, delimiterses).toStringWithGenericDelimiters());
//        } while (in != null && !in.isEmpty());
    }
}
