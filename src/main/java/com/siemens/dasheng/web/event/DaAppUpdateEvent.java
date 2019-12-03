package com.siemens.dasheng.web.event;

import org.springframework.context.ApplicationEvent;

/**
 * when da app update
 *
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2019/2/27.
 */
public class DaAppUpdateEvent extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public DaAppUpdateEvent(Object source) {
        super(source);
    }

}
