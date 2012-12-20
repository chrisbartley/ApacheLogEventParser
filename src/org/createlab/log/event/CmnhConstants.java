package org.createlab.log.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTimeZone;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class CmnhConstants
   {
   static final DateTimeZone GMT_TIME_ZONE = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+0"));
   static final DateTimeZone CMNH_TIME_ZONE = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT-5"));

   protected static enum ActivityMode
      {
         INIT, ACTIVE, IDLE, UNKNOWN
      }

   public static enum EventType
      {
         INIT_START("init-start", ActivityMode.INIT, null),
         INIT_FINISH("init-finish", ActivityMode.INIT, null),

         MEDIA_PANEL_CLOSE("media-panel-close", ActivityMode.ACTIVE, null),
         MEDIA_PLAY("media-play", ActivityMode.ACTIVE, Arrays.asList("url")),
         MEDIA_PAUSE("media-pause", ActivityMode.ACTIVE, Arrays.asList("url")),
         MEDIA_ENDED("media-ended", ActivityMode.ACTIVE, Arrays.asList("url")),
         MEDIA_TIME_CHANGED("media-time-changed", ActivityMode.ACTIVE, Arrays.asList("url")),

         NAV_THEME_CHANGE("nav-theme-change", ActivityMode.ACTIVE, Arrays.asList("new")),
         NAV_INTEREST_POINT("nav-interest-point", ActivityMode.ACTIVE, Arrays.asList("id")),
         NAV_TAP("nav-tap", ActivityMode.ACTIVE, null),
         NAV_RESET_HOME_VIEW("nav-reset-home-view", ActivityMode.ACTIVE, null),
         NAV_MOVE_START("nav-move-start", ActivityMode.ACTIVE, null),
         NAV_MOVE_FINISH("nav-move-finish", ActivityMode.ACTIVE, null),

         THEMES_PANEL_DRAWER_OPEN("themes-panel-drawer-open", ActivityMode.ACTIVE, null),
         THEMES_PANEL_DRAWER_CLOSE("themes-panel-drawer-close", ActivityMode.ACTIVE, null),

         IDLE_SCREEN_VISIBLE("idle-screen-visible", ActivityMode.IDLE, null),
         IDLE_SCREEN_HIDDEN("idle-screen-hidden", ActivityMode.IDLE, null);

      @NotNull
      private final String name;

      @NotNull
      private final ActivityMode activityMode;

      @Nullable
      private final List<String> paramNames;

      private static final Map<String, EventType> EVENT_TYPES_BY_NAME;

      static
         {
         final Map<String, EventType> eventTypesByName = new HashMap<String, EventType>(EventType.values().length);
         for (final EventType eventType : EventType.values())
            {
            eventTypesByName.put(eventType.getName(), eventType);
            }
         EVENT_TYPES_BY_NAME = Collections.unmodifiableMap(eventTypesByName);
         }

      @Nullable
      public static EventType findByName(@Nullable final String name)
         {
         return EVENT_TYPES_BY_NAME.get(name);
         }

      public static boolean isSupportedType(@Nullable final String name)
         {
         return findByName(name) != null;
         }

      EventType(@NotNull final String name, @NotNull final ActivityMode activityMode, @Nullable final List<String> paramNames)
         {
         this.name = name;
         this.activityMode = activityMode;
         this.paramNames = (paramNames == null) ? null : new ArrayList<String>(paramNames);
         }

      @NotNull
      public String getName()
         {
         return name;
         }

      @NotNull
      public ActivityMode getActivityMode()
         {
         return activityMode;
         }

      @Nullable
      public List<String> getParameterNames()
         {
         return (paramNames == null) ? null : Collections.unmodifiableList(paramNames);
         }
      }

   @NotNull
   static final SupportedEventTypes SUPPORTED_EVENT_TYPES;

   static
      {
      SUPPORTED_EVENT_TYPES = new SupportedEventTypes();

      for (final EventType eventType : EventType.values())
         {
         final List<String> paramNames = eventType.getParameterNames();
         if (paramNames == null)
            {
            SUPPORTED_EVENT_TYPES.registerEventType(eventType.getName());
            }
         else
            {
            SUPPORTED_EVENT_TYPES.registerEventType(eventType.getName(), paramNames);
            }
         }
      }

   private CmnhConstants()
      {
      // private to prevent instantiation
      }
   }
