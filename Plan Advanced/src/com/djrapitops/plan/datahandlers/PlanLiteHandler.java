
package com.djrapitops.plan.datahandlers;

import com.djrapitops.plan.Plan;
import com.djrapitops.plan.PlanLiteHook;

public class PlanLiteHandler {
    private Plan plugin;
    private PlanLiteHook hook;

    public PlanLiteHandler(Plan plugin) {
        this.plugin = plugin;
        hook = plugin.getPlanLiteHook();
    }    
}
