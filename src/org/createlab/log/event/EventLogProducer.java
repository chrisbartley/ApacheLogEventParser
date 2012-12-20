package org.createlab.log.event;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class EventLogProducer extends BaseApacheLogLineProcessor
   {
   @NotNull
   private final EventProcessor eventProcessor;

   @NotNull
   private final Map<String, Integer> countsByType = new HashMap<String, Integer>();

   @NotNull
   private final SupportedEventTypes supportedEventTypes;

   EventLogProducer(@NotNull final SupportedEventTypes supportedEventTypes)
      {
      this.supportedEventTypes = supportedEventTypes;
      eventProcessor = new EventLogOutputEventProcessor(supportedEventTypes);
      for (final String eventType : this.supportedEventTypes.getSupportedEventTypeNames())
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
   protected final void processEvent(@NotNull final Event event)
      {
      if (this.supportedEventTypes.getSupportedEventTypeNames().contains(event.getType()))
         {
         countsByType.put(event.getType(), countsByType.get(event.getType()) + 1);
         eventProcessor.processEvent(event);
         }
      else
         {
         System.err.println("ERROR: Unexpected event type [" + event.getType() + "].  Event: " + event);
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

   private static final class EventLogOutputEventProcessor extends CsvOutputEventProcessor
      {
      @NotNull
      private static final File FILE = new File("event-log.csv");
      private final SupportedEventTypes supportedEventTypes;

      private EventLogOutputEventProcessor(final SupportedEventTypes supportedEventTypes)
         {
         super(FILE);
         this.supportedEventTypes = supportedEventTypes;
         }

      @Override
      protected void appendEventParameters(@NotNull final Event event, @NotNull final StringBuilder stringBuilder)
         {
         final List<String> parameterList = this.supportedEventTypes.getParameterList(event.getType());
         if (!parameterList.isEmpty())
            {
            // We know there's never more than one because we defined them above
            final String parameterName = parameterList.get(0);

            // no need to append the field delimiter first since the superclass has already done it for us
            stringBuilder.append(event.getParameterValue(parameterName));
            }
         }
      }
   }
