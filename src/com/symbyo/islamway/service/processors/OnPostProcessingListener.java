package com.symbyo.islamway.service.processors;

/**
 * An interface to listen for post processing event.
 * @author kdehairy
 *
 */
public interface OnPostProcessingListener {
	void onPostProcessing( boolean result );
}
