package com.fzbox.framework.core.logic.builder;

import android.os.Handler;
import android.os.Message;

abstract public class BaseLogicBuilder {
	/**
	 * 私有handler
	 */
	final private Handler mHandler = new InnerHander(this);

	final protected Handler getHandler() {
		return mHandler;
	}

	protected void handleStateMessage(Message msg) {

	}

	private static class InnerHander extends Handler {
		private BaseLogicBuilder mBaseLogicBuilder;

		public InnerHander(BaseLogicBuilder baseLogicBuilder) {
			mBaseLogicBuilder = baseLogicBuilder;
		}

		public void handleMessage(Message msg) {
			if (mBaseLogicBuilder != null) {
				mBaseLogicBuilder.handleStateMessage(msg);
			}
		}
	}
}
