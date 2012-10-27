package org.createlab.log.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class BaseEventProcessor implements EventProcessor
   {
   @NotNull
   private final String eventType;

   @Nullable
   private final ParameterStringifier parameterStringifier;

   BaseEventProcessor(@NotNull final String eventType, @Nullable final ParameterStringifier parameterStringifier)
      {
      this.eventType = eventType;
      this.parameterStringifier = parameterStringifier;
      }

   @NotNull
   protected final String getEventType()
      {
      return eventType;
      }

   @Nullable
   public final ParameterStringifier getParameterStringifier()
      {
      return parameterStringifier;
      }
   }
