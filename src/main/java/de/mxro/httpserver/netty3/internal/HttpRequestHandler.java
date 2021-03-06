/*******************************************************************************
 * Copyright 2011 Max Erik Rohde http://www.mxro.de
 * 
 * All rights reserved.
 ******************************************************************************/
package de.mxro.httpserver.netty3.internal;

import delight.simplelog.Log;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.ssl.NotSslRecordException;

/**
 * 
 * @author <a href="http://www.mxro.de/">Max Rohde</a>
 * 
 */
public final class HttpRequestHandler extends SimpleChannelUpstreamHandler {

    protected final BytesHandler byteStreamHandler;

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {

        ctx.getChannel().close();

        final Throwable cause = e.getCause();

        if (cause instanceof IOException) {

            final IOException ioException = (IOException) cause;
            if (ioException.getMessage() != null && ioException.getMessage().contains("Connection reset by peer")) {
                Log.warn("Client disconnected before response was sent: " + e.getCause().getMessage());
            }
            Log.warn("IO Exception while processing message: " + cause.getMessage());
            return;
        }

        if (cause instanceof java.nio.channels.ClosedChannelException) {
            Log.warn("Client disconnected before response was sent.", cause);
            return;
        }
        if (cause instanceof javax.net.ssl.SSLHandshakeException || cause instanceof NotSslRecordException
                || cause instanceof javax.net.ssl.SSLException) {

            Log.warn("SSL connection cannot be established with client", cause);
            return;
        }
        
        if (cause instanceof IllegalArgumentException && cause.getMessage().startsWith("empty text")) {
        	Log.warn("Illegal HTTP version: empty text.");
        	return;
        }
        
        Log.warn("Error while processing HTTP", e.getCause());
        // HttpUtils.sendHttpError(e,
        // this.getClass().getName() + ": Unexpected exception occured [" +
        // e.getCause() + "].");

    }

    @Override
    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        super.channelConnected(ctx, e);
    }

    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        // System.out.println("closed!");
        super.channelClosed(ctx, e);
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        // System.out.println("msg processed");
        e.getChannel().getConfig().setConnectTimeoutMillis(3000);

        final HttpRequest request = (HttpRequest) e.getMessage();

        if (request.isChunked()) {
            HttpUtils.sendHttpError(e, this.getClass().getName() + ": Cannot process chunked requests.");
            return;
        }

        final ChannelBuffer buffer = request.getContent();

        if (buffer.readable()) {
            final byte[] receivedData = new byte[buffer.readableBytes()];
            buffer.readBytes(receivedData);
            byteStreamHandler.processRequest(receivedData, e);
            return;
        } else {
            byteStreamHandler.processRequest(new byte[0], e);
            return;
        }

    }

    public HttpRequestHandler(final BytesHandler byteStreamHandler) {
        super();
        this.byteStreamHandler = byteStreamHandler;

    }

}
