package org.createlab.log.event;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings("NoopMethodInAbstractClass")
abstract class CsvOutputEventProcessor implements EventProcessor
   {
   private static final Logger LOG = Logger.getLogger(CsvOutputEventProcessor.class);

   @NotNull
   private final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");

   @NotNull
   public static final String FIELD_DELIMITER = ",";

   @NotNull
   private final File file;

   @Nullable
   private PrintStream printStream;

   protected CsvOutputEventProcessor(@NotNull final File file)
      {
      this.file = file;
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
         }
      catch (IOException e)
         {
         LOG.error("IOException while trying to open a PrintStream for file [" + file + "]", e);
         }
      doBeforeProcessingAnyEvents();
      }

   protected void doBeforeProcessingAnyEvents()
      {
      // do nothing
      }

   @Override
   public void processEvent(@NotNull final Event event)
      {
      final StringBuilder sb = new StringBuilder();
      sb.append(dateTimeFormatter.print(event.getDate()));
      sb.append(FIELD_DELIMITER);
      sb.append(dateTimeFormatter.print(event.getTime()));
      sb.append(FIELD_DELIMITER);
      sb.append(event.getDateInMillis());
      sb.append(FIELD_DELIMITER);
      sb.append(event.getTimeInMillis());
      sb.append(FIELD_DELIMITER);
      sb.append(event.getType());
      sb.append(FIELD_DELIMITER);
      appendEventParameters(event, sb);

      printStream.println(sb.toString());
      }

   protected final boolean println(final String s)
      {
      if (printStream != null)
         {
         printStream.println(s);
         return true;
         }
      else
         {
         System.err.println("CsvOutputEventProcessor.println(): WARNING: Nothing written since PrintStream is null.");
         return false;
         }
      }

   protected void appendEventParameters(@NotNull final Event event, @NotNull final StringBuilder stringBuilder)
      {
      // do nothing
      }

   @Override
   public final void afterProcessingAnyEvents()
      {
      doAfterProcessingAnyEvents();
      if (printStream != null)
         {
         printStream.close();
         }
      }

   protected void doAfterProcessingAnyEvents()
      {
      // do nothing
      }
   }
