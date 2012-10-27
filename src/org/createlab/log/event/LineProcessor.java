package org.createlab.log.event;

import org.jetbrains.annotations.NotNull;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
interface LineProcessor
   {
   /** Should be called before any lines are processed. */
   void preProcess();

   void processLine(@NotNull final String line);

   /** Should be called after all lines are processed. */
   void postProcess();
   }
