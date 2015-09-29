package org.bh.app.im;



import org.bh.app.im.flow.BHIMMainFlow;
import org.bh.app.im.fx.App;
import org.bh.tools.flow.FlowState;

import static org.bh.app.im.flow.BHIMMainFlow.MainStateEnum.APP_DID_START;
import static org.bh.app.im.flow.BHIMMainFlow.MainStateEnum.APP_WILL_START;



/**
 * BHIM is copyright Blue Husky Programming ©2015 BH-1-PS <hr/>
 *
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * - 2015-04-22 (1.0.0) - Kyli created Main
 * @since 2015-04-22
 */
public class Main {
    public static final String APP_NAME = "BHIM";
    public static final String APP_VERSION = "1.0.0";

    /** The main app flow */
    public static final BHIMMainFlow FLOW = new BHIMMainFlow();


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        FLOW.forceState(new FlowState<>(APP_WILL_START));

        App.launch(App.class, args);

        FLOW.forceState(new FlowState<>(APP_DID_START));
    }
}
