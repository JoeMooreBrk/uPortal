/**
 * Copyright (c) 2001 The JA-SIG Collaborative.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by the JA-SIG Collaborative
 *    (http://www.jasig.org/)."
 *
 * THIS SOFTWARE IS PROVIDED BY THE JA-SIG COLLABORATIVE "AS IS" AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE JA-SIG COLLABORATIVE OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package  org.jasig.portal;

import  javax.servlet.*;
import  javax.servlet.jsp.*;
import  javax.servlet.http.*;
import  java.io.*;
import  java.util.*;
import  java.text.*;
import  java.sql.*;
import  com.objectspace.xml.*;
import  org.jasig.portal.layout.*;
import  org.jasig.portal.security.*;


/**
 * @author Ken Weiner
 * @modified by Vikrant Joshi: June 26  2001.
 */
public class AuthenticationBean extends GenericPortalBean
    implements IAuthenticationBean {
  protected org.jasig.portal.security.IPerson m_Person = null;
  protected String authenticationFailure = null;

  /**
   * Authenticate a user.
   * @param sUserName User name
   * @param sPassword User password
   * @return true if successful, otherwise false.
   */
  public boolean authenticate (String sUserName, String sPassword) {
    return  authenticate(sUserName, sPassword, "root");
  }

  /**
   * Authenticate a user.
   * @param sUserName User name
   * @param sPassword User password
   * @param contextType  String to be passed to the InitialSecurityContext when initialising it 
   * @return true if successful, otherwise false.
   */
  public boolean authenticate (String sUserName, String sPassword, String contextType) {
    // Don't bother if SessionManager says there's not enough resources
    if (!SessionManager.allowLogins()) {
      return  (false);
    }
    // Instantiate the InitialSecurityContext
    SecurityContext ic = null;
    try {
      ic = new InitialSecurityContext(contextType);
    } catch (Exception e) {
      Logger.log(Logger.ERROR, "AuthenticationBean.authenticate(): Could not create InitialSecurityContext of type " + contextType
          + " " + e.getMessage());
      authenticationFailure = "Could not create InitialSecurityContext of type " + contextType;
      return  (false);
    }
    // Make sure the InitialSecurityContext was initialized
    if (ic == null) {
      Logger.log(Logger.ERROR, "AuthenticationBean.authenticate(): Could not create InitialSecurityContext of type " + contextType);
      authenticationFailure = "Could not create InitialSecurityContext of type " + contextType;
      return  (false);
    }
    Principal me;
    OpaqueCredentials op;
    // Get an instance of the principal from the security context
    me = ic.getPrincipalInstance();
    if (me == null) {
      Logger.log(Logger.ERROR, "AuthenticationBean.authenticate(): Could not retrieve Principal instance from security context");
      authenticationFailure = "Could not retrieve Principal instance from security context";
      return  (false);
    }
    // Get an instance of the credentials from the security context
    op = ic.getOpaqueCredentialsInstance();
    if (op == null) {
      Logger.log(Logger.ERROR, "AuthenticationBean.authenticate(): Could not retrieve OpaqueCredentials instance from security context");
      authenticationFailure = "Could not retrieve OpaqueCredentials instance from security context";
      return  (false);
    }
    // Set the user name in the principal
    me.setUID(sUserName);
    // Set the password in the credentials
    op.setCredentials(sPassword);
    try {
      // Attempt to authenticate the user
      ic.authenticate();
    } catch (Exception e) {
      Logger.log(Logger.ERROR, "AuthenticationBean.authenticate(): Security context thre exception while attempting to authenticate "
          + e.getMessage());
      authenticationFailure = "Security context thre exception while attempting to authenticate " + e.getMessage();
      return  (false);
    }
    // Check to see if the user passed authentication
    boolean bAuthenticated = ic.isAuthenticated();
    if (bAuthenticated) {
      // Get the additional descriptor from the security context
      AdditionalDescriptor addInfo = ic.getAdditionalDescriptor();
      // Create an IPerson object if one was not passed from the security context
      if (addInfo == null || !(addInfo instanceof PersonImpl)) {
        m_Person = new PersonImpl();
        m_Person.setID(sUserName);
        m_Person.setFullName(me.getFullName());
        m_Person.setAttribute("globalUID", me.getGlobalUID());
      } 
      else {
        m_Person = (IPerson)addInfo;
      }
    } 
    else {
      authenticationFailure = ic.getAuthenticationFailure();
    }
    return  (bAuthenticated);
  }

  /**
   * Returns a Person object that can be used to hold site-specific attributes
   * about the logged on user.  This information is established during
   * authentication.
   * @return An object that implements the
   * <code>org.jasig.portal.security.Person</code> interface.
   */
  public IPerson getPerson () {
    return  m_Person;
  }

  /**
   * Returns the reason for AuthenticationFailure (such as "Incorrect UserName/Password"
   * or "Authentication Service Unavailable").
   */
  public String getAuthenticationFailure () {
    return  (authenticationFailure);
  }
}



