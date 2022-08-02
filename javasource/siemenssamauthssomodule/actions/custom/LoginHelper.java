// @<COPYRIGHT>@
// ==================================================
// Copyright 2019.
// Siemens Product Lifecycle Management Software Inc.
// All Rights Reserved.
// ==================================================
// @<COPYRIGHT>@

package siemenssamauthssomodule.actions.custom;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.JsonNode;
import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.m2ee.api.IMxRuntimeRequest;
import com.mendix.m2ee.api.IMxRuntimeResponse;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.ISession;
import com.mendix.systemwideinterfaces.core.IUser;

import siemenssamauthssomodule.proxies.SessionData;
import system.proxies.Session;

/**
 * Helper class, which is used to create a Mendix Session; based on XAS 2.4 com.mendix.core.action.{client/user}.LoginAction
 */
class LoginHelper {
    /** based on xas2.5 */
    private static final String XAS_SESSION_ID = "XASSESSIONID";
    private static final String XAS_ID = "XASID";

    protected static void createSession(IMxRuntimeRequest request, IMxRuntimeResponse response, IContext context, IUser user,
            JsonNode sessionUser) {
        ISession session = context.getSession();
        try {
            session = Core.initializeSession(user, session != null ? session.getId().toString() : null);

            // cache session data
            final JsonNode accessKey = (JsonNode) sessionUser.get("accessKey");

            SessionData sessionData;
            try {
                Session sessionProxy = Session.load(context, session.getMendixObject().getId());
                sessionData = new SessionData(context);
                sessionData.setgivenName(context, sessionUser.get("given_name").asText());
                sessionData.setfamilyName(context, sessionUser.get("family_name").asText());
                sessionData.setaccount(context, sessionUser.get("account").asText());
                sessionData.setemail(context, sessionUser.get("email").asText());
                sessionData.setidToken(context, sessionUser.get("id_token").asText());
                sessionData.setrefreshToken(context, sessionUser.get("refresh_token").asText());
                sessionData.setaccessToken(context, sessionUser.get("access_token").asText());
                sessionData.setuserId(context, sessionUser.get("userid").asText());
                
                if (accessKey != null)
                {
                    sessionData.setaccessKeyId(context, accessKey.get("objectGUID").asText());
                    sessionData.setsecretAccessKey(context, accessKey.get("secretAccessKey").asText());
                    sessionData.setaccessKeyExpiration(context, sessionUser.get("accessKeyExpiration").asText());
                }
                
                HttpSession httpSession = request.getHttpServletRequest().getSession();
                sessionData.setsessionId(httpSession.getId());
                @SuppressWarnings (value="unchecked")
                Map<String, String[]> queryParameters = (Map<String, String[]>) httpSession.getAttribute("queryParameters");
                if (queryParameters != null && queryParameters.size() != 0)
                {
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String,String[]> entry : queryParameters.entrySet())
                    {
                        if (sb.length() != 0)
                        {
                            sb.append('&');
                        }
                        sb.append(entry.getKey());
                        sb.append('=');
                        sb.append(String.join(",", entry.getValue()));
                    }
                    sessionData.setqueryParameters(context, sb.toString());
                }

                sessionData.setSessionData_Session(context, sessionProxy);
                sessionData.commit(context);
            } catch (CoreException ex) {
                Core.getLogger("LoginHelper").warn("Error while commiting data to session" + ex);
            }

        } catch (CoreException ex) {
            Core.getLogger("LoginHelper").error("Failed to initialize new Mendix session " + ex);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new RuntimeException("Single Sign On unable to create new session");
        }

        response.addCookie(XAS_SESSION_ID, session.getId().toString(), "/", "", -1);
        response.addCookie(XAS_ID, "0." + Core.getXASId(), "/", "", -1);

        String indexPage = "/index3.html";
        redirect(response, indexPage);
    }

    /**
     * Sends a redirect (the redirect method provided by the class is less reliable).
     * 
     * @param response
     * @param path
     */
    protected static void redirect(IMxRuntimeResponse response, String path) {
        HttpServletResponse httpResponse = response.getHttpServletResponse();
        Core.getLogger("LoginHelper").debug("Redirecting to location: " + path);
        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
        httpResponse.setHeader("location", path);
    }
}