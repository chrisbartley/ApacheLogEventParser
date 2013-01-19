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

---------------------------------------------------------------------
PROCESSING THE CMNH LOGS
---------------------------------------------------------------------
$ java -jar dist/event-parser.jar org.createlab.log.event.LogFileProcessor /Users/chris/Downloads/CMNHApacheLogs/access.log --list-event-types
16:41:50,327 [main] DEBUG: LogFileProcessor.printTypes(/Users/chris/Downloads/CMNHApacheLogs/access.log)
Number of lines processed       = 103050
idle-screen-hidden
idle-screen-visible
init-finish
init-start
media-ended
media-panel-close
media-pause
media-play
media-time-changed
nav-interest-point
nav-move-finish
nav-move-start
nav-reset-home-view
nav-tap
nav-theme-change
themes-panel-drawer-close
themes-panel-drawer-open

$ java -jar dist/event-parser.jar org.createlab.log.event.CmnhEventLogProducer /Users/chris/Downloads/CMNHApacheLogs/access.log

Found [10819] events for type [nav-move-finish]
Found [345] events for type [media-panel-close]
Found [563] events for type [idle-screen-visible]
Found [174] events for type [init-finish]
Found [326] events for type [media-pause]
Found [1324] events for type [nav-theme-change]
Found [2524] events for type [nav-reset-home-view]
Found [1866] events for type [nav-interest-point]
Found [128] events for type [media-ended]
Found [949] events for type [themes-panel-drawer-close]
Found [326] events for type [media-play]
Found [1030] events for type [themes-panel-drawer-open]
Found [126] events for type [media-time-changed]
Found [10835] events for type [nav-move-start]
Found [174] events for type [init-start]
Found [529] events for type [idle-screen-hidden]
Found [1577] events for type [nav-tap]

$ sort -g event-log.csv > event-log-sorted.csv

Now manually strip out all occurences of the following from event-log-sorted.csv:
* http%3A%2F%2F127.0.0.1%2Fmedia%2F46684%2Faudio%2F
* http%3A%2F%2F127.0.0.1%2Fmedia%2F46684%2Fvideo%2F
* http%3A%2F%2Flocalhost%2Fmedia%2F46684%2Faudio%2F
* http%3A%2F%2Flocalhost%2Fmedia%2F46684%2Fvideo%2F

$ java -jar dist/event-parser.jar org.createlab.log.event.CmnhStatsGenerator event-log-sorted.csv
