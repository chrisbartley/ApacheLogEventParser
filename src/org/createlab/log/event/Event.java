package org.createlab.log.event;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class Event implements Comparable<Event>
   {
   @NotNull
   private final String type;

   @NotNull
   private final Long timeInMillis;

   private final long dateInMillis;

   @NotNull
   private final Map<String, String> params = new HashMap<String, String>();

   Event(@NotNull final String type, final long timeInMillis, @NotNull final Map<String, String> params)
      {
      this.type = type;
      this.timeInMillis = timeInMillis;
      final Calendar dateTime = new GregorianCalendar();
      dateTime.setTimeInMillis(timeInMillis);
      this.dateInMillis = new GregorianCalendar(dateTime.get(Calendar.YEAR),
                                                dateTime.get(Calendar.MONTH),
                                                dateTime.get(Calendar.DAY_OF_MONTH)).getTimeInMillis();
      this.params.putAll(params);
      }

   /**
    * Compares only the time and the type, assuming that there can never be two events of the same type at the same time.
    */
   @Override
   public int compareTo(final Event event)
      {
      final int timeComparison = timeInMillis.compareTo(event.timeInMillis);
      if (timeComparison != 0)
         {
         return timeComparison;
         }

      final int typeComparison = type.compareTo(event.type);
      if (typeComparison != 0)
         {
         return typeComparison;
         }

      return 0;
      }

   @NotNull
   public String getType()
      {
      return type;
      }

   public long getTimeInMillis()
      {
      return timeInMillis;
      }

   public long getDateInMillis()
      {
      return dateInMillis;
      }

   @Nullable
   public String getParameterValue(@NotNull final String parameterName)
      {
      return params.get(parameterName);
      }

   /**
    * Returns a {@link String} representation of the <code>Event</code>, as a series of fields separated by the given
    * <code>fieldDelimiter</code>, but containing only the time in milliseconds and the parameters specified by the
    * given <code>parameterNames</code>.  The event type is NOT included in this representation.
    */
   @NotNull
   public String toString(@NotNull final String fieldDelimiter, @Nullable final List<String> parameterNames)
      {
      final StringBuilder sb = new StringBuilder(String.valueOf(timeInMillis));
      if (parameterNames != null)
         {
         for (final String key : parameterNames)
            {
            final String val = getParameterValue(key);
            sb.append(fieldDelimiter).append(val);
            }
         }
      return sb.toString();
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

      final Event event = (Event)o;

      if (!params.equals(event.params))
         {
         return false;
         }
      if (!timeInMillis.equals(event.timeInMillis))
         {
         return false;
         }
      if (!type.equals(event.type))
         {
         return false;
         }

      return true;
      }

   @Override
   public int hashCode()
      {
      int result = type.hashCode();
      result = 31 * result + timeInMillis.hashCode();
      result = 31 * result + params.hashCode();
      return result;
      }

   @Override
   public String toString()
      {
      final StringBuilder sb = new StringBuilder();
      sb.append("Event");
      sb.append("{type='").append(type).append('\'');
      sb.append(", timeInMillis=").append(timeInMillis);
      sb.append(", params=").append(params);
      sb.append('}');
      return sb.toString();
      }
   }
