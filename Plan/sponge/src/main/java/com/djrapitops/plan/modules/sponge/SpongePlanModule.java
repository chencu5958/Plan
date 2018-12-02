/*
 *  This file is part of Player Analytics (Plan).
 *
 *  Plan is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License v3 as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Plan is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Plan. If not, see <https://www.gnu.org/licenses/>.
 */
package com.djrapitops.plan.modules.sponge;

import com.djrapitops.plan.PlanPlugin;
import com.djrapitops.plan.PlanSponge;
import com.djrapitops.plan.command.PlanCommand;
import com.djrapitops.plugin.command.CommandNode;
import dagger.Binds;
import dagger.Module;

import javax.inject.Named;

/**
 * Dagger module for binding PlanSponge instance.
 *
 * @author Rsl1122
 */
@Module
public interface SpongePlanModule {

    @Binds
    PlanPlugin bindPlanPlugin(PlanSponge plugin);

    @Binds
    @Named("mainCommand")
    CommandNode bindMainCommand(PlanCommand command);
}