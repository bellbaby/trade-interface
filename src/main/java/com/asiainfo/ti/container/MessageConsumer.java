package com.asiainfo.ti.container;

import java.io.Closeable;

public interface MessageConsumer extends Closeable{
	
	public boolean getAndExecute() throws InterruptedException;
	
}
