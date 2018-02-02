package de.mxro.httpserver.netty3.tests

import com.mashape.unirest.http.Unirest
import de.mxro.httpserver.netty3.Netty3Server
import de.mxro.httpserver.services.HttpServices
import delight.async.jre.Async
import mx.jreutils.MxJREUtils
import org.junit.Assert
import org.junit.Test

class TestBasicAuth {

	@Test
	def void test() {
		
		val service = HttpServices.requireBasicAuth("root", "dummy", HttpServices.data(new String("Hi").getBytes("UTF-8"), "text/plain"))
		
		
		val port = MxJREUtils.nextAvailablePort(12329);
		
		val server = Async.waitFor [cb |
			Netty3Server.start(service, port, cb)
		]
		
		
		var unauth = Unirest.get("http://localhost:"+port).asString
		Assert.assertTrue(unauth.status != 200)
		Assert.assertEquals(401, unauth.status)
		
		val auth = Unirest.get("http://localhost:"+port).basicAuth("root", "dummy").asString
		Assert.assertEquals("Hi", auth.body)
		Assert.assertEquals(200, auth.status)
		
		unauth = Unirest.get("http://localhost:"+port).basicAuth("root", "dummywrong").asString
		Assert.assertTrue(unauth.status != 200)
		Assert.assertEquals(401, unauth.status)
		
		
		Async.waitFor [cb |
			server.stop(Async.asSimpleCallback(cb))
		]
		
		
		
		
	}

}
