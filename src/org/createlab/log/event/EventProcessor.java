package org.createlab.log.event;

import org.jetbrains.annotations.NotNull;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
interface EventProcessor
   {
   void beforeProcessingAnyEvents();

   void processEvent(@NotNull final Event event);

   void afterProcessingAnyEvents();
   }
