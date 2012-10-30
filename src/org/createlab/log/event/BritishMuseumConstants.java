package org.createlab.log.event;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTimeZone;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class BritishMuseumConstants
   {
   protected static enum ActivityMode
      {
         INIT, ACTIVE, IDLE, ERROR, UNKNOWN
      }

   static final DateTimeZone GMT_TIME_ZONE = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+0"));
   static final String EVENT_INIT_BEGIN = "init-begin";
   static final String EVENT_INIT_DONE = "init-done";
   static final String EVENT_TOUR_BEGIN = "tour-begin";
   static final String EVENT_TOUR_CHANGE_SNAPSHOT = "tour-change-snapshot";
   static final String EVENT_TOUR_END = "tour-end";
   static final String EVENT_IDLE_BEGIN = "idle-begin";
   static final String EVENT_IDLE_CHANGE_PANO = "idle-change-pano";
   static final String EVENT_IDLE_END = "idle-end";
   static final String EVENT_FLASH_WATCHDOG_ERROR_COUNT_CHANGE = "flash-watchdog-error-count-change";
   static final String EVENT_FLASH_WATCHDOG_ERROR_DETECTED = "flash-watchdog-error-detected";
   static final String EVENT_FLASH_WATCHDOG_FORCE_BROWSER_RELOAD = "flash-watchdog-force-browser-reload";

   static final Map<String, ActivityMode> EVENT_TYPE_ACTIVITY_MODES;

   static
      {
      final Map<String, ActivityMode> eventTypeActivityModes = new HashMap<String, ActivityMode>();
      eventTypeActivityModes.put(EVENT_INIT_BEGIN, ActivityMode.INIT);
      eventTypeActivityModes.put(EVENT_INIT_DONE, ActivityMode.INIT);

      eventTypeActivityModes.put(EVENT_TOUR_BEGIN, ActivityMode.ACTIVE);
      eventTypeActivityModes.put(EVENT_TOUR_CHANGE_SNAPSHOT, ActivityMode.ACTIVE);
      eventTypeActivityModes.put(EVENT_TOUR_END, ActivityMode.ACTIVE);

      eventTypeActivityModes.put(EVENT_IDLE_BEGIN, ActivityMode.IDLE);
      eventTypeActivityModes.put(EVENT_IDLE_CHANGE_PANO, ActivityMode.IDLE);
      eventTypeActivityModes.put(EVENT_IDLE_END, ActivityMode.IDLE);

      eventTypeActivityModes.put(EVENT_FLASH_WATCHDOG_ERROR_COUNT_CHANGE, ActivityMode.ERROR);
      eventTypeActivityModes.put(EVENT_FLASH_WATCHDOG_ERROR_DETECTED, ActivityMode.ERROR);
      eventTypeActivityModes.put(EVENT_FLASH_WATCHDOG_FORCE_BROWSER_RELOAD, ActivityMode.ERROR);

      EVENT_TYPE_ACTIVITY_MODES = Collections.unmodifiableMap(eventTypeActivityModes);
      }

   @NotNull
   static final SupportedEventTypes SUPPORTED_EVENT_TYPES;

   static
      {
      SUPPORTED_EVENT_TYPES = new SupportedEventTypes();
      SUPPORTED_EVENT_TYPES.registerEventType(EVENT_INIT_BEGIN);
      SUPPORTED_EVENT_TYPES.registerEventType(EVENT_INIT_DONE);

      SUPPORTED_EVENT_TYPES.registerEventType(EVENT_TOUR_BEGIN, Arrays.asList("pano"));
      SUPPORTED_EVENT_TYPES.registerEventType(EVENT_TOUR_CHANGE_SNAPSHOT, Arrays.asList("pano"));
      SUPPORTED_EVENT_TYPES.registerEventType(EVENT_TOUR_END, Arrays.asList("pano"));

      SUPPORTED_EVENT_TYPES.registerEventType(EVENT_IDLE_BEGIN);
      SUPPORTED_EVENT_TYPES.registerEventType(EVENT_IDLE_CHANGE_PANO, Arrays.asList("pano"));
      SUPPORTED_EVENT_TYPES.registerEventType(EVENT_IDLE_END);

      SUPPORTED_EVENT_TYPES.registerEventType(EVENT_FLASH_WATCHDOG_ERROR_DETECTED);
      SUPPORTED_EVENT_TYPES.registerEventType(EVENT_FLASH_WATCHDOG_ERROR_COUNT_CHANGE, Arrays.asList("count"));
      SUPPORTED_EVENT_TYPES.registerEventType(EVENT_FLASH_WATCHDOG_FORCE_BROWSER_RELOAD);
      }

   private BritishMuseumConstants()
      {
      // private to prevent instantiation
      }
   }
