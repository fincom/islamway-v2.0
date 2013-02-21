package com.symbyo.islamway.service.processors;

/**
 * An interface to listen for post processing event.
 * @author kdehairy
 *
 */
public interface OnPostProccessingListener {
	void onPostProcessing( boolean result );
}
