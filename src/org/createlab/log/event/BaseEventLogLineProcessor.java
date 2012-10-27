package org.createlab.log.event;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class BaseEventLogLineProcessor implements EventLogLineProcessor
   {
   public static final String EVENT_PARAMETER_TYPE_NAME = "type";
   public static final String EVENT_PARAMETER_TIME_NAME = "time";

   private static final Pattern EVENT_LOG_PATTERN = Pattern.compile("^.*event\\.json\\?(.*)&_=.*$");

   private int numLinesProcessed = 0;
   private int numEventLinesProcessed = 0;

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
            if (matcher.groupCount() == 1)
               {
               final String eventParameterStr = matcher.group(1);
               if (eventParameterStr != null && eventParameterStr.length() > 0)
                  {
                  final String[] keyValuePairs = eventParameterStr.split("&");
                  if (keyValuePairs != null && keyValuePairs.length > 0)
                     {
                     final Map<String, String> eventParameters = new HashMap<String, String>(keyValuePairs.length);
                     String eventType = null;
                     long eventTimeInMillis = -1;
                     for (final String keyValuePair : keyValuePairs)
                        {
                        final String[] keyAndValue = keyValuePair.split("=");
                        final String key = keyAndValue[0];
                        final String value = keyAndValue[1];
                        if (key != null && value != null)
                           {
                           if (EVENT_PARAMETER_TYPE_NAME.equals(key))
                              {
                              eventType = value;
                              }
                           else if (EVENT_PARAMETER_TIME_NAME.equals(key))
                              {
                              eventTimeInMillis = Long.parseLong(value);
                              }
                           else
                              {
                              eventParameters.put(key, value);
                              }
                           }
                        }
                     if (eventType != null && eventTimeInMillis >= 0)
                        {
                        final Event event = new Event(eventType, eventTimeInMillis, eventParameters);
                        process(event);
                        numEventLinesProcessed++;
                        }
                     }
                  }
               }
            }
         }
      }

   @Override
   public final void preProcess()
      {
      numLinesProcessed = 0;
      numEventLinesProcessed = 0;
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

   protected abstract void process(@NotNull final Event event);

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

   @Override
   public final int getNumberOfEventLinesProcessed()
      {
      return numEventLinesProcessed;
      }
   }
