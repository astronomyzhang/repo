package com.siemens.dasheng.web.event;

import org.springframework.context.ApplicationEvent;

/**
 * when da provider update
 *
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2019/2/27.
 */
public class DaProviderUpdateEvent extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public DaProviderUpdateEvent(Object source) {
        super(source);
    }

}
