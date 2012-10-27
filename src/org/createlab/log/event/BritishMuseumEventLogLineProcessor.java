package org.createlab.log.event;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class BritishMuseumEventLogLineProcessor extends BaseEventLogLineProcessor
   {
   private static final Logger LOG = Logger.getLogger(BritishMuseumEventLogLineProcessor.class);

   private static final String EVENT_FLASH_WATCHDOG_ERROR_COUNT_CHANGE = "flash-watchdog-error-count-change";
   private static final String EVENT_FLASH_WATCHDOG_ERROR_DETECTED = "flash-watchdog-error-detected";
   private static final String EVENT_FLASH_WATCHDOG_FORCE_BROWSER_RELOAD = "flash-watchdog-force-browser-reload";
   private static final String EVENT_IDLE_BEGIN = "idle-begin";
   private static final String EVENT_IDLE_CHANGE_PANO = "idle-change-pano";
   private static final String EVENT_IDLE_END = "idle-end";
   private static final String EVENT_INIT_BEGIN = "init-begin";
   private static final String EVENT_INIT_DONE = "init-done";
   private static final String EVENT_TOUR_BEGIN = "tour-begin";
   private static final String EVENT_TOUR_CHANGE_SNAPSHOT = "tour-change-snapshot";
   private static final String EVENT_TOUR_END = "tour-end";

   private static final SortedSet<String> EVENT_TYPE_NAMES = Collections.unmodifiableSortedSet(new TreeSet<String>(Arrays.asList(
         EVENT_FLASH_WATCHDOG_ERROR_COUNT_CHANGE,
         EVENT_FLASH_WATCHDOG_ERROR_DETECTED,
         EVENT_FLASH_WATCHDOG_FORCE_BROWSER_RELOAD,
         EVENT_IDLE_BEGIN,
         EVENT_IDLE_CHANGE_PANO,
         EVENT_IDLE_END,
         EVENT_INIT_BEGIN,
         EVENT_INIT_DONE,
         EVENT_TOUR_BEGIN,
         EVENT_TOUR_CHANGE_SNAPSHOT,
         EVENT_TOUR_END)));
   private static final Map<String, EventProcessor> EVENT_PROCESSORS;

   static
      {
      final Map<String, EventProcessor> eventProcessors = new HashMap<String, EventProcessor>();
      eventProcessors.put(EVENT_FLASH_WATCHDOG_ERROR_COUNT_CHANGE,
                          new CsvOutputEventProcessor(EVENT_FLASH_WATCHDOG_ERROR_COUNT_CHANGE, new NoOpParameterStringifier("count")));
      eventProcessors.put(EVENT_FLASH_WATCHDOG_ERROR_DETECTED,
                          new CsvOutputEventProcessor(EVENT_FLASH_WATCHDOG_ERROR_DETECTED));
      eventProcessors.put(EVENT_FLASH_WATCHDOG_FORCE_BROWSER_RELOAD,
                          new CsvOutputEventProcessor(EVENT_FLASH_WATCHDOG_FORCE_BROWSER_RELOAD));

      eventProcessors.put(EVENT_IDLE_BEGIN,
                          new CsvOutputEventProcessor(EVENT_IDLE_BEGIN, 1, "idle-state"));
      eventProcessors.put(EVENT_IDLE_CHANGE_PANO,
                          new CsvOutputEventProcessor(EVENT_IDLE_CHANGE_PANO, 1, "idle-state"));
      eventProcessors.put(EVENT_IDLE_END,
                          new CsvOutputEventProcessor(EVENT_IDLE_END, 0, "idle-state"));

      eventProcessors.put(EVENT_INIT_BEGIN,
                          new CsvOutputEventProcessor(EVENT_INIT_BEGIN, 0));
      eventProcessors.put(EVENT_INIT_DONE,
                          new CsvOutputEventProcessor(EVENT_INIT_DONE, 1));

      eventProcessors.put(EVENT_TOUR_BEGIN,
                          new CsvOutputEventProcessor(EVENT_TOUR_BEGIN, new NoOpParameterStringifier("pano")));
      eventProcessors.put(EVENT_TOUR_CHANGE_SNAPSHOT,
                          new CsvOutputEventProcessor(EVENT_TOUR_CHANGE_SNAPSHOT, new NoOpParameterStringifier("pano")));
      eventProcessors.put(EVENT_TOUR_END,
                          new CsvOutputEventProcessor(EVENT_TOUR_END, new NoOpParameterStringifier("pano")));

      EVENT_PROCESSORS = Collections.unmodifiableMap(eventProcessors);
      }

   private final Map<String, Integer> countsByType = new HashMap<String, Integer>();

   public BritishMuseumEventLogLineProcessor()
      {
      for (final String eventType : EVENT_TYPE_NAMES)
         {
         countsByType.put(eventType, 0);
         }
      }

   @Override
   protected void doBeforeProcessingLines()
      {
      for (final EventProcessor eventProcessor : EVENT_PROCESSORS.values())
         {
         eventProcessor.beforeProcessingAnyEvents();
         }
      }

   @Override
   protected void process(@NotNull final Event event)
      {
      if (EVENT_TYPE_NAMES.contains(event.getType()))
         {
         countsByType.put(event.getType(), countsByType.get(event.getType()) + 1);
         final EventProcessor eventProcessor = EVENT_PROCESSORS.get(event.getType());
         if (eventProcessor != null)
            {
            eventProcessor.process(event);
            }
         }
      else
         {
         System.err.println("ERROR: Unexpected event type [" + event.getType() + "].  Event: " + event);
         }
      }

   @Override
   public void doAfterProcessingLines()
      {
      LOG.debug("BritishMuseumEventLogLineProcessor.doAfterProcessingLines(): ");

      for (final EventProcessor eventProcessor : EVENT_PROCESSORS.values())
         {
         eventProcessor.afterProcessingAnyEvents();
         }

      for (final String eventType : countsByType.keySet())
         {
         final int count = countsByType.get(eventType);
         System.out.println("Found [" + count + "] events for type [" + eventType + "]");
         }
      }
   }

// SUM: 857497
//Found [206] events for type [flash-watchdog-error-count-change]
//Found [176011] events for type [tour-change-snapshot]
//Found [1656] events for type [flash-watchdog-force-browser-reload]
//Found [37907] events for type [tour-begin]
//Found [36243] events for type [tour-end]
//Found [1769] events for type [flash-watchdog-error-detected]
//Found [2231] events for type [init-done]
//Found [523890] events for type [idle-change-pano]
//Found [37376] events for type [idle-end]
//Found [37975] events for type [idle-begin]
//Found [2233] events for type [init-begin]

/*

*/