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

	public StateMachineConfig<STATE> setParent(STATE parent) {
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

	public StateMachineConfig interruption(STATE destination) {
		getStateConfig(destination);
		transition = transition.getInterruptConfig(destination);
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

	public StateMachineContext<STATE> build() {
		// TODO
		return null;
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
		private TransitionAction exitAction;
		private TransitionAction enterAction;
		private TransitionAction currentAction;
		private Predicate<StateMachineContext<STATE>> guard;
		private ObjectMap<STATE, StateTransitionConfig<STATE>> validInterrupts = new ObjectMap<STATE, StateTransitionConfig<STATE>>();

		StateTransitionConfig<STATE> getInterruptConfig(STATE destination) {
			StateTransitionConfig<STATE> transitionConfig = validInterrupts.get(destination);
			if (transitionConfig == null) {
				transitionConfig = new StateTransitionConfig<STATE>();
				validInterrupts.put(destination, transitionConfig);
			}
			return transitionConfig;
		}

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

	private static class ConfigurableStateMachineContext<STATE> implements StateMachineContext<STATE> {
		private STATE initial;
		private Array<STATE> stateStack;
		private ObjectMap<STATE, StateConfig<STATE>> states = new ObjectMap<STATE, StateConfig<STATE>>();

		@Override
		public STATE getInitialState() {
			return initial;
		}

		@Override
		public StateTransition<STATE> getTransition(STATE source, STATE destination) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public StateTransition<STATE> getInterruptTransition(STATE source, STATE currentDestination,
				StateTransition<STATE> currentTransition, STATE newDestination) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void stateChanged(STATE newState) {
		}

		@Override
		public void reset() {
		}
	}
}
