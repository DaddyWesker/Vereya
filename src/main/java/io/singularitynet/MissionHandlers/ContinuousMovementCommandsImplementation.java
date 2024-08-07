// --------------------------------------------------------------------------------------------------
//  Copyright (c) 2016 Microsoft Corporation
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
//  associated documentation files (the "Software"), to deal in the Software without restriction,
//  including without limitation the rights to use, copy, modify, merge, publish, distribute,
//  sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in all copies or
//  substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
//  NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
//  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
//  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
// --------------------------------------------------------------------------------------------------
package io.singularitynet.MissionHandlers;

import io.singularitynet.MissionHandlerInterfaces.ICommandHandler;
import io.singularitynet.projectmalmo.ContinuousMovementCommands;


public class ContinuousMovementCommandsImplementation extends CommandGroup implements ICommandHandler {

    public ContinuousMovementCommandsImplementation(){
        setShareParametersWithChildren(true);	// Pass our parameter block on to the following children:
        this.addCommandHandler(new CommandForAttackAndUseImplementation());
        this.addCommandHandler(new CommandForWheeledRobotNavigationImplementation());
    }

    @Override
    public boolean parseParameters(Object params)
    {
        super.parseParameters(params);

        if (params == null || !(params instanceof ContinuousMovementCommands))
            return false;

        ContinuousMovementCommands cmparams = (ContinuousMovementCommands)params;
        setUpAllowAndDenyLists(cmparams.getModifierList());
        return true;
    }

    @Override
    public boolean isFixed() { return true; }
}
