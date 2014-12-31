package com.silveroak.wifiplayer.service;

import com.silveroak.wifiplayer.constants.SystemConstant;
import com.silveroak.wifiplayer.utils.LogUtils;
import org.apache.http.*;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.*;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;


/**
 * Basic, yet fully functional and spec compliant, HTTP/1.1 file server.
 * <p>
 * Please note the purpose of this application is demonstrate the usage of
 * HttpCore APIs. It is NOT intended to demonstrate the most efficient way of
 * building an HTTP file server.
 *
 *
 */
public class HttpServer {

    private final static String TAG = HttpServer.class.getSimpleName();


    private static RouteService routeService;

    public static void start() throws Exception {
        routeService = RouteService.init();
        Thread t = new RequestListenerThread(SystemConstant.PORT.HTTP_SERVER_PORT);

        t.setDaemon(false);
        t.start();		//start the webservice server


    }




    static class WebServiceHandler implements HttpRequestHandler {


        public WebServiceHandler() {
            super();
        }


        public void handle(final HttpRequest request,
                           final HttpResponse response, final HttpContext context)
                throws HttpException, IOException {
            String method = request.getRequestLine().getMethod()
                    .toUpperCase(Locale.ENGLISH);
            String target = request.getRequestLine().getUri();

            if(method.equalsIgnoreCase("post")){
                 routeService.post(target,request,response,context);
            } else if("get".equalsIgnoreCase(method)){
                routeService.get(target,request,response,context);
            }
        }
    }


    static class RequestListenerThread extends Thread {


        private final ServerSocket serversocket;
        private final HttpParams params;
        private final HttpService httpService;


        public RequestListenerThread(int port)
                throws IOException {
            //
            this.serversocket = new ServerSocket(port);


            // Set up the HTTP protocol processor
            HttpProcessor httpproc = new ImmutableHttpProcessor(
                    new HttpResponseInterceptor[] {
                            new ResponseDate(), new ResponseServer(),
                            new ResponseContent(), new ResponseConnControl() });


            this.params = new BasicHttpParams();
            this.params
                    .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
                    .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE,
                            8 * 1024)
                    .setBooleanParameter(
                            CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
                    .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                    .setParameter(CoreProtocolPNames.ORIGIN_SERVER,
                            "HttpComponents/1.1");


            // Set up request handlers
            HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
            reqistry.register("*", new WebServiceHandler());  //WebServiceHandler用来处理webservice请求。


            this.httpService = new HttpService(httpproc,
                    new DefaultConnectionReuseStrategy(),
                    new DefaultHttpResponseFactory());
            httpService.setParams(this.params);
            httpService.setHandlerResolver(reqistry);		//为http服务设置注册好的请求处理器。


        }


        @Override
        public void run() {
            LogUtils.debug(TAG, "Listening on port "
                    + this.serversocket.getLocalPort());
            System.out.println("Thread.interrupted = " + Thread.interrupted());
            while (!Thread.interrupted()) {
                try {
                    // Set up HTTP connection
                    Socket socket = this.serversocket.accept();
                    DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
                    System.out.println("Incoming connection from "
                            + socket.getInetAddress());
                    conn.bind(socket, this.params);


                    // Start worker thread
                    Thread t = new WorkerThread(this.httpService, conn);
                    t.setDaemon(true);
                    t.start();
                } catch (InterruptedIOException ex) {
                    LogUtils.error(TAG,ex);
                    break;
                } catch (IOException e) {
                    LogUtils.error(TAG, "I/O error initialising connection thread: "
                            + e.getMessage());
                    break;
                }
            }
        }
    }


    static class WorkerThread extends Thread {


        private final HttpService httpservice;
        private final HttpServerConnection conn;


        public WorkerThread(final HttpService httpservice,
                            final HttpServerConnection conn) {
            super();
            this.httpservice = httpservice;
            this.conn = conn;
        }


        @Override
        public void run() {
            System.out.println("New connection thread");
            HttpContext context = new BasicHttpContext(null);
            try {
                while (!Thread.interrupted() && this.conn.isOpen()) {
                    this.httpservice.handleRequest(this.conn, context);
                }
            } catch (ConnectionClosedException ex) {
                LogUtils.error(TAG,ex);
                System.err.println("Client closed connection");
            } catch (IOException ex) {
                LogUtils.error(TAG, "I/O error:" + ex.toString());
            } catch (HttpException ex) {
                LogUtils.error(TAG, "Unrecoverable HTTP protocol violation: " + ex.toString());
            } finally {
                try {
                    this.conn.shutdown();
                } catch (IOException ignore) {
                }
            }
        }
    }
}