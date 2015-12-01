Todo
========
*This is (approximately) a priority order*

### Main features (or important things!)
 - Add the extract of the Wikipedia article in the POI CardView
 - Improve the way the map works, so it returns to a saved state if we have already located ourselves and come back from another screen (for now it restarts the whole process, even sometimes failing)
 - Add preferences for the user (default language, default range/#POI, preferred POIs like museums/historical places/landscape)
 - Add routing between points, to guide the user. Some functions are already ready in Routing.java, but we have to get a better pedestrian routing (tourists are often on foot)

### Improvements
 - Improve the GPS positioning, once we have located the user he is tracked premanently (and the battery melts...)
 - Improve the map with custom icons for each POI type (see the [WikiJourney repo](https://github.com/WikiJourney/wikijourney_website/) for this)
 - Rework the main parameters (range and # of POIs), this makes little sense since a huge town will be crowded from POIs. Maybe make some profiles, like "Big town" (1km/20POIs), "Lost place" (20km/10POIs)...
 - Rework the home screen UI, so it looks better on smaller screens (and better in general too!)

### Structure of the app (experimented coders are welcome!)
 - General cleanup and refactoring of the code
 - Write tests
 - Rething the app architecture, should we keep Fragments everywhere, or change some screen to Activities?