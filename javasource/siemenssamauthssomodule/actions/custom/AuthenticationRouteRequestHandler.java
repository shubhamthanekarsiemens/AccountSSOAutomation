// @<COPYRIGHT>@
// ==================================================
// Copyright 2019.
// Siemens Product Lifecycle Management Software Inc.
// All Rights Reserved.
// ==================================================
// @<COPYRIGHT>@

package siemenssamauthssomodule.actions.custom;

import javax.servlet.http.HttpServletRequest;

import com.mendix.externalinterface.connector.RequestHandler;
import com.mendix.m2ee.api.IMxRuntimeRequest;
import com.mendix.m2ee.api.IMxRuntimeResponse;
import com.mendix.systemwideinterfaces.core.IContext;
import com.siemens.plm.webapp.WebappEndpoint;
import com.siemens.plm.webapp.config.BaseConfigParams;
import com.siemens.plm.webapp.handler.EnsureAuthenticatedHandler;

/**
 * The request handler to handle routes that require authentication.
 */
public class AuthenticationRouteRequestHandler extends RequestHandler {
    /**
     * Handler instance.
     */
    private final EnsureAuthenticatedHandler handler;

    /**
     * The WebappEndpoint object.
     */
    private final WebappEndpoint webappEndpoint;

    /**
     * Constructor.
     * 
     * @param webappEndpoint The webapp endpoint instance.
     * @param context        The Mendix context.
     */
    public AuthenticationRouteRequestHandler(final WebappEndpoint webappEndpoint, final IContext context) {
        this.webappEndpoint = webappEndpoint;
        final BaseConfigParams configParams = webappEndpoint.getConfigParams();
        final boolean skipSAMAuth = Boolean.parseBoolean((String) configParams.getValue("skipSamAuth"));
        if (!skipSAMAuth) {
            handler = new EnsureAuthenticatedHandler(webappEndpoint.getConfigParams(), webappEndpoint.getStartupConfig(),
                    webappEndpoint.getRequestScopeContext());
        } else {
            handler = null;
        }
    }

    @Override
    protected void processRequest(final IMxRuntimeRequest mxRequest, final IMxRuntimeResponse mxResponse, final String path)
            throws Exception {
        final BaseConfigParams configParams = webappEndpoint.getConfigParams();
        final HttpServletRequest httpRequest = mxRequest.getHttpServletRequest();
        httpRequest.getSession().setAttribute("queryParameters", httpRequest.getParameterMap());
        final boolean skipSAMAuth = Boolean.parseBoolean((String) configParams.getValue("skipSamAuth"));
        if (!skipSAMAuth) {
            handler.invoke(httpRequest, mxResponse.getHttpServletResponse(), null);
        } else {
            LoginHelper.redirect(mxResponse, "/index2.html");
        }
    }
}
