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

import com.mendix.core.Core;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.siemens.plm.webapp.config.BaseStartupConfig;
import com.siemens.plm.webapp.strategies.IConfigStrategy;

import siemenssamauthssomodule.proxies.ConfigParam;
import siemenssamauthssomodule.proxies.PublicRoute;
import siemenssamauthssomodule.proxies.Scope;
import siemenssamauthssomodule.proxies.StartupConfig;
import siemenssamauthssomodule.proxies.StaticPath;

/**
 * The class to provide startup configurations.
 */
public class MendixStartupConfig extends BaseStartupConfig {
    /**
     * The instance.
     */
    private static MendixStartupConfig instance = new MendixStartupConfig();

    /**
     * The map to hold configuration parameters.
     */
    private Map<String, Object> configParamsMap;

    /**
     * Get the instance.
     * 
     * @return The instance.
     */
    public static MendixStartupConfig getInstance() {
        return instance;
    }

    /**
     * Perform initialization.
     *
     * @param context       The Mendix context.
     * @param startupConfig The startup configuration.
     * @throws Exception If there are errors when loading objects from the Mendix database.
     */
    public synchronized void initialize(final IContext context, final StartupConfig startupConfig) throws Exception {
        // if startup configuration is null, load it from the from database
        final StartupConfig mxStartupConfig;
        if (startupConfig == null) {
            final List<StartupConfig> startupConfigList = StartupConfig.load(context, "");
            if (startupConfigList != null && !startupConfigList.isEmpty()) {
                mxStartupConfig = startupConfigList.get(0);
            } else {
                throw new Exception("No startup config object found in database.");
            }
        } else {
            mxStartupConfig = startupConfig;
        }

        // populate various configurations
        populateStartupConfigParams(context, mxStartupConfig);
        populateAppConfigParams(context, mxStartupConfig);
        populatePublicRoutes(context, mxStartupConfig);
        populateStaticPaths(context, mxStartupConfig);
        populateScopes(context, mxStartupConfig);
    }

    @Override
    public IConfigStrategy getConfigStrategy() {
        return new MendixConfigStrategy(configParamsMap);
    }

    @Override
    public String getConfigSetName() {
        return "SiemensSAMAuthSSOModule";
    }

    @Override
    public String[] getConfigNames() {
        return configParamsMap.keySet().toArray(new String[0]);
    }

    @Override
    public String[] getPublicRoutes() {
        @SuppressWarnings("unchecked")
        List<String> publicRoutes = (List<String>) configParamsMap.get("publicRoutes");
        return (publicRoutes == null) ? null : publicRoutes.toArray(new String[0]);
    }

    @Override
    public String[] getStaticPaths() {
        @SuppressWarnings("unchecked")
        List<String> staticPaths = (List<String>) configParamsMap.get("staticPaths");
        return (staticPaths == null) ? null : staticPaths.toArray(new String[0]);
    }

    @Override
    public String[] getScopes() {
        @SuppressWarnings("unchecked")
        List<String> scopes = (List<String>) configParamsMap.get("scopes");
        return (scopes == null) ? null : scopes.toArray(new String[0]);
    }

    /**
     * Constructor.
     */
    private MendixStartupConfig() {
        configParamsMap = new HashMap<String, Object>();
    }

    /**
     * Populate default configuration parameters from 'StartupConfig' entity.
     *
     * @param context       The Mendix context.
     * @param startupConfig The startup configuration.
     * @throws Exception If there are errors when loading objects from the Mendix database.
     */
    private void populateStartupConfigParams(final IContext context, final StartupConfig startupConfig) throws Exception {
        configParamsMap.put("clientId", getTrimmedValue(startupConfig.getclientId()));
        configParamsMap.put("CLIENT_ID", getTrimmedValue(startupConfig.getclientId()));
        configParamsMap.put("clientSecret", getTrimmedValue(startupConfig.getclientSecret()));
        configParamsMap.put("CLIENT_SECRET", getTrimmedValue(startupConfig.getclientSecret()));
        configParamsMap.put("_devAccessKeyId", getTrimmedValue(startupConfig.get_devAccessKeyId()));
        configParamsMap.put("_devSecretAccessKey", getTrimmedValue(startupConfig.get_devSecretAccessKey()));
        configParamsMap.put("authHandlerEndpoint", getTrimmedValue(startupConfig.getauthHandlerEndpoint()));
        configParamsMap.put("AUTH_HANDLER_ENDPOINT", getTrimmedValue(startupConfig.getauthHandlerEndpoint()));
        configParamsMap.put("callbackUrl", getTrimmedValue(startupConfig.getcallbackUrl()));
        configParamsMap.put("CALLBACK_URL", getTrimmedValue(startupConfig.getcallbackUrl()));
        configParamsMap.put("postLogoutUrl", getTrimmedValue(startupConfig.getpostLogoutUrl()));
        configParamsMap.put("POST_LOGOUT_URL", getTrimmedValue(startupConfig.getpostLogoutUrl()));
        configParamsMap.put("defaultMendixPassword", getTrimmedValue(startupConfig.getdefaultMendixPassword()));
        configParamsMap.put("skipSamAuth", getTrimmedValue(startupConfig.getskipSamAuth()));
        configParamsMap.put("tokenRevocationRequest", getTrimmedValue(startupConfig.gettokenRevocationRequest()));
        configParamsMap.put("TOKEN_REVOCATION_REQUEST", getTrimmedValue(startupConfig.gettokenRevocationRequest()));
        configParamsMap.put("logoutHandlerEndpoint", getTrimmedValue(startupConfig.getlogoutHandlerEndpoint()));
        configParamsMap.put("LOGOUT_HANDLER_ENDPOINT", getTrimmedValue(startupConfig.getlogoutHandlerEndpoint()));
        configParamsMap.put("APP_LOGOUT_URL", getTrimmedValue(startupConfig.getappLogoutUrl()));
        configParamsMap.put("appLogoutUrl", getTrimmedValue(startupConfig.getappLogoutUrl()));
    }

