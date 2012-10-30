package org.createlab.log.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class SupportedEventTypes
   {
   private static final List<String> EMPTY_LIST = Collections.unmodifiableList(new ArrayList<String>());

   @NotNull
   private final Map<String, List<String>> parameterNamesByEventType = new HashMap<String, List<String>>();

   public void registerEventType(@NotNull final String eventType)
      {
      parameterNamesByEventType.put(eventType, EMPTY_LIST);
      }

   public void registerEventType(@NotNull final String eventType, @NotNull final List<String> parameterList)
      {
      parameterNamesByEventType.put(eventType, Collections.unmodifiableList(new ArrayList<String>(parameterList)));
      }

   public boolean isEventTypeSupported(@Nullable final String eventType)
      {
      if (eventType != null)
         {
         return parameterNamesByEventType.containsKey(eventType);
         }
      return false;
      }

   @NotNull
   public List<String> getParameterList(@Nullable final String eventType)
      {
      if (eventType != null && isEventTypeSupported(eventType))
         {
         return parameterNamesByEventType.get(eventType);
         }
      return EMPTY_LIST;
      }

   public Set<String> getSupportedEventTypeNames()
      {
      return Collections.unmodifiableSet(parameterNamesByEventType.keySet());
      }

   @Override
   public boolean equals(final Object o)
      {
      if (this == o)
         {
         return true;
         }
      if (o == null || getClass() != o.getClass())
         {
         return false;
         }

      final SupportedEventTypes that = (SupportedEventTypes)o;

      if (!parameterNamesByEventType.equals(that.parameterNamesByEventType))
         {
         return false;
         }

      return true;
      }

   @Override
   public int hashCode()
      {
      return parameterNamesByEventType.hashCode();
      }
   }
