package org.createlab.log.event;

import org.jetbrains.annotations.NotNull;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class NoOpParameterStringifier implements ParameterStringifier
   {
   private final String parameterName;

   NoOpParameterStringifier(final String parameterName)
      {
      this.parameterName = parameterName;
      }

   @Override
   public String convertParametersToString(@NotNull final Event event)
      {
      return event.getParameterValue(parameterName);
      }
   }
