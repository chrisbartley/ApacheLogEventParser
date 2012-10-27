package org.createlab.log.event;

import org.jetbrains.annotations.NotNull;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
interface ParameterStringifier
   {
   String convertParametersToString(@NotNull final Event event);
   }
