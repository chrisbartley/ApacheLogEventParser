HOW TO USE
----------

First, do a build:

   $ ant

Now parse the Apache access.log with the BritishMuseumEventLogProducer, to generate the event-log.csv file:

   $ java -jar dist/event-parser.jar org.createlab.log.event.BritishMuseumEventLogProducer /Users/chris/Downloads/BritishMuseumApacheLogs/logs/access.log

Next, we need to sort the event-log.csv file:

   $ sort -g event-log.csv > event-log-sorted.csv

Now run the BritishMuseumStatsGenerator on the sorted event log to produce the stats:

   $ java -jar dist/event-parser.jar org.createlab.log.event.BritishMuseumStatsGenerator event-log-sorted.csv

You can now upload the event log and stats table to Fusion Tables, or whatever.