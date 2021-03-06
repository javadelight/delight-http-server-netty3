package de.mxro.httpserver.netty3.examples.post

import de.mxro.httpserver.netty3.Netty3Server
import de.mxro.httpserver.services.HttpServices
import delight.async.AsyncCommon
import delight.async.jre.Async
import delight.concurrency.jre.ConcurrencyJre
import java.util.HashMap

class StartPostServer {

	def static void main(String[] args) {
		
		val services = new HashMap
		
		
		
		services.put("/service", HttpServices.echo)
		services.put("*", HttpServices.data(PAGE.bytes, "text/html"))

		val server = Async.waitFor([cb |
			Netty3Server.start(HttpServices.dispatcher(ConcurrencyJre.create, services), 8081, cb)
		
			])
		 
		 println("Open page at http://localhost:8081")
		 println('Press key to stop server')
		 System.in.read
		
		Async.waitFor([cb | server.stop(AsyncCommon.asSimpleCallback(cb))]);
	}

	static val PAGE = '''
		
		<html>
		
		<body>
			<script src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
			
			
			<script>
				var text = "";
				for (var i=0;i<20;i++) {
					text += ""+Math.random();
				}
				setInterval(function() {
				 $.ajax({
				 	        type: "POST",
		                    url: "/service",
		                    data:  "Some big chunk of data follows: "+text,
		                    processData: false,
		                    contentType: 'text',
		                    dataType: 'text',
		                    success: function(data, textStatus, rew) {
		                        $("body").append(data);
		                        
		
		                       
		                    }
		                    
		                });
		                }, 500);
			
			
			</script>
			
			
		</body>
		
		
		</html>
		
	'''

}
