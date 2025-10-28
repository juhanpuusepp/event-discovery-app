# Which API was chosen and why
We integrated the Nominatim (OpenStreetMap) Search API for place/address autocomplete and geocoding (returns lat/lon).  

Why we chose this:
- It's public and doesn't require an API key
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
What can go wrong:
- 503 Service Unavailable when the server is overloaded or a backend query times out. Nominatim's own config docs note that long queries may be canceled and returned as 503.
- Exceeding rate limits or aggressive usage can trigger throttling.  

What we implemented in the app:
1. Debounce (800 ms) while typing before each request (keeps load ~≤1 req/sec).
2. OkHttp timeouts (connect/read/write) so the UI doesn't hang on poor networks. Exceptions surface to the UI.
3. One-time retry with short backoff on 503/429 to smooth over brief overloads/rate-limit hiccups.
4. Geographic biasing: we pass `countrycodes=ee`, `accept-language=et` and Estonia viewbox with optional `bounded=1` to narrow results.
5. User feedback in UI: loading spinner while fetching. On errors we show "No results" or a short error message instead of crashing and allow the user to keep editing.
6. Compliant identification: every request includes a custom User-Agent header as required by the policy.
