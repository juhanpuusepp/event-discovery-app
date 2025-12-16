# EVNTLY – Event Discovery App
Discover events around you through a map-based interface, and as an organiser, add your own. The user can view nearby events filtered by categories. By clicking on an event, users can obtain more information about it. Additionally, a calendar view allows browsing upcoming events by date. The platform supports micro-payments for paid events, enabling users to purchase tickets directly within the app. Certified accounts can add their own events.

The app is written in **Kotlin language**, in **Android Studio** IDE. It is compiled and run by **Gradle**, with the UI being created by using the modern toolkit **Jetpack Compose**.

## Current Setup Instructions
APK available

## Team Members
Juhan Puusepp (juhanpuusepp) - Project Manager / Developer / Presenter

Karl Laine (LaineKarl) - Lead Developer

Gerda Jäe (gerdajj) - Researcher / Developer

Beatrice Hellrand (hellrand) - Editor / Developer

Evert Saarnak (s-evert) - Developer

## Features
Data & Persistence - events and their data is stored locally using Room DB

UI & Navigation - a map interface, muti-touch actions, animations, light and dark mode, Material icons

API Integration - location-based services to pinpoint the users location and to generate a map, API to autocomplete addresses in search

User Login and Profiling - user sign up, login and profile storage with Firebase Authentication

## Current Tools, frameworks, libraries, APIs
Room, Retrofit + Moshi, Google Maps API, Nominatim API, Firebase Authentication

Lightmode map style: The X-Spot location map by Ruben (https://snazzymaps.com/style/287702/the-x-spot-location-map)

Darkmode map style: Dark by Roy (https://snazzymaps.com/style/71079/dark)

## Planned Tools, frameworks, libraries, APIs
Dagger


## User Guide

Installing the App:
* Download the APK file from the repository (/apk folder) to your Android phone.
* Open the downloaded APK file.
* If prompted, allow installation from unknown sources.
* Complete the installation.

Launching the App:
* After installation, open the app.
* Make sure location services are enabled and the internet service is provided.

Navigating the App:
* First create an account if you do not have one already.
* The app opens on the main screen, where events are displayed on a map.
* Use the map to explore nearby events.
* Tap on an event marker to view event details.
* Tap the "+" icon to add a new event
* Use navigation icon on the top left to open menu drawer to access Profile view, Events list view and enable/disable dark/light mode.
* Log out from Profile view
