package org.createlab.log.event;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class LogFileProcessor
   {
   private static final Logger LOG = Logger.getLogger(LogFileProcessor.class);

   private static final String OPTION_PRINT_TYPES = "--list-event-types";

   public static void main(final String[] args)
      {
      if (args == null || args.length < 2)
         {
         System.err.println();
         System.err.println("Usage: java -jar event-parser.jar LINE_PROCESSOR_CLASS_NAME FILENAME [--list-event-types]");
         System.err.println();
         System.err.println("   --list-event-types            Prints a list of all the event types found in FILENAME");
         System.err.println();
         }
      else
         {
         final String logLineProcessorClassName = args[0];
         final String eventLogFilename = args[1];
         final File logFile = new File(eventLogFilename);

         if (logFile.exists() && logFile.canRead())
            {
            final LogFileProcessor logFileProcessor = new LogFileProcessor();
            if (args.length == 2)
               {
               final LineProcessor lineProcessor = instantiateLogLineProcessor(logLineProcessorClassName);
               if (lineProcessor == null)
                  {
                  System.err.println("ERROR: Failed to instantiate the LineProcessor [" + logLineProcessorClassName + "]");
                  }
               else
                  {
                  logFileProcessor.parse(logFile, lineProcessor);
                  }
               }
            else if (args[2].equals(OPTION_PRINT_TYPES))
               {
               logFileProcessor.listEventTypes(logFile);
               }
            }
         else
            {
            System.err.println("ERROR: Cannot read file [" + logFile + "].  Aborting.");
            }
         }
      }

   private static LineProcessor instantiateLogLineProcessor(final String logLineProcessorClassName)
      {
      try
         {
         final Class clazz = Class.forName(logLineProcessorClassName);
         final Constructor constructor = clazz.getConstructor();
         if (constructor != null)
            {
            final LineProcessor instance = (LineProcessor)constructor.newInstance();
            if (instance == null)
               {
               LOG.error("Instantiation of implementation class [" + logLineProcessorClassName + "] returned null.  Weird.");
               }
            else
               {
               return instance;
               }
            }
         }
      catch (ClassNotFoundException e)
         {
         LOG.error("ClassNotFoundException while trying to find LineProcessor implementation [" + logLineProcessorClassName + "]", e);
         }
      catch (NoSuchMethodException e)
         {
         LOG.error("NoSuchMethodException while trying to find no-arg constructor for LineProcessor implementation [" + logLineProcessorClassName + "]", e);
         }
      catch (IllegalAccessException e)
         {
         LOG.error("IllegalAccessException while trying to instantiate LineProcessor implementation [" + logLineProcessorClassName + "]", e);
         }
      catch (InvocationTargetException e)
         {
         LOG.error("InvocationTargetException while trying to instantiate LineProcessor implementation [" + logLineProcessorClassName + "]", e);
         }
      catch (InstantiationException e)
         {
         LOG.error("InstantiationException while trying to instantiate LineProcessor implementation [" + logLineProcessorClassName + "]", e);
         }

      return null;
      }

   private void listEventTypes(@NotNull final File logFile)
      {
      LOG.debug("LogFileProcessor.printTypes(" + logFile + ")");
      final SortedSet<String> types = new TreeSet<String>();

      final LineReader lineReader = new LineReader(logFile);
      lineReader.read(
            new BaseApacheLogLineProcessor()
            {
            @Override
            protected void processEvent(@NotNull Event event)
               {
               types.add(event.getType());
               }

            @Override
            public void doAfterProcessingLines()
               {
               System.out.println("Number of lines processed       = " + getNumberOfLinesProcessed());
               }
            });

      for (final String type : types)
         {
         System.out.println(type);
         }
      }

   private void parse(@NotNull final File logFile, final LineProcessor lineProcessor)
      {
      LOG.debug("LogFileProcessor.parse(" + logFile + ")");
      final LineReader lineReader = new LineReader(logFile);
      lineReader.read(lineProcessor);
      }
   }
