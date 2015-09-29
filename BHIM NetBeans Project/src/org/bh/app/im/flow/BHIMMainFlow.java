package org.bh.app.im.flow;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bh.app.im.flow.BHIMMainFlow.MainStateEnum;
import org.bh.tools.flow.Flow;
import org.bh.tools.flow.FlowReliant;
import org.bh.tools.flow.FlowState;
import org.bh.tools.util.ArrayPP;

import static org.bh.app.im.flow.BHIMMainFlow.MainStateEnum.*;
import static org.bh.tools.util.ImmutableArrayPP.ArrayPosition.START;
import static org.bh.tools.util.ImmutableArrayPP.SearchBehavior.ANY;



/**
 * BHIMFlowManager, made for BHIM, is copyright Blue Husky Programming ©2015 GPLv3 <hr/>
 *
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * - 2015-08-30 (1.0.0) - Kyli created BHIMFlowManager
 * @since 2015-08-30
 */
public class BHIMMainFlow implements Flow<MainStateEnum> {

	private final LinkedHashMap<FlowReliant, ArrayPP<MainStateEnum>> orderedListeners = new LinkedHashMap<>();
	private final HashMap<FlowReliant, ArrayPP<MainStateEnum>> parallelListeners = new HashMap<>();

	private final HashMap<MainStateEnum, Boolean> triggeredStates = new HashMap<>(MainStateEnum.values().length);

	@Override
	public boolean requestState(FlowState<MainStateEnum> state) {
		if (state == null) {
			return false;
		}
		if (stateIsValidNow(state.getState())) {
			forceState(state);
			return true;
		}
		return false;
	}

	@Override
	public void forceState(FlowState<MainStateEnum> state) {
		if (state == null) {
			return;
		}
		new Thread(() -> parallelListeners
				.entrySet()
				.parallelStream()
				.filter((t) -> t != null && t.getValue().contains(state.getState()))
				.forEach((t) -> t.getKey().flowRequiresState(state)),
				   "BHIM Main Flow parallel trigger"
		).start();
		orderedListeners
				.entrySet()
				.parallelStream()
				.filter((t) -> t != null && t.getValue().contains(state.getState()))
				.forEach((t) -> t.getKey().flowRequiresState(state));
	}

	@Override
	public void listenForState(FlowReliant listener, FlowState<MainStateEnum> state) {
		if (state == null) {
			return;
		}
		HashMap<FlowReliant, ArrayPP<MainStateEnum>> listeners = state.isTimingCritical()
																 ? orderedListeners
																 : parallelListeners;
		ArrayPP<MainStateEnum> states = listeners.get(listener);
		if (states == null) {
			states = new ArrayPP<>(state.getState());
		} else {
			states.add(state.getState());
		}
		listeners.put(listener, states); // TODO: Is this necessary?
	}

	@Override
	public void stopListeningForState(FlowReliant listener, FlowState<MainStateEnum> state) {
		if (state == null) {
			return;
		}
		HashMap<FlowReliant, ArrayPP<MainStateEnum>> listeners = state.isTimingCritical()
																 ? orderedListeners
																 : parallelListeners;
		ArrayPP<MainStateEnum> states = listeners.get(listener);
		if (states == null) {
			// listener wasn't listening, so there's no state to remove
			return;
		} else {
			states.remove(ANY, state.getState(), START);
		}
		if (states.isEmpty()) { // if we're not listening, free up some memory and maybe even computing time.
			listeners.remove(listener);
		} else {
			listeners.put(listener, states); // TODO: Is this necessary?
		}
	}

	@Override
	public boolean stateIsValidNow(MainStateEnum state) {
//        boolean appWillStart = triggeredStates.get(APP_WILL_START);
		boolean appDidStart = triggeredStates.get(APP_DID_START);
		boolean appWillExit = triggeredStates.get(APP_WILL_EXIT);
		boolean appDidExit = triggeredStates.get(APP_DID_EXIT);

		switch (state) {
			case APP_WILL_START:
				return !appDidStart
							   && !appWillExit
							   && !appDidExit;
			case APP_DID_START:
				return !appWillExit
							   && !appDidExit;
			case APP_WILL_EXIT:
			case APP_DID_EXIT:
				return !appDidExit;
		}
		return false;
	}



	public static enum MainStateEnum {
		APP_WILL_START,
		APP_DID_START,
		APP_WILL_EXIT,
		APP_DID_EXIT;
	}

}
