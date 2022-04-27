# easyTeamUp

Easy Team Up is an Android mobile application designed to help users
schedule events.

## Environment Setup

Easy Team Up was tested on an emulated Pixel XL device with Android
API 29. Use this device and an Android API of 29 or higher to run the
application.

## API Keys

To get the Google Maps functionalities, add Google Maps API key to the
local.properties file in the Gradle Scripts folder. The line should be
similar to:
`MAPS_API_KEY=AIabcdefghijklmnop`.

## Running the App

To run the app, first launch AVD in the emulator. Then, build the app and
run it from Android Studio.

## Changes made during the Sprint

### Search bar
- A search bar should be added in the Create Event Page for when adding guests to the invite list so that users are not expected to know everyone else’s usernames

### User can properly manage their engagement with events
- Guests can select time slots. Currently, available time slots are not visible and therefore not selectable. 
- Create a guest list table. 

### Map Activity
- Only public events and private events that a user has been invited to should show up on the map. 
- Make the map view zoom to the user’s location upon opening. Requires updating all tests’ hardcoded data, which also spurs motivation to place hardcoded test data into variables.

### Messages
- Receiving an invite should result in receiving a message

### Profile Page
- When you select a timeslot, the corresponding event should be added to your upcoming events
- Remove duplicates from the upcoming events so that multiple timeslots selected by a user do not cause multiple repetitions of the event name to appear
- Add an Edit Profile page to allow users to change their profile username and password
- New sign ups should have their username checked for uniqueness before being added as a new user

### Event Page Activity
- Currently, only events with guest lists (which are automatically marked as private) allow users to see a detailed view of the event (allowing withdrawal).
- All public events and private events a guest is invited to should allow selection of timeslots by a user.
- Events with guest lists should not automatically be marked as private. 
- The code can be refactored so that all base information is displayed by default and only additional options (withdrawing, editing the event, signing up for a timeslot) are conditioned on the user and event status.
- Add current signed-up guests list printout to host view of event page (I.e. all guests who selected a time should show up)

### Create Event Activity
- Currently, you can select both the public and private event bubbles at the same time. It should only be possible to select one.
- Add printout of running list of invited guests

### UI
- Improve TextView UI so that clickable objects are more clearly clickable
- All times coming from the database should be unpacked into readable date-times. If times are edited by a host, they should be properly repacked.

### Testing
- Improve testing assurance by deleting database contents on setup of new set of instrumented tests.



