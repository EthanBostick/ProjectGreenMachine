package io.github.ethanBostick.core;

import io.github.ethanBostick.events.Event;

public interface Observer{
	void onEvent(Event event);
}
