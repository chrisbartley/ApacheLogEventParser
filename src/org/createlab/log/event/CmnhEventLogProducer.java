package org.createlab.log.event;

import java.util.TimeZone;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class CmnhEventLogProducer extends EventLogProducer
   {
   public CmnhEventLogProducer()
      {
      super(CmnhConstants.SUPPORTED_EVENT_TYPES);
      }

   @Override
   protected TimeZone getTimeZone()
      {
      return TimeZone.getTimeZone("GMT-5");
      }
   }
