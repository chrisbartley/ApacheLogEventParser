package org.createlab.log.event;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class CsvOutputEventProcessor extends BaseEventProcessor
   {
   private static final Logger LOG = Logger.getLogger(CsvOutputEventProcessor.class);

   private static final String FILENAME_PREFIX = "event-";
   private static final String FILENAME_SUFFIX = ".csv";
   private static final double DEFAULT_PARAMETER_VALUE = 0.0;

   @NotNull
   private final File file;

   @Nullable
   private PrintStream printStream;

   @NotNull
   private final String bodyTrackChannelName;

   private final double defaultParameterValue;

   CsvOutputEventProcessor(@NotNull final String eventType)
      {
      this(eventType, DEFAULT_PARAMETER_VALUE, eventType);
      }

   CsvOutputEventProcessor(@NotNull final String eventType,
                           final double defaultParameterValue)
      {
      this(eventType, null, defaultParameterValue, eventType);
      }

   CsvOutputEventProcessor(final String eventType,
                           final double defaultParameterValue,
                           final String bodyTrackChannelName)
      {
      this(eventType, null, defaultParameterValue, bodyTrackChannelName);
      }

   CsvOutputEventProcessor(@NotNull final String eventType,
                           @Nullable final ParameterStringifier parameterStringifier)
      {
      this(eventType, parameterStringifier, DEFAULT_PARAMETER_VALUE, eventType);
      }

   private CsvOutputEventProcessor(@NotNull final String eventType,
                                   @Nullable final ParameterStringifier parameterStringifier,
                                   final double defaultParameterValue,
                                   @NotNull final String bodyTrackChannelName)
      {
      super(eventType, parameterStringifier);
      this.bodyTrackChannelName = bodyTrackChannelName;
      this.defaultParameterValue = defaultParameterValue;

      file = new File(FILENAME_PREFIX + eventType + FILENAME_SUFFIX);
      if (file.exists())
         {
         System.err.println("ERROR: File [" + file + "] already exists!!! Aborting.");
         System.exit(1);
         }
      }

   @Override
   public final void beforeProcessingAnyEvents()
      {
      try
         {
         printStream = new PrintStream(file);
         printStream.println("[{\"channel_names\":[\"" + bodyTrackChannelName + "\"], \"data\":[");
         }
      catch (IOException e)
         {
         LOG.error("IOException while trying to open a PrintStream for file [" + file + "]", e);
         }
      }

   @Override
   public final void process(@NotNull final Event event)
      {
      final double timeInSeconds = event.getTimeInMillis() / 1000.0;
      final StringBuilder sb = new StringBuilder("[" + String.valueOf(timeInSeconds) + ",");

      final ParameterStringifier parameterStringifier = getParameterStringifier();
      if (parameterStringifier != null)
         {
         sb.append(parameterStringifier.convertParametersToString(event));
         }
      else
         {
         sb.append(defaultParameterValue);
         }
      sb.append("],");
      printStream.println(sb.toString());
      }

   @Override
   public final void afterProcessingAnyEvents()
      {
      if (printStream != null)
         {
         // end with an empty data point because all of the actual data points above were followed with a comma
         printStream.println("[]");
         printStream.println("]}]");
         printStream.close();
         }
      }
   }
