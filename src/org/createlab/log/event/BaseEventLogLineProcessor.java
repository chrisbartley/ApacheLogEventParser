package org.createlab.log.event;

import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class BaseEventLogLineProcessor implements LineProcessor
   {
   private static final Logger LOG = Logger.getLogger(BaseEventLogLineProcessor.class);

   public static final String EVENT_PARAMETER_TYPE_NAME = "type";
   public static final String EVENT_PARAMETER_TIME_NAME = "time";
   private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("GMT+0");

   private static final Pattern EVENT_LOG_PATTERN = Pattern.compile("^([\\d]+),([\\d]+),([^,]+),(.*)$");

   private int numLinesProcessed = 0;

   @Override
   public final void processLine(@NotNull final String line)
      {
      numLinesProcessed++;
      if (line.length() > 0)
         {
         final Matcher matcher = EVENT_LOG_PATTERN.matcher(line);
         final boolean isMatchFound = matcher.find();
         if (isMatchFound)
            {
            if (matcher.groupCount() == 4)
               {
               try
                  {
                  processEvent(Long.parseLong(matcher.group(1)),
                               Long.parseLong(matcher.group(2)),
                               matcher.group(3),
                               matcher.group(4));
                  }
               catch (NumberFormatException e)
                  {
                  LOG.error("NumberFormatException while processing line [" + line + "]", e);
                  }
               }
            }
         }
      }

   @Override
   public final void preProcess()
      {
      numLinesProcessed = 0;
      doBeforeProcessingLines();
      }

   @Override
   public final void postProcess()
      {
      doAfterProcessingLines();
      }

   /** Called before any lines are processed.  Does nothing by default. */
   protected void doBeforeProcessingLines()
      {
      // do nothing
      }

   protected abstract void processEvent(final long dateInMillis,
                                        final long eventTimeInMillis,
                                        @NotNull final String eventType,
                                        @NotNull final String eventParams);

   /** Called after all lines are processed.  Does nothing by default. */
   protected void doAfterProcessingLines()
      {
      // do nothing
      }

   @Override
   public final int getNumberOfLinesProcessed()
      {
      return numLinesProcessed;
      }

   protected TimeZone getTimeZone()
      {
      return DEFAULT_TIME_ZONE;
      }
   }
