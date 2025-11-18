 ## Testing strategy 
#### Unit tesing
Unit tests for EventViewModel functions aka how the UI communicates with the backend. We tested adding an event, deleting an event and clearing place suggestions. The tests were written using jUnit and mockk. 
We mocked the repository and the Nominatim API, and created a fake event to run the tests.

#### UI testing
A UI test was written for the Add Event Screen. For this we mocked the viewModel and its state, and simulating filling out the form for adding a new event. The test passes once the "Save event" button is clicked and the event has been added to the database.
 
## Build process for APK  


## Known bugs or limitations
