// This file was generated by Mendix Studio Pro.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package siemenssamauthssomodule.actions;

import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import com.siemens.plm.webapp.strategies.IConfigStrategy;
import siemenssamauthssomodule.actions.custom.MendixStartupConfig;

public class JA_GetConfigParamValue extends CustomJavaAction<java.lang.String>
{
	private java.lang.String name;
	private java.lang.Boolean required;

	public JA_GetConfigParamValue(IContext context, java.lang.String name, java.lang.Boolean required)
	{
		super(context);
		this.name = name;
		this.required = required;
	}

	@java.lang.Override
	public java.lang.String executeAction() throws Exception
	{
		// BEGIN USER CODE

        if (name == null || name.isEmpty()) {
            return "";
        }

        // get mendix configuration stratgey
        final IConfigStrategy configStrategy = MendixStartupConfig.getInstance().getConfigStrategy();
        final String value = (String) configStrategy.getConfig(name);
        return value;

		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 * @return a string representation of this action
	 */
	@java.lang.Override
	public java.lang.String toString()
	{
		return "JA_GetConfigParamValue";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}