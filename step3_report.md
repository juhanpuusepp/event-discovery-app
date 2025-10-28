# Which API was chosen and why
We integrated the Nominatim (OpenStreetMap) Search API for place/address autocomplete and geocoding (returns lat/lon).  

Why we chose this:
- It's public and no API key
- It supports country/language biasing and map-bounded searches, so we can focus on Estonia
- It returns coordinates (lat/lon) for places/addresses so we can pin events on the map

# Example API endpoint used

```
https://nominatim.openstreetmap.org/search
  ?q=Genialistide%20Klubi
  &format=json
  &addressdetails=1
  &limit=10
  &countrycodes=ee
  &accept-language=et
  &viewbox=21.5,59.8,28.2,57.3
  &bounded=1
```

- `countrycodes=ee` and `accept-language=et` bias toward Estonia and Estonian labels
- `viewbox` and `bounded=1` restrict results to an Estonia bounding box for relevance and performance

# Error handling strategy
