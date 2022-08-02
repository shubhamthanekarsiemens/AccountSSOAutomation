// @<COPYRIGHT>@
// ==================================================
// Copyright 2019.
// Siemens Product Lifecycle Management Software Inc.
// All Rights Reserved.
// ==================================================
// @<COPYRIGHT>@

package siemenssamauthssomodule.actions.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mendix.core.Core;
import com.mendix.core.CoreRuntimeException;
import com.mendix.externalinterface.connector.RequestHandler;
import com.mendix.m2ee.api.IMxRuntimeRequest;
import com.mendix.m2ee.api.IMxRuntimeResponse;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IDataType;
import com.mendix.systemwideinterfaces.core.IMendixIdentifier;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.systemwideinterfaces.core.IUser;
import com.siemens.plm.webapp.WebappEndpoint;
import com.siemens.plm.webapp.config.BaseConfigParams;
import com.siemens.plm.webapp.handler.CallbackHandler;

import system.proxies.User;
import system.proxies.UserRole;

/**
 * The request handler to handle the callback route.
 */
public class CallbackRouteRequestHandler extends RequestHandler {
    public static final java.lang.String entityName = "System.User";
    /**
     * Handler instance.
     */
    private final CallbackHandler handler;

    /**
     * The WebappEndpoint object.
     */
    private final WebappEndpoint webappEndpoint;

    /**
     * Default password.
     */
    private final String defaultPassword = "Pass@word1";

    /**
     * Action name to resolve user.
     */
    private String actionNameToResolveUser = "SiemensSAMAuthSSOModule.OEn_ResolveUserByEmail";

    /**
     * Constructor.
     * 
     * @param webappEndpoint The webapp endpoint instance.
     */
    public CallbackRouteRequestHandler(final WebappEndpoint webappEndpoint) {
        this.webappEndpoint = webappEndpoint;
        handler = new CallbackHandler(webappEndpoint.getConfigParams());
    }

    @Override
    protected void processRequest(final IMxRuntimeRequest mxRequest, final IMxRuntimeResponse mxResponse, final String path)
            throws Exception {
        try {
            handler.invoke(mxRequest.getHttpServletRequest(), mxResponse.getHttpServletResponse(), null);
            final String userInfoString = (String) mxRequest.getHttpServletRequest().getSession().getAttribute("userInfo");
            final ObjectMapper mapper = new ObjectMapper();
            final JsonNode sessionUser = userInfoString != null ? mapper.readTree(userInfoString) : null;

            if (sessionUser == null) {
                return;
            }

            final String email = sessionUser.get("email").textValue();

            User user = null;
            IUser iUser = null;
            IMendixObject mxUser = null;

            IContext context = Core.createSystemContext();
            user = resolveUser(context, email);

            if (user == null) {
                mxUser = Core.instantiate(context, entityName);
                List<UserRole> roleList = UserRole.load(context, "");

                mxUser.setValue(context, User.MemberNames.Name.toString(), email);
                mxUser.setValue(context, User.MemberNames.Password.toString(), this.getPassword());
                List<IMendixIdentifier> userroles = new ArrayList<IMendixIdentifier>();
                for (UserRole role : roleList) {
                    if (role.getName().equals("User")) {
                        userroles.add(role.getMendixObject().getId());
                        break;
                    }
                }
                mxUser.setValue(context, User.MemberNames.UserRoles.toString(), userroles);
                Core.commit(context, mxUser);
                String userName = mxUser.getValue(context, User.MemberNames.Name.toString());
                iUser = Core.getUser(context, userName);
            } else {
                iUser = Core.getUser(context, user.getName());
            }

            LoginHelper.createSession(mxRequest, mxResponse, context, iUser, sessionUser);

        } catch (Exception ex) {

            Core.getLogger("CallbackRouteRequestHandler").error("Exception occurred while processing request " + ex);
        }
    }

    /**
     * Method to call a microflow that will resolve the user based on the mfInput
     * parameter
     */
    private User resolveUser(final IContext context, final String mfInput) {
        try {

            Map<String, Object> params = new HashMap<String, Object>();
            String actionName = actionNameToResolveUser;
            Map<String, IDataType> inputParameters = Core.getInputParameters(actionName);
            for (String name : inputParameters.keySet()) {
                params.put(name, mfInput);
                break;
            }
            Core.getLogger("CallbackRouteRequestHandler").debug("Calling action " + actionName + " with params: " + params);
            Object result = Core.microflowCall(actionName).withParams(params).execute(context);
            /*
             * If user not found then return null; Else, return the user object
             */
            if (result == null) {
                return null;
            } else {
                User user = User.initialize(context, (IMendixObject) result);
                Core.getLogger("CallbackRouteRequestHandler").debug("Resolved user " + user.getName());
                return user;
            }
        } catch (Exception ex) {
            Core.getLogger("CallbackRouteRequestHandler").error("Exception occurred while resolving user " + ex);
            throw new CoreRuntimeException("Failed to resolve user");
        }
    }

    /**
     * Get the password for the Mendix user.
     * 
     * @return The password for the Mendix user.
     */
    private String getPassword() {
        final BaseConfigParams configParams = webappEndpoint.getConfigParams();
        String password = (String) configParams.getValue("defaultMendixPassword");
        password = (password.isEmpty()) ? defaultPassword : password;

        return password;
    }
}