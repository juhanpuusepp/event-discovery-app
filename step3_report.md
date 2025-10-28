# Which API was chosen and why
Nominatim (OpenStreetMap) Search API for place/address autocomplete and geocoding (returns lat/lon)  
Why - free, no API key, good coverage in Estonia, can bias results by country/language and even by a bounding box. Perfect fit for our Add Event flow where users type an address and we need coordinates for map markers.

# Example API endpoint used


# Error handling strategy
