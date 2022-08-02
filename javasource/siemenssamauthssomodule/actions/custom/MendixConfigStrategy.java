// @<COPYRIGHT>@
// ==================================================
// Copyright 2019.
// Siemens Product Lifecycle Management Software Inc.
// All Rights Reserved.
// ==================================================
// @<COPYRIGHT>@

package siemenssamauthssomodule.actions.custom;

import java.util.Map;

import com.siemens.plm.webapp.strategies.IConfigStrategy;

/**
 * The configuration strategy to use for Mendix.
 */
public class MendixConfigStrategy implements IConfigStrategy {
    /**
     * The map to hold configuration parameters.
     */
    final private Map<String, Object> configParamsMap;

    /**
     * Constructor.
     *
     * @param startupConfig The configuration parameters map.
     */
    public MendixConfigStrategy(final Map<String, Object> configParamsMap) {
        this.configParamsMap = configParamsMap;
    }

    @Override
    public void fetchConfiguration(final String configSetName) {
        // no implementation - operate directly on cached map
    }

    @Override
    public Object getConfig(final String key) {
        return configParamsMap.get(key);
    }

    @Override
    public void setConfig(final String key, final Object value) {
        // no implementation - we don't want to override the configuration parameters
    }
}
