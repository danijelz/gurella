package com.gurella.engine.asset;

import com.gurella.engine.subscriptions.application.ApplicationEventSubscription;

interface GlContextInvalidatedListener extends ApplicationEventSubscription {
	void onGlContextInvalidated();
}
