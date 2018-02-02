package de.mxro.httpserver.netty3.tests;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import de.mxro.httpserver.HttpService;
import de.mxro.httpserver.netty3.Netty3Server;
import de.mxro.httpserver.netty3.Netty3ServerComponent;
import de.mxro.httpserver.services.HttpServices;
import delight.async.Operation;
import delight.async.callbacks.ValueCallback;
import delight.async.jre.Async;
import mx.jreutils.MxJREUtils;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("all")
public class TestBasicAuth {
  @Test
  public void test() {
    try {
      final HttpService service = HttpServices.requireBasicAuth("root", "dummy", HttpServices.data(new String("Hi").getBytes("UTF-8"), "text/plain"));
      final int port = MxJREUtils.nextAvailablePort(12329);
      final Operation<Netty3ServerComponent> _function = new Operation<Netty3ServerComponent>() {
        @Override
        public void apply(final ValueCallback<Netty3ServerComponent> cb) {
          Netty3Server.start(service, port, cb);
        }
      };
      final Netty3ServerComponent server = Async.<Netty3ServerComponent>waitFor(_function);
      HttpResponse<String> unauth = Unirest.get(("http://localhost:" + Integer.valueOf(port))).asString();
      int _status = unauth.getStatus();
      boolean _notEquals = (_status != 200);
      Assert.assertTrue(_notEquals);
      Assert.assertEquals(401, unauth.getStatus());
      final HttpResponse<String> auth = Unirest.get(("http://localhost:" + Integer.valueOf(port))).basicAuth("root", "dummy").asString();
      Assert.assertEquals("Hi", auth.getBody());
      Assert.assertEquals(200, auth.getStatus());
      unauth = Unirest.get(("http://localhost:" + Integer.valueOf(port))).basicAuth("root", "dummywrong").asString();
      int _status_1 = unauth.getStatus();
      boolean _notEquals_1 = (_status_1 != 200);
      Assert.assertTrue(_notEquals_1);
      Assert.assertEquals(401, unauth.getStatus());
      final Operation<Object> _function_1 = new Operation<Object>() {
        @Override
        public void apply(final ValueCallback<Object> cb) {
          server.stop(Async.<Object>asSimpleCallback(cb));
        }
      };
      Async.<Object>waitFor(_function_1);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
