package org.createlab.log.event;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class BaseApacheLogLineProcessor implements LineProcessor
   {
   public static final String EVENT_PARAMETER_TYPE_NAME = "type";
   public static final String EVENT_PARAMETER_TIME_NAME = "time";
   private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("GMT+0");

   private static final Pattern PATTERN = Pattern.compile("^.*event\\.json\\?(.*)&_=.*$");

   private int numLinesProcessed = 0;

   @Override
   public final void processLine(@NotNull final String line)
      {
      numLinesProcessed++;
      if (line.length() > 0)
         {
         final Matcher matcher = PATTERN.matcher(line);
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
                        final Event event = new Event(eventType, eventTimeInMillis, eventParameters, getTimeZone());
                        processEvent(event);
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

   protected abstract void processEvent(@NotNull final Event event);

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
