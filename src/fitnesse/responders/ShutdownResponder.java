// Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
// Released under the terms of the CPL Common Public License version 1.0.
package fitnesse.responders;

import fitnesse.FitNesse;
import fitnesse.FitNesseContext;
import fitnesse.authentication.AlwaysSecureOperation;
import fitnesse.authentication.SecureOperation;
import fitnesse.authentication.SecureResponder;
import fitnesse.http.Request;
import fitnesse.http.Response;
import fitnesse.http.SimpleResponse;
import fitnesse.html.template.HtmlPage;
import fitnesse.html.template.PageTitle;

public class ShutdownResponder implements SecureResponder {
  public Response makeResponse(final FitNesseContext context, Request request) {
    SimpleResponse response = new SimpleResponse();

    HtmlPage html = context.pageFactory.newPage();
    html.setTitle("Shutdown");
    html.setPageTitle(new PageTitle("Shutdown"));

    html.setMainTemplate("shutdownPage.vm");
    response.setContent(html.html());

    Thread shutdownThread = new Thread() {
      public void run() {
        try {
          context.fitNesse.stop();
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    shutdownThread.start();

    return response;
  }

  public SecureOperation getSecureOperation() {
    return new AlwaysSecureOperation();
  }
}
