package com.gurella.engine.subscriptions.application;

public enum CommonUpdatePriority {
	IO() {
		@Override
		public int getPriority() {
			return ioPriority;
		}
	},

	INPUT() {
		@Override
		public int getPriority() {
			return inputPriority;
		}
	},

	LOGIC() {
		@Override
		public int getPriority() {
			return logicPriority;
		}
	},

	PHYSICS() {
		@Override
		public int getPriority() {
			return physicPriority;
		}
	},

	UPDATE() {
		@Override
		public int getPriority() {
			return updatePriority;
		}
	},

	PRE_RENDER() {
		@Override
		public int getPriority() {
			return preRenderPriority;
		}
	},

	RENDER() {
		@Override
		public int getPriority() {
			return renderPriority;
		}
	},

	POST_RENDER() {
		@Override
		public int getPriority() {
			return postRenderPriority;
		}
	},

	CLEANUP() {
		@Override
		public int getPriority() {
			return cleanupPriority;
		}
	};

	public static final int ioPriority = -400;
	public static final int inputPriority = -300;
	public static final int logicPriority = -200;
	public static final int physicPriority = -100;
	public static final int updatePriority = 0;
	public static final int preRenderPriority = 100;
	public static final int renderPriority = 200;
	public static final int debugRenderPriority = 300;
	public static final int postRenderPriority = 400;
	public static final int cleanupPriority = 500;

	public abstract int getPriority();
}
