package org.createlab.log.event;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class BritishMuseumStatsGenerator extends BaseEventLogLineProcessor
   {
   @NotNull
   private final DailyUsageStats eventProcessor = new DailyUsageStats();

   @NotNull
   private final Map<String, Integer> countsByType = new HashMap<String, Integer>();

   public BritishMuseumStatsGenerator()
      {
      for (final String eventType : BritishMuseumConstants.SUPPORTED_EVENT_TYPES.getSupportedEventTypeNames())
         {
         countsByType.put(eventType, 0);
         }
      }

   @Override
   protected final void doBeforeProcessingLines()
      {
      eventProcessor.beforeProcessingAnyEvents();
      }

   @Override
   protected void processEvent(final long dateInMillis,
                               final long eventTimeInMillis,
                               @NotNull final String eventType,
                               @NotNull final String eventParams)
      {
      if (BritishMuseumConstants.SUPPORTED_EVENT_TYPES.getSupportedEventTypeNames().contains(eventType))
         {
         countsByType.put(eventType, countsByType.get(eventType) + 1);
         eventProcessor.processEvent(dateInMillis, eventTimeInMillis, eventType, eventParams);
         }
      else
         {
         System.err.println("ERROR: Unexpected event type [" + eventType + "]");
         }
      }

   @Override
   public final void doAfterProcessingLines()
      {
      eventProcessor.afterProcessingAnyEvents();

      for (final String eventType : countsByType.keySet())
         {
         final int count = countsByType.get(eventType);
         System.out.println("Found [" + count + "] events for type [" + eventType + "]");
         }
      }

   private static final class DailyUsageStats extends CsvOutputEventProcessor
      {
      @NotNull
      private static final String[] PANO_IDS = new String[]{"46694", "79105", "79156", "79221", "80073", "89586"};

      @NotNull
      private static final File FILE = new File("daily-usage-stats.csv");

      @NotNull
      private final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");

      @Nullable
      private Long currentDate = null;
      @Nullable
      private Long timeOfEarliestEvent = null;
      @Nullable
      private Long timeOfLatestEvent = null;

      @Nullable
      private Long timeOfMostRecentInitEvent = null;
      @Nullable
      private Long timeOfMostRecentActiveEvent = null;
      @Nullable
      private Long timeOfMostRecentIdleEvent = null;
      @Nullable
      private Long timeOfMostRecentErrorEvent = null;

      private long durationInit = 0;
      private long durationActive = 0;
      private long durationIdle = 0;
      private long durationError = 0;

      private int numInits = 0;
      private int numIdles = 0;
      private int numTours = 0;
      private int numErrors = 0;
      private int numReloads = 0;

      private final Map<String, Integer> tourCounts = new HashMap<String, Integer>();

      @NotNull
      private BritishMuseumConstants.ActivityMode activityMode = BritishMuseumConstants.ActivityMode.UNKNOWN;

      private DailyUsageStats()
         {
         super(FILE);
         for (final String panoId : PANO_IDS)
            {
            tourCounts.put(panoId, 0);
            }
         }

      @Override
      protected void doBeforeProcessingAnyEvents()
         {
         final StringBuilder sb = new StringBuilder();

         sb.append("date_formatted").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("starting_time_formatted").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("ending_time_formatted").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("date").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("starting_time").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("ending_time").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("duration_init_millis").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("duration_active_millis").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("duration_idle_millis").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("duration_error_millis").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("num_startups").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("num_tours").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         for (int i = 0; i < PANO_IDS.length; i++)
            {
            sb.append("num_tour_" + PANO_IDS[i]).append(CsvOutputEventProcessor.FIELD_DELIMITER);
            }
         sb.append("num_idle_periods").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("num_errors").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("num_reloads").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         println(sb.toString());
         }

      @Override
      public void processEvent(@NotNull final Event event)
         {
         // not used
         }

      public void processEvent(@NotNull final Long eventDate,
                               final long eventTime,
                               @NotNull final String eventType,
                               @NotNull final String eventParams)
         {
         // see whether we need to start a new day
         if (!eventDate.equals(currentDate))
            {
            // write the stats, unless this is the first record we've seen (in which case the currentDate is null)
            if (currentDate != null)
               {
               writeDaysStats();
               }

            resetStats(eventDate);
            timeOfEarliestEvent = eventTime;
            }
         timeOfLatestEvent = eventTime;

         // figure out what mode we're in
         activityMode = BritishMuseumConstants.EVENT_TYPE_ACTIVITY_MODES.get(eventType);

         switch (activityMode)
            {
            case INIT:
               if (timeOfMostRecentInitEvent == null)
                  {
                  numInits++;
                  }
               durationInit += getElapsedTime(eventTime, timeOfMostRecentInitEvent);
               timeOfMostRecentInitEvent = eventTime;

               if (BritishMuseumConstants.EVENT_INIT_BEGIN.equals(eventType))
                  {
                  // An init means that we need to reset the times for the other activity types
                  timeOfMostRecentActiveEvent = null;
                  timeOfMostRecentIdleEvent = null;
                  timeOfMostRecentErrorEvent = null;
                  }
               else if (BritishMuseumConstants.EVENT_INIT_DONE.equals(eventType))
                  {
                  timeOfMostRecentInitEvent = null;
                  }
               break;
            case ACTIVE:
               if (timeOfMostRecentActiveEvent == null)
                  {
                  numTours++;
                  tourCounts.put(eventParams, tourCounts.get(eventParams) + 1);
                  }
               durationActive += getElapsedTime(eventTime, timeOfMostRecentActiveEvent);
               timeOfMostRecentActiveEvent = eventTime;

               if (BritishMuseumConstants.EVENT_TOUR_END.equals(eventType))
                  {
                  timeOfMostRecentActiveEvent = null;
                  }
               break;
            case IDLE:
               if (timeOfMostRecentIdleEvent == null)
                  {
                  numIdles++;
                  }
               durationIdle += getElapsedTime(eventTime, timeOfMostRecentIdleEvent);
               timeOfMostRecentIdleEvent = eventTime;

               if (BritishMuseumConstants.EVENT_IDLE_END.equals(eventType))
                  {
                  timeOfMostRecentIdleEvent = null;
                  }
               break;
            case ERROR:
               durationError += getElapsedTime(eventTime, timeOfMostRecentErrorEvent);
               timeOfMostRecentErrorEvent = eventTime;

               if (BritishMuseumConstants.EVENT_FLASH_WATCHDOG_ERROR_DETECTED.equals(eventType))
                  {
                  numErrors++;
                  }
               else if (BritishMuseumConstants.EVENT_FLASH_WATCHDOG_FORCE_BROWSER_RELOAD.equals(eventType))
                  {
                  numReloads++;
                  timeOfMostRecentErrorEvent = null;
                  }
               break;
            case UNKNOWN:
            default:
               System.err.println("Unexpected activity mode [" + activityMode + "] for event: " + eventType);
               break;
            }
         }

      private long getElapsedTime(final long currentEventTime, final Long previousEventTime)
         {
         return (previousEventTime == null) ? 0 : currentEventTime - previousEventTime;
         }

      @Override
      public void doAfterProcessingAnyEvents()
         {
         writeDaysStats();
         }

      private void writeDaysStats()
         {
         final StringBuilder sb = new StringBuilder();

         final DateTime currentDateJoda = new DateTime(currentDate, BritishMuseumConstants.GMT_TIME_ZONE);
         final DateTime timeOfFirstEventJoda = new DateTime(timeOfEarliestEvent, BritishMuseumConstants.GMT_TIME_ZONE);
         final DateTime timeOfLastEventJoda = new DateTime(timeOfLatestEvent, BritishMuseumConstants.GMT_TIME_ZONE);

         sb.append(dateTimeFormatter.print(currentDateJoda)).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(dateTimeFormatter.print(timeOfFirstEventJoda)).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(dateTimeFormatter.print(timeOfLastEventJoda)).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(currentDate).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(timeOfEarliestEvent).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(timeOfLatestEvent).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(durationInit).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(durationActive).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(durationIdle).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(durationError).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(numInits).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(numTours).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         for (int i = 0; i < PANO_IDS.length; i++)
            {
            sb.append(tourCounts.get(PANO_IDS[i])).append(CsvOutputEventProcessor.FIELD_DELIMITER);
            }
         sb.append(numIdles).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(numErrors).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(numReloads).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         println(sb.toString());
         }

      private void resetStats(final long date)
         {
         currentDate = date;

         timeOfEarliestEvent = null;
         timeOfLatestEvent = null;

         timeOfMostRecentInitEvent = null;
         timeOfMostRecentActiveEvent = null;
         timeOfMostRecentIdleEvent = null;
         timeOfMostRecentErrorEvent = null;

         durationInit = 0;
         durationActive = 0;
         durationIdle = 0;
         durationError = 0;
         numInits = 0;
         numIdles = 0;
         numTours = 0;
         numErrors = 0;
         numReloads = 0;
         for (final String panoId : tourCounts.keySet())
            {
            tourCounts.put(panoId, 0);
            }
         activityMode = BritishMuseumConstants.ActivityMode.UNKNOWN;
         }
      }
   }

//Found [48] events for type [init-begin]
//Found [48] events for type [init-done]

//Found [6150] events for type [tour-begin]
//Found [29726] events for type [tour-change-snapshot]
//Found [6129] events for type [tour-end]

//Found [6126] events for type [idle-begin]
//Found [68253] events for type [idle-change-pano]
//Found [6097] events for type [idle-end]

//Found [42] events for type [flash-watchdog-error-detected]
//Found [42] events for type [flash-watchdog-error-count-change]
//Found [21] events for type [flash-watchdog-force-browser-reload]
//------------------------------------------------

// SUM: 857497
//Found [2233] events for type [init-begin]
//Found [2231] events for type [init-done]

//Found [37907] events for type [tour-begin]
//Found [176011] events for type [tour-change-snapshot]
//Found [36243] events for type [tour-end]

//Found [37975] events for type [idle-begin]
//Found [523890] events for type [idle-change-pano]
//Found [37376] events for type [idle-end]

//Found [1769] events for type [flash-watchdog-error-detected]
//Found [206] events for type [flash-watchdog-error-count-change]
//Found [1656] events for type [flash-watchdog-force-browser-reload]
