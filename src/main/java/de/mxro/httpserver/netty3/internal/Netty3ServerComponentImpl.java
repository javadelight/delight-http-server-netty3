/*******************************************************************************
 * Copyright 2011 Max Erik Rohde http://www.mxro.de
 * 
 * All rights reserved.
 ******************************************************************************/
package de.mxro.httpserver.netty3.internal;

import delight.async.callbacks.SimpleCallback;
import delight.simplelog.Log;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.util.Timer;

import de.mxro.httpserver.netty3.Netty3ServerComponent;
import de.mxro.server.ComponentConfiguration;
import de.mxro.server.ComponentContext;

public final class Netty3ServerComponentImpl implements Netty3ServerComponent {
	
	private final boolean ENABLE_TRACE = false;
	
    protected final Channel channel;
    protected final int port;
    protected final ServerBootstrap bootstrap;
    protected final Timer timer;

    @Override
    public Channel getChannel() {

        return channel;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void stop(final SimpleCallback callback) {
    	
    	try {
            destroy(callback);

        } catch (final Throwable t) {
            callback.onFailure(t);
        }

    }

    public Netty3ServerComponentImpl(final Channel channel, final int port, final ServerBootstrap bootstrap,
            final Timer timer) {
        super();
        this.channel = channel;
        this.port = port;
        this.bootstrap = bootstrap;
        this.timer = timer;
        
        if (ENABLE_TRACE) {
        	Log.trace(this, "Starting server on port "+port);
        }
    }

    @Override
    public void start(final SimpleCallback callback) {
        throw new RuntimeException("Not supported!");
    }

    @Override
    public void injectConfiguration(final ComponentConfiguration conf) {
        throw new RuntimeException("Not supported!");
    }

    @Override
    public void injectContext(final ComponentContext context) {
        throw new RuntimeException("Not supported!");
    }

    @Override
    public ComponentConfiguration getConfiguration() {
        throw new RuntimeException("Not supported!");
    }

    @Override
    public void destroy(final SimpleCallback callback) {

        channel.close().awaitUninterruptibly(1000 * 20);
        timer.stop();
        bootstrap.releaseExternalResources();
        
        if (ENABLE_TRACE) {
        	Log.trace(this, "Stopped server on port "+port);
        }
        
        callback.onSuccess();

    }

}
