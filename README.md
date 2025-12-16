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
