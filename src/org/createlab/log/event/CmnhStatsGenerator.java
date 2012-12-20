package org.createlab.log.event;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static org.createlab.log.event.CmnhConstants.EventType;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class CmnhStatsGenerator extends BaseEventLogLineProcessor
   {
   @NotNull
   private final DailyUsageStats eventProcessor = new DailyUsageStats();

   @NotNull
   private final Map<EventType, Integer> eventTypeCounts = new HashMap<EventType, Integer>();

   public CmnhStatsGenerator()
      {
      for (final EventType eventType : EventType.values())
         {
         eventTypeCounts.put(eventType, 0);
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
                               @NotNull final String eventTypeName,
                               @NotNull final String eventParams)
      {
      final EventType eventType = EventType.findByName(eventTypeName);
      if (eventType != null)
         {
         eventTypeCounts.put(eventType, eventTypeCounts.get(eventType) + 1);
         eventProcessor.processEvent(dateInMillis, eventTimeInMillis, eventType, eventParams);
         }
      else
         {
         System.err.println("ERROR: Unexpected event type [" + eventTypeName + "]");
         }
      }

   @Override
   public final void doAfterProcessingLines()
      {
      eventProcessor.afterProcessingAnyEvents();

      for (final EventType eventType : eventTypeCounts.keySet())
         {
         final int count = eventTypeCounts.get(eventType);
         System.out.println("Found [" + count + "] events for type [" + eventType + "]");
         }
      }

   private static final class DailyUsageStats extends CsvOutputEventProcessor
      {
      @NotNull
      private static final String[] INTEREST_POINT_IDS = new String[]{"ip1", "ip2", "ip3", "ip4", "ip5", "ip6", "ip7", "ip8", "ip9", "ip10", "ip11", "ip12", "ip13", "ip14"};

      // Including null in the THEME_IDS since null is a valid choice (no theme selected)
      @NotNull
      private static final String[] THEME_IDS = new String[]{"null", "theme0", "theme1", "theme2", "theme3", "theme4"};

      @NotNull
      private static final EventType[] INTERACTION_EVENTS = new EventType[]{EventType.MEDIA_PANEL_CLOSE,
                                                                            EventType.MEDIA_PLAY,
                                                                            EventType.MEDIA_PAUSE,
                                                                            EventType.MEDIA_ENDED,
                                                                            EventType.MEDIA_TIME_CHANGED,
                                                                            EventType.NAV_RESET_HOME_VIEW,
                                                                            EventType.THEMES_PANEL_DRAWER_OPEN,
                                                                            EventType.THEMES_PANEL_DRAWER_CLOSE};
      @NotNull
      private static final String[] MEDIA_FILES = new String[]{"auroch_hunt.ogv",
                                                               "onager_mother_and_foal.ogv",
                                                               "hunting_dog_party.ogv",
                                                               "aurochs_horns.ogv",
                                                               "asian_onager.ogv",
                                                               "cloaked_hunter.ogv",
                                                               "aurochs.webm",
                                                               "beard_3672.webm",
                                                               "olsen_3649.webm",
                                                               "abdulaziz_3736.webm",
                                                               "khan_3745.webm",
                                                               "beard_3670.webm"
      };

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

      private int numInterestPointSelections = 0;
      private int numThemeSelections = 0;

      @NotNull
      private final Map<String, Integer> interestPointSelectionCounts = new HashMap<String, Integer>();

      @NotNull
      private final Map<String, Integer> themeSelectionCounts = new HashMap<String, Integer>();

      @NotNull
      private final Map<CmnhConstants.ActivityMode, Integer> activityModeCounts = new HashMap<CmnhConstants.ActivityMode, Integer>();

      @NotNull
      private final Map<CmnhConstants.ActivityMode, Long> activityModeDurations = new HashMap<CmnhConstants.ActivityMode, Long>();

      @NotNull
      private final Map<EventType, Integer> interactionEventCounts = new HashMap<EventType, Integer>();

      @NotNull
      private final SortedMap<String, Integer> mediaPlayCounts = new TreeMap<String, Integer>();

      @NotNull
      private CmnhConstants.ActivityMode activityMode = CmnhConstants.ActivityMode.UNKNOWN;

      private DailyUsageStats()
         {
         super(FILE);
         resetStats(null);
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

         sb.append("duration_total_millis").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("duration_init_millis").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("duration_active_millis").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("duration_idle_millis").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("duration_other_millis").append(CsvOutputEventProcessor.FIELD_DELIMITER);

         sb.append("num_init_periods").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("num_active_periods").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("num_idle_periods").append(CsvOutputEventProcessor.FIELD_DELIMITER);

         sb.append("num_theme_selections").append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append("num_interest_point_selections").append(CsvOutputEventProcessor.FIELD_DELIMITER);

         for (final String themeId : THEME_IDS)
            {
            sb.append("null".equals(themeId) ? "themeNone" : themeId).append(CsvOutputEventProcessor.FIELD_DELIMITER);
            }

         for (final String interestPointId : INTEREST_POINT_IDS)
            {
            sb.append(interestPointId).append(CsvOutputEventProcessor.FIELD_DELIMITER);
            }

         for (final EventType eventType : INTERACTION_EVENTS)
            {
            sb.append(eventType.getName()).append(CsvOutputEventProcessor.FIELD_DELIMITER);
            }

         for (final String mediaFile : MEDIA_FILES)
            {
            sb.append("play_" + mediaFile).append(CsvOutputEventProcessor.FIELD_DELIMITER);
            }

         println(sb.toString());
         }

      @Override
      public void processEvent(@NotNull final Event event)
         {
         // not used
         }

      public void processEvent(@NotNull final Long eventDate,
                               final long eventTime,
                               @NotNull final EventType eventType,
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
         final long elapsedTimeSinceLastEvent = getElapsedTime(eventTime, timeOfLatestEvent);
         timeOfLatestEvent = eventTime;

         switch (eventType)
            {
            case INIT_START:
               // When we hit an init-start, then the page has just loaded so we don't want to add the elapsed time to
               // the previous mode, because there really WASN'T a previous mode.  The machine was either just powered
               // on for the day, rebooted, or the page reloaded (due to either a manual reload or a browser crash and
               // auto-restart by the watchdog script).  So, instead, we add the elapsed time to the UNKNOWN mode.
               updateActivityModeDuration(CmnhConstants.ActivityMode.UNKNOWN, elapsedTimeSinceLastEvent);

               // increment the INIT activity mode counter
               incrementActivityModeCounter(CmnhConstants.ActivityMode.INIT);

               // We're now in INIT mode
               activityMode = CmnhConstants.ActivityMode.INIT;
               break;
            case INIT_FINISH:

               // init-finish always immediately follows an init-start, so we can simply add the elapsed time since the
               // last event to the INIT mode duration tracker
               updateActivityModeDuration(CmnhConstants.ActivityMode.INIT, elapsedTimeSinceLastEvent);

               // We always go into IDLE mode at the end of init-finish.
               activityMode = CmnhConstants.ActivityMode.IDLE;
               break;
            case MEDIA_PANEL_CLOSE:
               updateActivityModeDuration(activityMode, elapsedTimeSinceLastEvent);
               incrementInteractionEventCounter(EventType.MEDIA_PANEL_CLOSE);
               break;
            case MEDIA_PLAY:
               updateActivityModeDuration(activityMode, elapsedTimeSinceLastEvent);
               incrementInteractionEventCounter(EventType.MEDIA_PLAY);
               mediaPlayCounts.put(eventParams, mediaPlayCounts.get(eventParams) + 1);
               break;
            case MEDIA_PAUSE:
               updateActivityModeDuration(activityMode, elapsedTimeSinceLastEvent);
               incrementInteractionEventCounter(EventType.MEDIA_PAUSE);
               break;
            case MEDIA_ENDED:
               updateActivityModeDuration(activityMode, elapsedTimeSinceLastEvent);
               incrementInteractionEventCounter(EventType.MEDIA_ENDED);
               break;
            case MEDIA_TIME_CHANGED:
               updateActivityModeDuration(activityMode, elapsedTimeSinceLastEvent);
               incrementInteractionEventCounter(EventType.MEDIA_TIME_CHANGED);
               break;
            case NAV_THEME_CHANGE:
               numThemeSelections++;

               themeSelectionCounts.put(eventParams, themeSelectionCounts.get(eventParams) + 1);

               updateActivityModeDuration(activityMode, elapsedTimeSinceLastEvent);
               break;
            case NAV_INTEREST_POINT:
               numInterestPointSelections++;

               interestPointSelectionCounts.put(eventParams, interestPointSelectionCounts.get(eventParams) + 1);

               updateActivityModeDuration(activityMode, elapsedTimeSinceLastEvent);
               break;
            case NAV_TAP:
               updateActivityModeDuration(activityMode, elapsedTimeSinceLastEvent);
               break;
            case NAV_RESET_HOME_VIEW:
               updateActivityModeDuration(activityMode, elapsedTimeSinceLastEvent);
               incrementInteractionEventCounter(EventType.NAV_RESET_HOME_VIEW);
               break;
            case NAV_MOVE_START:
               updateActivityModeDuration(activityMode, elapsedTimeSinceLastEvent);
               break;
            case NAV_MOVE_FINISH:
               updateActivityModeDuration(activityMode, elapsedTimeSinceLastEvent);
               break;
            case THEMES_PANEL_DRAWER_OPEN:
               updateActivityModeDuration(activityMode, elapsedTimeSinceLastEvent);
               incrementInteractionEventCounter(EventType.THEMES_PANEL_DRAWER_OPEN);
               break;
            case THEMES_PANEL_DRAWER_CLOSE:
               updateActivityModeDuration(activityMode, elapsedTimeSinceLastEvent);
               incrementInteractionEventCounter(EventType.THEMES_PANEL_DRAWER_CLOSE);
               break;
            case IDLE_SCREEN_VISIBLE:

               // increment the IDLE activity mode counter
               incrementActivityModeCounter(CmnhConstants.ActivityMode.IDLE);

               // In order to get to the idle-screen-visible event, the kiosk must have been idle since the last event,
               // so the correct thing to do is count the elapsed time since last event towards IDLE
               updateActivityModeDuration(CmnhConstants.ActivityMode.IDLE, elapsedTimeSinceLastEvent);

               // The idle screen being visible means we're back in ACTIVE mode
               // (and actually have been idle for the past 90 seconds)
               activityMode = CmnhConstants.ActivityMode.IDLE;
               break;
            case IDLE_SCREEN_HIDDEN:

               // There are a few instances of two init-finish events in a row (probably due to people double-tapping
               // the idle mode screen which triggers two init-finish events).  In those cases, go ahead and add the
               // elapsed time to the IDLE mode, but don't count it as the beginning of a new ACTIVE mode period.
               if (activityMode.equals(CmnhConstants.ActivityMode.IDLE))
                  {
                  // increment the ACTIVE activity mode counter
                  incrementActivityModeCounter(CmnhConstants.ActivityMode.ACTIVE);
                  }
               updateActivityModeDuration(CmnhConstants.ActivityMode.IDLE, elapsedTimeSinceLastEvent);

               // The idle screen being hidden means we're back in ACTIVE mode.  Furthermore,
               // this is the ONLY way we can get into ACTIVE mode.
               activityMode = CmnhConstants.ActivityMode.ACTIVE;
               break;
            }
         }

      private void updateActivityModeDuration(final CmnhConstants.ActivityMode mode, final long elapsedTimeSinceLastEvent)
         {
         activityModeDurations.put(mode, activityModeDurations.get(mode) + elapsedTimeSinceLastEvent);
         }

      private void incrementActivityModeCounter(final CmnhConstants.ActivityMode mode)
         {
         activityModeCounts.put(mode, activityModeCounts.get(mode) + 1);
         }

      private void incrementInteractionEventCounter(final EventType eventType)
         {
         interactionEventCounts.put(eventType, interactionEventCounts.get(eventType) + 1);
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

         final DateTime currentDateJoda = new DateTime(currentDate, CmnhConstants.CMNH_TIME_ZONE);
         final DateTime timeOfFirstEventJoda = new DateTime(timeOfEarliestEvent, CmnhConstants.CMNH_TIME_ZONE);
         final DateTime timeOfLastEventJoda = new DateTime(timeOfLatestEvent, CmnhConstants.CMNH_TIME_ZONE);

         sb.append(dateTimeFormatter.print(currentDateJoda)).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(dateTimeFormatter.print(timeOfFirstEventJoda)).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(dateTimeFormatter.print(timeOfLastEventJoda)).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(currentDate).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(timeOfEarliestEvent).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(timeOfLatestEvent).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(timeOfLatestEvent - timeOfEarliestEvent).append(CsvOutputEventProcessor.FIELD_DELIMITER);

         sb.append(activityModeDurations.get(CmnhConstants.ActivityMode.INIT)).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(activityModeDurations.get(CmnhConstants.ActivityMode.ACTIVE)).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(activityModeDurations.get(CmnhConstants.ActivityMode.IDLE)).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(activityModeDurations.get(CmnhConstants.ActivityMode.UNKNOWN)).append(CsvOutputEventProcessor.FIELD_DELIMITER);

         sb.append(activityModeCounts.get(CmnhConstants.ActivityMode.INIT)).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(activityModeCounts.get(CmnhConstants.ActivityMode.ACTIVE)).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(activityModeCounts.get(CmnhConstants.ActivityMode.IDLE)).append(CsvOutputEventProcessor.FIELD_DELIMITER);

         sb.append(numThemeSelections).append(CsvOutputEventProcessor.FIELD_DELIMITER);
         sb.append(numInterestPointSelections).append(CsvOutputEventProcessor.FIELD_DELIMITER);

         for (final String themeId : THEME_IDS)
            {
            sb.append(themeSelectionCounts.get(themeId)).append(CsvOutputEventProcessor.FIELD_DELIMITER);
            }

         for (final String interestPointId : INTEREST_POINT_IDS)
            {
            sb.append(interestPointSelectionCounts.get(interestPointId)).append(CsvOutputEventProcessor.FIELD_DELIMITER);
            }

         for (final EventType eventType : INTERACTION_EVENTS)
            {
            sb.append(interactionEventCounts.get(eventType)).append(CsvOutputEventProcessor.FIELD_DELIMITER);
            }

         for (final String mediaFile : MEDIA_FILES)
            {
            sb.append(mediaPlayCounts.get(mediaFile)).append(CsvOutputEventProcessor.FIELD_DELIMITER);
            }

         println(sb.toString());
         }

      private void resetStats(final Long date)
         {
         currentDate = date;

         timeOfEarliestEvent = null;
         timeOfLatestEvent = null;

         numInterestPointSelections = 0;
         numThemeSelections = 0;

         for (final String interestPointId : INTEREST_POINT_IDS)
            {
            interestPointSelectionCounts.put(interestPointId, 0);
            }
         for (final String themeId : THEME_IDS)
            {
            themeSelectionCounts.put(themeId, 0);
            }
         for (final CmnhConstants.ActivityMode mode : CmnhConstants.ActivityMode.values())
            {
            activityModeCounts.put(mode, 0);
            activityModeDurations.put(mode, 0L);
            }
         for (final EventType eventType : INTERACTION_EVENTS)
            {
            interactionEventCounts.put(eventType, 0);
            }
         for (final String mediaFile : MEDIA_FILES)
            {
            mediaPlayCounts.put(mediaFile, 0);
            }

         activityMode = CmnhConstants.ActivityMode.UNKNOWN;
         }
      }
   }