    /**
     * Populate app configuration parameters from 'ConfigParam' entity.
     *
     * @param context       The Mendix context.
     * @param startupConfig The startup configuration.
     * @throws Exception If there are errors when loading objects from the Mendix database.
     */
    private void populateAppConfigParams(final IContext context, final StartupConfig startupConfig) throws Exception {
        final List<IMendixObject> mxConfigParams = Core.retrieveByPath(context, startupConfig.getMendixObject(),
                ConfigParam.MemberNames.ConfigParam_StartupConfig.toString());
        if (mxConfigParams != null && !mxConfigParams.isEmpty()) {
            for (final IMendixObject mxConfigParam : mxConfigParams) {
                final String name = getTrimmedValue(mxConfigParam.getValue(context, ConfigParam.MemberNames.name.toString()));
                final String value = getTrimmedValue(mxConfigParam.getValue(context, ConfigParam.MemberNames.value.toString()));
                configParamsMap.put(name, value);
            }
        }
    }

    /**
     * Populate public routes from 'PublicRoute' entity.
     *
     * @param context       The Mendix context.
     * @param startupConfig The startup configuration.
     * @throws Exception If there are errors when loading objects from the Mendix database.
     */
    private void populatePublicRoutes(final IContext context, final StartupConfig startupConfig) throws Exception {
        final List<IMendixObject> mxPublicRoutes = Core.retrieveByPath(context, startupConfig.getMendixObject(),
                PublicRoute.MemberNames.PublicRoute_StartupConfig.toString());
        if (mxPublicRoutes != null && !mxPublicRoutes.isEmpty()) {
            final List<String> values = new ArrayList<String>();
            for (final IMendixObject mxPublicRoute : mxPublicRoutes) {
                final String value = getTrimmedValue(mxPublicRoute.getValue(context, PublicRoute.MemberNames.route.toString()));
                if (!value.isEmpty()) {
                    values.add(value);
                }
            }
            configParamsMap.put("publicRoutes", values);
        }
    }

    /**
     * Populate static paths from 'StaticPath' entity.
     *
     * @param context       The Mendix context.
     * @param startupConfig The startup configuration.
     * @throws Exception If there are errors when loading objects from the Mendix database.
     */
    private void populateStaticPaths(final IContext context, final StartupConfig startupConfig) throws Exception {
        final List<IMendixObject> mxStaticPaths = Core.retrieveByPath(context, startupConfig.getMendixObject(),
                StaticPath.MemberNames.StaticPath_StartupConfig.toString());
        if (mxStaticPaths != null && !mxStaticPaths.isEmpty()) {
            final List<String> values = new ArrayList<String>();
            for (final IMendixObject mxStaticPath : mxStaticPaths) {
                final String value = getTrimmedValue(mxStaticPath.getValue(context, StaticPath.MemberNames.path.toString()));
                if (!value.isEmpty()) {
                    values.add(value);
                }
            }
            configParamsMap.put("staticPaths", values);
        }
    }

    /**
     * Populate scopes from 'Scope' entity.
     *
     * @param context       The Mendix context.
     * @param startupConfig The startup configuration.
     * @throws Exception If there are errors when loading objects from the Mendix database.
     */
    private void populateScopes(final IContext context, final StartupConfig startupConfig) throws Exception {
        final List<IMendixObject> mxScopes = Core.retrieveByPath(context, startupConfig.getMendixObject(),
                Scope.MemberNames.Scope_StartupConfig.toString());
        if (mxScopes != null && !mxScopes.isEmpty()) {
            final List<String> values = new ArrayList<String>();
            for (final IMendixObject mxScope : mxScopes) {
                final String value = getTrimmedValue(mxScope.getValue(context, Scope.MemberNames.name.toString()));
                if (!value.isEmpty()) {
                    values.add(value);
                }
            }
            configParamsMap.put("scopes", values);
        }
    }

    /**
     * Trim the given value and return it. If the value is null, return an empty string.
     * 
     * @param value The string value.
     * @return The trimmed value.
     */
    private String getTrimmedValue(final String value) {
        return (value == null) ? "" : value.trim();
    }
}
