package com.gurella.engine.managedobject;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.event.EventService;
import com.gurella.engine.event.EventSubscription;
import com.gurella.engine.pool.PoolService;

class ObjectSubscriptionAttachment extends Attachment<ObjectSubscriptionAttachment.ObjectSubscription>
		implements Poolable {
	static ObjectSubscriptionAttachment obtain(int objectId, EventSubscription subscriber) {
		ObjectSubscriptionAttachment attachment = PoolService.obtain(ObjectSubscriptionAttachment.class);
		attachment.value.objectId = objectId;
		attachment.value.subscriber = subscriber;
		return attachment;
	}

	ObjectSubscriptionAttachment() {
		value = new ObjectSubscription();
	}

	@Override
	protected void attach() {
		EventService.subscribe(value.objectId, value.subscriber);
	}

	@Override
	protected void detach() {
		EventService.unsubscribe(value.objectId, value.subscriber);
	}

	@Override
	public void reset() {
		value.reset();
	}

	static class ObjectSubscription implements Poolable {
		int objectId;
		EventSubscription subscriber;

		static ObjectSubscription obtain(int objectId, EventSubscription subscriber) {
			ObjectSubscription objectSubscription = PoolService.obtain(ObjectSubscription.class);
			objectSubscription.objectId = objectId;
			objectSubscription.subscriber = subscriber;
			return objectSubscription;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + objectId;
			return prime * result + subscriber.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}

			ObjectSubscription other = (ObjectSubscription) obj;
			return objectId == other.objectId && subscriber == other.subscriber;
		}

		@Override
		public void reset() {
			objectId = -1;
			subscriber = null;
		}

		void free() {
			PoolService.free(this);
		}
	}
}
