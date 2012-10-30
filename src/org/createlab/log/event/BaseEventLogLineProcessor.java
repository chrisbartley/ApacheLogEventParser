package org.createlab.log.event;

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

   private static final Pattern PATTERN = Pattern.compile("^([^,]+),([^,]+),([\\d]+),([\\d]+),([^,]+),(.*)$");

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
            if (matcher.groupCount() == 6)
               {
               try
                  {
                  // ignore groups 1 and 2 since it's just the formatted dates
                  processEvent(Long.parseLong(matcher.group(3)),
                               Long.parseLong(matcher.group(4)),
                               matcher.group(5),
                               matcher.group(6));
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
   }
