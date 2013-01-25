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
Start by editing the access.log file and stripping out all of log records prior to 2012-11-20.  Then do the following...

$ java -jar dist/event-parser.jar org.createlab.log.event.LogFileProcessor /Users/chris/Downloads/CMNHApacheLogs/access.log --list-event-types
16:41:50,327 [main] DEBUG: LogFileProcessor.printTypes(/Users/chris/Downloads/CMNHApacheLogs/access.log)
Number of lines processed       = 208663
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

Found [23727] events for type [nav-move-finish]
Found [759] events for type [media-panel-close]
Found [1199] events for type [idle-screen-visible]
Found [100] events for type [init-finish]
Found [772] events for type [media-pause]
Found [3242] events for type [nav-theme-change]
Found [5994] events for type [nav-reset-home-view]
Found [4428] events for type [nav-interest-point]
Found [316] events for type [media-ended]
Found [2188] events for type [themes-panel-drawer-close]
Found [772] events for type [media-play]
Found [2374] events for type [themes-panel-drawer-open]
Found [266] events for type [media-time-changed]
Found [23730] events for type [nav-move-start]
Found [100] events for type [init-start]
Found [1308] events for type [idle-screen-hidden]
Found [4993] events for type [nav-tap]

$ sort -g event-log.csv > event-log-sorted.csv

Now manually strip out all occurences of the following from event-log-sorted.csv:
* http%3A%2F%2F127.0.0.1%2Fmedia%2F46684%2Faudio%2F
* http%3A%2F%2F127.0.0.1%2Fmedia%2F46684%2Fvideo%2F
* http%3A%2F%2Flocalhost%2Fmedia%2F46684%2Faudio%2F
* http%3A%2F%2Flocalhost%2Fmedia%2F46684%2Fvideo%2F

$ java -jar dist/event-parser.jar org.createlab.log.event.CmnhStatsGenerator event-log-sorted.csv
17:46:48,790 [main] DEBUG: LogFileProcessor.parse(event-log-sorted.csv)
Found [100] events for type [INIT_START]
Found [23727] events for type [NAV_MOVE_FINISH]
Found [266] events for type [MEDIA_TIME_CHANGED]
Found [1199] events for type [IDLE_SCREEN_VISIBLE]
Found [23730] events for type [NAV_MOVE_START]
Found [772] events for type [MEDIA_PLAY]
Found [100] events for type [INIT_FINISH]
Found [3242] events for type [NAV_THEME_CHANGE]
Found [2188] events for type [THEMES_PANEL_DRAWER_CLOSE]
Found [4993] events for type [NAV_TAP]
Found [316] events for type [MEDIA_ENDED]
Found [1308] events for type [IDLE_SCREEN_HIDDEN]
Found [759] events for type [MEDIA_PANEL_CLOSE]
Found [4428] events for type [NAV_INTEREST_POINT]
Found [5994] events for type [NAV_RESET_HOME_VIEW]
Found [772] events for type [MEDIA_PAUSE]
Found [2374] events for type [THEMES_PANEL_DRAWER_OPEN]
