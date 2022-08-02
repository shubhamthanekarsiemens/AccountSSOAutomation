package siemenssamauthssomodule.actions.custom;

import com.siemens.plm.logger.IServiceLogConfig;

public class ServiceLogConfig implements IServiceLogConfig{

	private String eventSource = "SSO Module Application";
    
    private String eventSourceVersion = "1.0.0";
    
    private String eventVersion = "1.0.0";
    
    
    @Override
    public String getEventSource() {
        return this.eventSource;
    }

    @Override
    public String getEventSourceVersion() {
        return this.eventSourceVersion;
    }

    @Override
    public String getEventVersion() {
        
        return this.eventVersion;
    }

    @Override
    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
        
    }

    @Override
    public void setEventSourceVersion(String eventSourceVersion) {
        this.eventSourceVersion = eventSourceVersion;
        
    }

    @Override
    public void setEventVersion(String eventVersion) {
        this.eventVersion = eventVersion;
    }
}
