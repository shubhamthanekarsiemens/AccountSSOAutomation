// This file was generated by Mendix Studio Pro.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package siemensdescommons.actions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.ISession;
import com.mendix.webui.CustomJavaAction;
import com.siemens.plm.webapp.strategies.IConfigStrategy;
import com.siemens.plm.webapp.util.SamAuthSessionHelper;
import siemenssamauthssomodule.actions.custom.MendixStartupConfig;
import siemenssamauthssomodule.proxies.SessionData;
import siemenssamauthssomodule.proxies.microflows.Microflows;
import system.proxies.Session;

public class CheckOrRenewSession extends CustomJavaAction<java.lang.Boolean>
{
	public CheckOrRenewSession(IContext context)
	{
		super(context);
	}

	@java.lang.Override
	public java.lang.Boolean executeAction() throws Exception
	{
		// BEGIN USER CODE
                try {
                        final IConfigStrategy configStrategy = MendixStartupConfig.getInstance().getConfigStrategy();
                        final SessionData sessionData = Microflows.mF_GetSessionData(getContext());

                        ObjectMapper mapper = new ObjectMapper();
                        ObjectNode jsonData = mapper.createObjectNode();

                        jsonData.put("accessKeyExpiration", sessionData.getaccessKeyExpiration());
                        jsonData.put("id_token", sessionData.getidToken());
                        jsonData.put("refresh_token", sessionData.getrefreshToken());
                        JsonNode output = SamAuthSessionHelper.checkOrRenewSession(configStrategy, jsonData);
                        ISession session = context().getSession();
                        Session sessionProxy = Session.load(context(), session.getMendixObject().getId());

                        if (output.get("isLogout") != null && !(output.get("isLogout").asBoolean())) {
                                JsonNode accessKey = output.get("accessKey");
                                sessionData.setaccessKeyId(context(), accessKey.get("objectGUID").asText());
                                sessionData.setsecretAccessKey(context(), accessKey.get("secretAccessKey").asText());
                                sessionData.setidToken(context(), output.get("id_token").asText());
                                sessionData.setrefreshToken(context(), output.get("refresh_token").asText());
                                sessionData.setaccessKeyExpiration(context(),
                                                output.get("accessKeyExpiration").asText());
                                sessionData.setaccessToken(context(), output.get("access_token").asText());
                                sessionData.setSessionData_Session(context(), sessionProxy);
                                sessionData.commit(context());
                                return false;
                        } else if (output.get("isLogout") != null && (output.get("isLogout").asBoolean())) {
                                return true;
                        }
                } catch (Exception ex) {
                        System.out.println(ex);
                }
                return false;
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 * @return a string representation of this action
	 */
	@java.lang.Override
	public java.lang.String toString()
	{
		return "CheckOrRenewSession";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}