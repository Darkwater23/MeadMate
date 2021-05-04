# Mead Mate
Mead Mate is a HTML5 / jQuery Mobile single-page web app wrapped in a WebView for Android. The app uses the built-in Sqlite capabilities of Android.

The scope of the app is specifically limited to mead and mead makers. Originally, the app was going to be a more general homebrewing app, but there were so many variables involved when wine & beer were considered. A more focused scope is easier to maintain.

The primary function of this app is to help users manage the progress of their mead batches. There are currently three primary entities in the project: Meads, Events and Readings.

The Mead entity represents the mead batch when it is first created. It has a name, a start date and an initial gravity reading. The other entities in the project all need to be related to a primary mead entity.

The Event entities are used to indicate steps in the mead making process, like primary fermentation, secondary fermentation, bottling, etc. A general note Event was added to give the user some latitude in defining meaningful events. Events are many-to-one Mead entity.

The Reading entity represents a specific gravity reading on a particular date. Readings are many-to-one Mead entity.

There is also a tagging feature under development. Tags are simple strings that are related many-to-many Meads and will be used for filtering and other features later.

Need to link to other files from here...
