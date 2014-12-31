package com.silveroak.playerclient.service.HttpServer;

import org.apache.http.*;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.*;

public class DownloadFileHandler implements HttpRequestHandler {

	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		String target = request.getRequestLine().getUri();

		final File file = new File(target);
		if(!file.exists() || !file.canRead()){
			response.setStatusCode(HttpStatus.SC_NOT_FOUND);
		} else {
			final InputStream inputStream = new FileInputStream(file);
			HttpEntity entity = new EntityTemplate(new ContentProducer() {
				@Override
				public void writeTo(OutputStream outstream) throws IOException {
					byte[] b=new byte[1024];
					int length;
					while((length=inputStream.read(b))!=-1){
						outstream.write(b,0,length);
					}
				}
			});
			response.setStatusCode(HttpStatus.SC_OK);
			response.setHeader("Content-Type", "application/octet-stream");
			response.addHeader("Content-Disposition",
					"attachment;filename=" + file.getName());
			response.addHeader("Location", target);
			response.setEntity(entity);
		}
	}


}
