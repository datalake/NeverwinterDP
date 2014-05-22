package com.neverwinterdp.sparkngin.http;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.neverwinterdp.message.Message;
import com.neverwinterdp.netty.http.HttpServer;
import com.neverwinterdp.netty.http.client.DumpResponseHandler;
import com.neverwinterdp.netty.http.client.HttpClient;
import com.neverwinterdp.testframework.event.SampleEvent;

public class SparknginHttpUnitTest {
  static {
    System.setProperty("log4j.configuration", "file:src/main/resources/log4j.properties") ;
  }
  
  private HttpServer server ;
  
  @Before
  public void setup() throws Exception {
    server = new HttpServer();
    server.add("/message", new MessageRouteHandler());
    new Thread() {
      public void run() {
        try {
          server.start() ;
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }.start() ;
    Thread.sleep(1000);
  }
  
  @After
  public void teardown() {
    server.shutdown() ;
  }
  
  @Test
  public void testSendMessage() throws Exception {
    DumpResponseHandler handler = new DumpResponseHandler() ;
    HttpClient client = new HttpClient ("127.0.0.1", 8080, handler) ;
    for(int i = 0; i < 10; i++) {
      SampleEvent event = new SampleEvent("event-" + i, "event " + i) ;
      Message message = new Message("m" + i, event, true) ;
      client.post("/message", message);
    }
    Thread.sleep(1000);
    assertEquals(10, handler.getCount()) ;
  }
}
