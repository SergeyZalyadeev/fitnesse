// Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
// Released under the terms of the CPL Common Public License version 1.0.
package fitnesse.authentication;

import junit.framework.TestCase;
import fitnesse.FitNesseContext;
import fitnesse.Responder;
import fitnesse.http.MockRequest;
import fitnesse.http.Request;
import fitnesse.http.Response;
import fitnesse.testutil.FitNesseUtil;
import fitnesse.testutil.SimpleAuthenticator;
import fitnesse.wiki.InMemoryPage;
import fitnesse.wiki.PageData;
import fitnesse.wiki.WikiPage;

public class AuthenticatorTest extends TestCase {
  SimpleAuthenticator authenticator;
  private WikiPage root;
  private MockRequest request;
  private Responder responder;
  private Class<? extends Responder> responderType;
  private DummySecureResponder privilegedResponder;
  private FitNesseContext context;

  class DummySecureResponder implements SecureResponder {

    public SecureOperation getSecureOperation() {
      return new AlwaysSecureOperation();
    }

    public Response makeResponse(FitNesseContext context, Request request) {
      return null;
    }

    protected void refactorReferences(FitNesseContext context, WikiPage pageToBeMoved, String newParentName) {
    }
  }
  

  public void setUp() {
    root = InMemoryPage.makeRoot("RooT");
    WikiPage frontpage = root.addChildPage("FrontPage");
    makeReadSecure(frontpage);
    authenticator = new SimpleAuthenticator();
    privilegedResponder = new DummySecureResponder();

    request = new MockRequest();
    request.setResource("FrontPage");
    context = FitNesseUtil.makeTestContext(root);
  }

  private void makeReadSecure(WikiPage frontpage) {
    PageData data = frontpage.getData();
    data.setAttribute(PageData.PropertySECURE_READ);
    frontpage.commit(data);
  }

  public void tearDown() {
  }

  public void testNotAuthenticated() {
    makeResponder();
    assertEquals(UnauthorizedResponder.class, responderType);
  }

  public void testAuthenticated() {
    authenticator.authenticated = true;
    makeResponder();
    assertEquals(DummySecureResponder.class, responderType);
  }

  private void makeResponder() {
    responder = authenticator.authenticate(context, request, privilegedResponder);
    responderType = responder.getClass();
  }
}
