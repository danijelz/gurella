package com.gurella.engine.state;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Predicate;

public class StateMachineConfig<STATE> {
	private STATE initial;
	private boolean stack;

	private ObjectMap<STATE, StateConfig<STATE>> states = new ObjectMap<STATE, StateConfig<STATE>>();

	private StateConfig<STATE> stateConfig;
	private StateTransitionConfig<STATE> transition;

	public static <STATE> StateMachineConfig<STATE> initial(STATE initial) {
		return new StateMachineConfig<STATE>(initial, false);
	}

	public static <STATE> StateMachineConfig<STATE> initial(STATE initial, boolean stack) {
		return new StateMachineConfig<STATE>(initial, stack);
	}

	private StateMachineConfig(STATE initial, boolean stack) {
		this.initial = initial;
		this.stack = stack;
		stateConfig = getStateConfig(initial);
	}

	public StateMachineConfig<STATE> configure(STATE state) {
		stateConfig = getStateConfig(state);
		return this;
	}

	public StateMachineConfig<STATE> configure(STATE state, STATE parent) {
		configure(state);
		return setParent(parent);
	}

	private StateMachineConfig<STATE> setParent(STATE parent) {
		stateConfig.parent = parent;
		getStateConfig(parent).children.add(stateConfig.state);
		return this;
	}

	private StateConfig getStateConfig(STATE state) {
		StateConfig config = states.get(state);
		if (config == null) {
			config = new StateConfig<STATE>(state);
			states.put(state, config);
		}
		return config;
	}

	public StateMachineConfig transition(STATE destination) {
		getStateConfig(destination);
		transition = stateConfig.getTransitionConfig(destination);
		return this;
	}

	public StateMachineConfig enterAction(TransitionAction enterAction) {
		transition.enterAction = enterAction;
		return this;
	}

	public StateMachineConfig exitAction(TransitionAction exitAction) {
		transition.exitAction = exitAction;
		return this;
	}

	public StateMachineConfig guard(Predicate<StateMachineContext<STATE>> guard) {
		transition.guard = guard;
		return this;
	}

	public interface TransitionAction {
		boolean process();
	}

	private static class StateConfig<STATE> {
		private STATE state;
		private boolean reentrant;
		private ObjectMap<STATE, StateTransitionConfig<STATE>> validTransitions = new ObjectMap<STATE, StateTransitionConfig<STATE>>();
		private STATE parent;
		private Array<STATE> children = new Array<STATE>();

		StateConfig(STATE state) {
			this.state = state;
		}

		StateTransitionConfig<STATE> getTransitionConfig(STATE destination) {
			StateTransitionConfig<STATE> transitionConfig = validTransitions.get(destination);
			if (transitionConfig == null) {
				transitionConfig = new StateTransitionConfig<STATE>();
				validTransitions.put(destination, transitionConfig);
			}
			return transitionConfig;
		}
	}

	private static class StateTransitionConfig<STATE> implements StateTransition<STATE> {
		TransitionAction exitAction;
		TransitionAction enterAction;
		TransitionAction currentAction;
		Predicate<StateMachineContext<STATE>> guard;

		@Override
		public boolean process() {
			if (currentAction == null) {
				currentAction = exitAction == null ? enterAction : exitAction;
				if (currentAction == null) {
					return true;
				}
			} else if (currentAction == exitAction && currentAction.process()) {
				currentAction = enterAction;
				if (currentAction == null) {
					return true;
				}
			} else if (currentAction == enterAction && currentAction.process()) {
				currentAction = null;
				return true;
			}

			return false;
		}

	}
}
