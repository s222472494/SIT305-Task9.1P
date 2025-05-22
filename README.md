# SIT305-Task7.1P

# Lost & Found App

This Android app connects lost items with their owners. Users can report lost or found items, store location data, and remove listings once items are returned.

## Features

- **Item Listings**: Post lost/found items with descriptions and location details.
- **View All Listings**: View all lost and found posts in a scrollable list.
- **Item Removal**: Remove items once they have been returned to their owners.
- **SQLite Database**: Store item data locally on the device.
- **Geo-Location Integration**:
  - **Location Autocomplete**: Users can tap the location field to search and select an address using Google Places Autocomplete.
  - **Get Current Location**: Users can tap the "GET CURRENT LOCATION" button to automatically detect and use their current location.
  - **Map View**: Tapping the "SHOW ON MAP" button displays all lost and found item locations on a Google Map, marked with pins for each item's location.

## Tech Stack

- **Language**: Java
- **Platform**: Android SDK
- **Database**: SQLite for local data storage
- **UI Components**: Material Buttons, TextViews, RecyclerViews, LinearLayouts, Google Maps
- **APIs Used**:
  - Google Maps SDK for Android
  - Google Places API
  - Google Location Services API

## Screens/Wireframes Implemented

- **Home Page**: Allows navigation to post/view listings or view all items on a map.
- **Post Item Page**: Lets the user fill in item details and select a location via autocomplete or current location.
- **Map Page**: Displays all posted lost and found items as markers on a Google Map.

## Setup Instructions

1. Clone this repository.
2. Add your Google Maps and Places API key in the `AndroidManifest.xml`, 'CreateAdvertAcitivity.java' and 'String.xml'.
3. Build and run the app on an Android device or emulator.
4. Ensure location permissions are granted.
