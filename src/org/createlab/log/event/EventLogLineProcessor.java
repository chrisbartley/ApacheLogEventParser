package org.createlab.log.event;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
interface EventLogLineProcessor extends LineProcessor
   {
   int getNumberOfLinesProcessed();

   int getNumberOfEventLinesProcessed();
   }
