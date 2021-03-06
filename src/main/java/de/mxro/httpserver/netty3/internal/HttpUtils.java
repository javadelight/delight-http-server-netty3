package de.mxro.httpserver.netty3.internal;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpChunk;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

import delight.simplelog.Log;

public class HttpUtils {

	private final static boolean ENABLE_TRACE = false;

	public final static void sendFullHttpResponse(final MessageEvent event, final byte[] bytes, final int responseCode,
			final Map<String, List<String>> headerFields) {

		final HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(responseCode));

		if (headerFields != null) {
			for (final Entry<String, List<String>> header : headerFields.entrySet()) {

				
				if (header.getKey() != null) {
					response.headers().add(header.getKey(), header.getValue());
					if (ENABLE_TRACE) {
						Log.trace(HttpUtils.class,
								"Setting header " + header.getKey() + " Value: '" + header.getValue() + "'");
					}
				}
			}
		}

		response.headers().add(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);

		final ChannelFuture future;
		final ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(bytes);

		response.setContent(buffer);
		final int length;
		if (responseCode == 304) {
			length = 0;
		} else {
			length = response.getContent().readableBytes();

		}
		// if (!headerFields.containsKey(HttpHeaders.Names.CONTENT_LENGTH)) {
		response.headers().add(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(length));
		// }
		if (ENABLE_TRACE) {
			Log.println(HttpUtils.class, "Setting length "+length);
		}

		future = event.getChannel().write(response);

		future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

		// future.addListener(ChannelFutureListener.CLOSE);

	}

	public final static void sendHttpError(final MessageEvent event, final String message) {
		final HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

		ChannelBuffer buffer;
		try {
			buffer = ChannelBuffers.wrappedBuffer(message.getBytes("UTF-8"));

			response.setContent(buffer);
			response.headers().add(CONTENT_TYPE, "text/plain");
			final int length = response.getContent().readableBytes();
			response.headers().add(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(length));

			final ChannelFuture future = event.getChannel().write(response);
			future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

	}

	public final static void sendHttpError(final ExceptionEvent event, final String message) {
		final HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

		ChannelBuffer buffer;
		try {
			buffer = ChannelBuffers.wrappedBuffer(message.getBytes("UTF-8"));

			response.setContent(buffer);
			response.headers().add(CONTENT_TYPE, "text/plain");
			final int length = response.getContent().readableBytes();
			response.headers().add(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(length));

			final ChannelFuture future = event.getChannel().write(response);
			future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

	}

}
