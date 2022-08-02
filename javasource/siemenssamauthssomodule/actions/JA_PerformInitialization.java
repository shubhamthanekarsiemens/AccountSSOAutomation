// This file was generated by Mendix Studio Pro.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package siemenssamauthssomodule.actions;

import com.mendix.core.Core;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.webui.CustomJavaAction;
import com.siemens.plm.webapp.WebappEndpoint;
import siemenssamauthssomodule.actions.custom.AuthenticationRouteRequestHandler;
import siemenssamauthssomodule.actions.custom.CallbackRouteRequestHandler;
import siemenssamauthssomodule.actions.custom.LogoutHandler;
import siemenssamauthssomodule.actions.custom.MendixStartupConfig;

public class JA_PerformInitialization extends CustomJavaAction<java.lang.Boolean>
{
	private IMendixObject __startupConfig;
	private siemenssamauthssomodule.proxies.StartupConfig startupConfig;
	private java.lang.String authenticationRoutePath;
	private java.lang.String callbackRoutePath;
	private java.lang.String logoutRoutePath;

	public JA_PerformInitialization(IContext context, IMendixObject startupConfig, java.lang.String authenticationRoutePath, java.lang.String callbackRoutePath, java.lang.String logoutRoutePath)
	{
		super(context);
		this.__startupConfig = startupConfig;
		this.authenticationRoutePath = authenticationRoutePath;
		this.callbackRoutePath = callbackRoutePath;
		this.logoutRoutePath = logoutRoutePath;
	}

	@java.lang.Override
	public java.lang.Boolean executeAction() throws Exception
	{
		this.startupConfig = this.__startupConfig == null ? null : siemenssamauthssomodule.proxies.StartupConfig.initialize(getContext(), __startupConfig);

		// BEGIN USER CODE

        // initialize webapp endpoint
        final MendixStartupConfig mendixStartupConfig = MendixStartupConfig.getInstance();
        mendixStartupConfig.initialize(getContext(), startupConfig);
        final WebappEndpoint webappEndpoint = new WebappEndpoint(mendixStartupConfig);
        webappEndpoint.initialize();

        // add route handlers
        Core.addRequestHandler(authenticationRoutePath, new AuthenticationRouteRequestHandler(webappEndpoint, getContext()));
        Core.addRequestHandler(callbackRoutePath, new CallbackRouteRequestHandler(webappEndpoint));
        Core.addRequestHandler(logoutRoutePath, new LogoutHandler(webappEndpoint));

        return Boolean.TRUE;

		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 * @return a string representation of this action
	 */
	@java.lang.Override
	public java.lang.String toString()
	{
		return "JA_PerformInitialization";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}
