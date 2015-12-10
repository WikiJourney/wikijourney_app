Todo
========
*This is (approximately) a priority order*

### Main (easy?) features (or important things!)
 - ~~Add the extract of the Wikipedia article in the POI CardView~~ Done; should be checked
 - Improve the way the map works, so it returns to a saved state if we have already located ourselves and come back from another screen (for now it restarts the whole process, even sometimes failing)
 - Filter through the POIs (default filter to get rid of subway stations or other things referenced in Wikipedia but not very culturally useful)
 - Add preferences for the user (default language, default range/#POI, preferred POIs like museums/historical places/landscape)
 - Add routing between points, to guide the user. Some functions are already ready in Routing.java, but we have to get a better pedestrian routing (tourists are often on foot)
 - Allow manual reordering of the POI list, so the routing follows the needs of the tourist (and we don't need to find the shortest/best path :p)

### Completely new features (hard ones?)
 - Allow offline work (download of the map tiles & Wikipedia articles); very useful if in a foreign country (expensive data...)
 - Integrate user accounts (using Wikipedia OAuth?), so users can log in on the website, prepare their travel, and retrieve it on the phone

### Improvements
 - Translations!
 - Make the app search for POIs in the user language
 - Improve the GPS positioning, once we have located the user he is tracked permanently (and the battery melts...)
 - Improve the map with custom icons for each POI type (see the [WikiJourney website repo](https://github.com/WikiJourney/wikijourney_website/) for this)
 - Rework the main parameters (range and # of POIs), this makes little sense since a huge town will be crowded from POIs. Maybe make some profiles, like "Big town" (1km/20POIs), "Lost in the desert" (20km/10POIs)...
 - Rework the home screen UI, so it looks better on smaller screens (and better in general too!)

### Structure of the app (experimented coders are welcome!)
 - General cleanup and refactoring of the code
 - Write tests
 - Rethink the app architecture, should we keep Fragments everywhere, or change some screen to Activities?