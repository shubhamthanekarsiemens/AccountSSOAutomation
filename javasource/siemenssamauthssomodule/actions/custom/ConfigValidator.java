// @<COPYRIGHT>@
// ==================================================
// Copyright 2019.
// Siemens Product Lifecycle Management Software Inc.
// All Rights Reserved.
// ==================================================
// @<COPYRIGHT>@

package siemenssamauthssomodule.actions.custom;

import java.util.ArrayList;
import java.util.List;

import com.siemens.plm.webapp.config.BaseConfigParams;
import com.siemens.plm.webapp.config.IConfigValidator;
import com.siemens.plm.webapp.exceptions.ParameterMissingException;

/**
 * The configuration validator.
 */
public class ConfigValidator implements IConfigValidator {
    @Override
    public void validate(final BaseConfigParams configParams) throws ParameterMissingException {
        final List<String> invalidParams = new ArrayList<String>();

        // validate app configurations
        validateConfigParam(configParams, "LOGOUT_HANDLER_ENDPOINT", invalidParams);
        validateConfigParam(configParams, "POST_LOGOUT_URL", invalidParams);

        // throw exception for invalid configuration parameters
        if (!invalidParams.isEmpty()) {
            throw new ParameterMissingException("The following configuration parameters are missing: " + String.join(",", invalidParams));
        }
    }

    /**
     * Validate if the value for the specified configuration parameter is null or empty.
     * 
     * @param configParams  The configuration parameters.
     * @param configName    The name of the configuration parameter to validate.
     * @param invalidParams The list of invalid configuration parameters to update.
     */
    private void validateConfigParam(final BaseConfigParams configParams, final String configName, final List<String> invalidParams) {
        Object configValue = configParams.getValue(configName);
        if (configValue == null || (configValue instanceof String && ((String) configValue).isEmpty())) {
            invalidParams.add(configName);
        }
    }
}
