package de.mxro.httpserver.netty3.examples.staticfile

import de.mxro.httpserver.netty3.Netty3Server
import de.mxro.httpserver.resources.Resources
import delight.async.AsyncCommon
import delight.async.jre.Async
import de.mxro.httpserver.services.HttpServices

class StartStaticFileServer {
	def static void main(String[] args) {
		
		val source = Resources.cache(Resources.forWeb(Resources.fromClasspath(StartStaticFileServer)));
		
		val service = HttpServices.resources(source)

		Async.waitFor([cb | service.start(AsyncCommon.asSimpleCallback(cb))])


		val server = Async.waitFor([cb | Netty3Server.start(service, 8081, cb) ])
		 
		 println("Download file from at http://localhost:8081/bigfile.js")
		 println('Press key to stop server')
		 System.in.read
		
		Async.waitFor([cb | server.stop(AsyncCommon.asSimpleCallback(cb))]);
		
		Async.waitFor([cb | service.stop(AsyncCommon.asSimpleCallback(cb))])
	}
}