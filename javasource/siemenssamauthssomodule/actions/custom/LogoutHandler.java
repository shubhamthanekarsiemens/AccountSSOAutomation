// @<COPYRIGHT>@
// ==================================================
// Copyright 2019.
// Siemens Product Lifecycle Management Software Inc.
// All Rights Reserved.
// ==================================================
// @<COPYRIGHT>@

package siemenssamauthssomodule.actions.custom;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mendix.core.Core;
import com.mendix.externalinterface.connector.RequestHandler;
import com.mendix.m2ee.api.IMxRuntimeRequest;
import com.mendix.m2ee.api.IMxRuntimeResponse;
import com.mendix.systemwideinterfaces.core.ISession;
import com.siemens.plm.webapp.WebappEndpoint;
import com.siemens.plm.webapp.config.BaseConfigParams;

public class LogoutHandler extends RequestHandler {
    /**
     * Handler instance.
     */
    private final com.siemens.plm.webapp.handler.LogoutHandler handler;

    /**
     * The WebappEndpoint object.
     */
    private final WebappEndpoint webappEndpoint;

    /**
     * Constructor.
     * 
     * @param webappEndpoint The webapp endpoint instance.
     */
    public LogoutHandler(final WebappEndpoint webappEndpoint) {
        this.webappEndpoint = webappEndpoint;
        handler = new com.siemens.plm.webapp.handler.LogoutHandler(webappEndpoint.getConfigParams());
    }

    @Override
    protected void processRequest(IMxRuntimeRequest request, IMxRuntimeResponse response, String path) throws Exception {
        final HttpServletRequest httpRequest = request.getHttpServletRequest();
        final HttpServletResponse httpResponse = response.getHttpServletResponse();

        final ISession session = getSessionFromRequest(request);
        session.destroy();
        Core.logout(session);

        final Cookie[] cookies = httpRequest.getCookies();
        for (Cookie cookie : cookies) {
            cookie.setMaxAge(0);
            cookie.setValue(null);
            cookie.setPath("/");
            httpResponse.addCookie(cookie);
        }

        final Object userInfo = httpRequest.getSession().getAttribute("userInfo");
        if (userInfo != null) {
            handler.invoke(httpRequest, httpResponse, null);
        } else {
            final BaseConfigParams configParams = this.webappEndpoint.getConfigParams();
            final String postLogoutUrl = configParams.getValue("POST_LOGOUT_URL").toString();
            httpResponse.setHeader("Location", postLogoutUrl);
            httpResponse.setStatus(302);
            httpRequest.getSession().invalidate();
        }
    }
}
