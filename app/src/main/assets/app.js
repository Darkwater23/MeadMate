/*
This file is part of Mead Mate.

Mead Mate is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Mead Mate is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Mead Mate.  If not, see <https://www.gnu.org/licenses/>.
*/

const abvPrefKeyName = 'ABVPREF';
const sortPrefKeyName = 'SORTPREF';
const specificGravityRange = [0.700, 2.000];
const archivePrefKeyName = 'INCLUDE_ARCHIVED';
const dateFormatPrefKeyName = 'DATEFORMATPREF';
const themeModePrefKeyName = 'THEMEPREF';



// One-time code executions
var tagList;
var confirmTheme = 'light'; // default value

$(function() {

    // If options list is empty, load values from database
    var count = $('#newEventType').children('option').length;

    if(count == 0)
    {
        // Add default option
        $('#newEventType').append($('<option>', {
            value: '',
            text : '- select -'
        }));

        if(window.Android)
        {
            var eventTypesJson = window.Android.fetchEventTypes();
            var eventTypes = JSON.parse(eventTypesJson);

            $.each(eventTypes, function (i, item) {
                $('#newEventType').append($('<option>', {
                    value: item.id,
                    text : item.name
                }));
            });

            var tagJson = window.Android.fetchTags();
            tagList = JSON.parse(tagJson);

            var versionInfoJson = window.Android.versionInfo();
            var versionInfo = JSON.parse(versionInfoJson);
            $("#app-version-name").text(versionInfo.versionName);
            $("#app-version-number").text(versionInfo.versionNumber);
            $("#database-version-number").text(versionInfo.databaseVersion);
            $("#update-date").text(formatDisplayDate(versionInfo.dateUpdated));
        }
    }

    $("#newEventType").selectmenu();
    $("#newCalendarEventDescription").selectmenu();
    $("#theme-pref").selectmenu();

    var themePref = localStorage.getItem(themeModePrefKeyName);
    if(themePref == 'b') confirmTheme = 'dark';
});

// Object definitions
function AbvResultSet() {
    this.standard = '-.--%';
    this.highstandard = '-.--%';
    this.alternate = '-.--%';
    this.wine = '-.--%';
};

// Page Transition events

$(document).on("pagebeforeshow","#abv", function(){

    var abvPref = localStorage.getItem(abvPrefKeyName) ?? 'std';

    switch(abvPref)
    {
        case 'std':
            $("#abvFormulaPref").html("Currently using <strong>Standard</strong> formula.");
            break;
        case 'highstd':
            $("#abvFormulaPref").html("Currently using <strong>High Standard</strong> formula.");
            break;
        case 'alt':
            $("#abvFormulaPref").html("Currently using <strong>Alternate</strong> formula.");
            break;
        case 'wine':
            $("#abvFormulaPref").html("Currently using <strong>Wine</strong> formula.");
            break;
        default:
            $("#abvFormulaPref").html("Preference not found. Using <strong>Standard</strong> formula.");
            window.Android.logError('calcButton','avbPref variable fell through switch.');
    }

    $('#initialGravity').val('');
    $('#newGravity').val('');
    $("#abvResult").text('0.00%');
});

$(document).on("pagebeforeshow","#my-meads",function() {

    loadMyMeadsListView();

});

$(document).on("pagebeforeshow","#my-recipes",function() {

    loadMyRecipesListView();

});

$(document).on("pagebeforeshow","#new-mead",function() {

    // use hidden field as the trigger for the form mode
    var meadId = $("#meadId").val();

    //var dateFormatPref = localStorage.getItem(dateFormatPrefKeyName) ?? 'ISO';
    //var dateFormatString = (dateFormatPref === 'ISO') ? 'ISO_8601' : 'mm/dd/yy';

    if(meadId)
    {
        // Field values should already be set from invoking function
        if(meadId == "FromRecipe")
        {
            $("#meadId").val(""); // reset form field for new mead
            $("#new-mead-content-title").text("Add New Mead from Recipe");
        }
        else
        {
            $("#new-mead-content-title").text("Edit Mead");
        }
    }
    else
    {
        $("#new-mead-content-title").text("Add New Mead");
        $("#newMeadName").val('');
        $("#newMeadStartDate").val('');
        $("#newMeadOriginalGravity").val('');
        $("#newMeadDescription").val('');
    }
});

$(document).on("pagebeforeshow","#new-recipe",function() {

    // use hidden field as the trigger for the form mode

    var recipeId = $("#recipeId").val();

    if(recipeId)
    {
        // Field values should already be set from invoking function
        $("#new-recipe-content-title").text("Edit Recipe");
    }
    else
    {
        $("#new-recipe-content-title").text("Add New Recipe");
        $("#newRecipeName").val('');
        $("#newRecipeDescription").val('');
    }
});

$(document).on("pagebeforeshow","#new-reading",function() {

    // Clear form
    $("#newReadingDate").val('');
    $("#newReadingGravity").val('');

});

$(document).on("pagebeforeshow","#new-event",function() {

    var eventId = $("#eventId").val();

    if(eventId)
    {
        // Field values should already be set from invoking function
        $("#new-event-content-title").text("Edit Event");
    }
    else
    {
        $("#new-event-content-title").text("Add New Event");

        $("#newEventDate").val('');
        $("#newEventType").val('');
        $("#newEventType").selectmenu("refresh", true);
        $("#newEventDescription").val('');
    }
});

$(document).on("pagebeforeshow","#preferences",function(){

    var abvPref = localStorage.getItem(abvPrefKeyName) ?? 'std';
    var sortPref = localStorage.getItem(sortPrefKeyName) ?? 'byId';
    var dateFormatPref = localStorage.getItem(dateFormatPrefKeyName) ?? 'ISO';
    var themePref = localStorage.getItem(themeModePrefKeyName) ?? 'a';

    $("#abv-formula-pref").val(abvPref);
    $("#abv-formula-pref").selectmenu("refresh", true);

    $("#sort-pref").val(sortPref);
    $("#sort-pref").selectmenu("refresh", true);

    $("#date-format-pref").val(dateFormatPref);
    $("#date-format-pref").selectmenu("refresh", true);

    $("#theme-pref").val(themePref);
    $("#theme-pref").selectmenu("refresh", true);

    // Change theme immediately
    //$('div[data-role="page"]').attr('data-theme', themePref);
    changeGlobalTheme(themePref);
});

// Form validation events

$("#new-mead-form").validate({
    errorLabelContainer: "#messageList",
    wrapper: "li",
    rules: {
        newMeadName: {
            required: true
        },
        newMeadStartDate: {
            required: true
        },
        newMeadOriginalGravity: {
            required: false,
            range: specificGravityRange
        }
    },
    messages: {
        newMeadName: "Mead Name is required.",
        newMeadStartDate: "Start Date is required.",
        newMeadOriginalGravity: "Specific Gravity between " + specificGravityRange[0] + " and " + specificGravityRange[1] + " is required.",
    }
});

$("#new-recipe-form").validate({
    errorLabelContainer: "#recipeMessageList",
    wrapper: "li",
    rules: {
        newRecipeName: {
            required: true
        }
    },
    messages: {
        newRecipeName: "Recipe Name is required."
    }
});

$("#new-reading-form").validate({
    errorLabelContainer: "#newReadingMessageList",
    wrapper: "li",
    rules: {
        newReadingDate: {
            required: true
        },
        newReadingGravity: {
            required: true,
            range: specificGravityRange
        }
    },
    messages: {
        newReadingDate: "Reading Date is required.",
        newReadingGravity: "Specific Gravity between " + specificGravityRange[0] + " and " + specificGravityRange[1] + " is required.",
    }
});

$("#new-event-form").validate({
    errorLabelContainer: "#newEventMessageList",
    wrapper: "li",
    rules: {
        newEventDate: {
            required: true
        },
        newEventType: {
            required: true
        }
    },
    messages: {
        newEventDate: "Event Date is required.",
        newEventType: "Event Type is required."
    }
});

$("#abv-form").validate({
    errorLabelContainer: "#abvMessageList",
    wrapper: "li",
    rules: {
        initialGravity: {
            required: true,
            range: specificGravityRange
        },
        newGravity: {
            required: true,
            range: specificGravityRange
        }
    },
    messages: {
        initialGravity: "Initial Gravity between " + specificGravityRange[0] + " and " + specificGravityRange[1] + " is required.",
        newGravity: "New Gravity between " + specificGravityRange[0] + " and " + specificGravityRange[1] + " is required.",
    }
});

$("#calendar-event-form").validate({
   errorLabelContainer: "#calendarEventMessageList",
   wrapper: "li",
   rules: {
       newCalendarEventTitle: {
           required: true
       },
       newCalendarEventDescription: {
           required: true
       },
       newCalendarEventDate: {
           required: true
       }
   },
   messages: {
       newCalendarEventTitle: "Event Title is required.",
       newCalendarEventDescription: "Event Description is required.",
       newCalendarEventDate: "Event Date is required.",
   }
});

// Button tap events
$("#newMeadButton").on("tap", function(event) {

    event.preventDefault();

    // Set hidden field value so form switches to correct mode
    $("#meadId").val('');
    $(":mobile-pagecontainer").pagecontainer("change", "#new-mead");
});

$("#newRecipeButton").on("tap", function(event) {

    event.preventDefault();

    // Set hidden field value so form switches to correct mode
    $("#recipeId").val('');
    $(":mobile-pagecontainer").pagecontainer("change", "#new-recipe");
});

$("#toggleArchivedMeadsButton").on("tap", function(event) {
    var includeArchivedString = localStorage.getItem(archivePrefKeyName) ?? "false";

    var includeArchived = !(includeArchivedString === "true");

    localStorage.setItem(archivePrefKeyName, includeArchived);

    loadMyMeadsListView();
});

$("#saveMeadButton").on("tap", function(event){

    event.preventDefault();

    if(window.Android)
    {
        if($("#new-mead-form").valid()){

            // Grab form inputs
            var mId = $("#meadId").val();
            var mName = $("#newMeadName").val();
            var mDate = $("#newMeadStartDate").val();
            var mGravity = $("#newMeadOriginalGravity").val();
            var mDesc = $("#newMeadDescription").val();

            if(mId)
            {
                window.Android.updateMead(mId, mName, mDate, mGravity, mDesc);
                window.Android.logInfo('MainActivity', 'Mead ' + mId + ' updated!');
            }
            else
            {
                window.Android.addMead(mName, mDate, mGravity, mDesc);
                window.Android.logInfo('MainActivity', 'New mead saved!');
            }

            if(mId)
            {
                viewMead(mId);
            }
            else
            {
                $.mobile.navigate("#my-meads");
            }
        }
    }
    else
    {
        $.alert({
            theme: confirmTheme,
            title: 'Alert',
            content:'Android Javascript bridge is not available'
        });
    }

});

$("#saveRecipeButton").on("tap", function(event){

    event.preventDefault();

    if(window.Android)
    {
        if($("#new-recipe-form").valid()){

            // Grab form inputs
            var rId = $("#recipeId").val();
            var rName = $("#newRecipeName").val();
            var rDesc = $("#newRecipeDescription").val();

            if(rId)
            {
                window.Android.updateRecipe(rId, rName, rDesc);
                window.Android.logInfo('MainActivity', 'Recipe ' + rId + ' updated!');
            }
            else
            {
                window.Android.addRecipe(rName, rDesc);
                window.Android.logInfo('MainActivity', 'New recipe saved!');
            }

            if(rId)
            {
                viewRecipe(rId);
            }
            else
            {
                $.mobile.navigate("#my-recipes");
            }
        }
    }
    else
    {
        $.alert({
            theme: confirmTheme,
            title: 'Alert',
            content:'Android Javascript bridge is not available'
        });
    }

});

$("#saveReadingButton").on("tap", function(event){

    event.preventDefault();

    if(window.Android)
    {
        if($("#new-reading-form").valid()){

            // Persist data
            var meadId = $("#newReadingMeadId").val();
            var date = $("#newReadingDate").val();
            var gravity = $("#newReadingGravity").val();

            window.Android.logDebug('MainActivity', 'MeadID: ' + meadId);
            window.Android.logDebug('MainActivity', 'Date: ' + date);
            window.Android.logDebug('MainActivity', 'Gravity: ' + gravity);

            window.Android.addReading(meadId, date, gravity);

            window.Android.logInfo('MainActivity', 'New mead reading saved!');

            viewReadings(meadId);
        }
    }
    else
    {
        $.alert({
            theme: confirmTheme,
            title: 'Alert',
            content:'Android Javascript bridge is not available'
        });
    }

});

$("#saveEventButton").on("tap", function(event){

 event.preventDefault();

 if(window.Android)
 {
     if($("#new-event-form").valid()){

         // Grab form inputs
         var eventId = $("#eventId").val();
         var meadId = $("#newEventMeadId").val();
         var date = $("#newEventDate").val();
         var typeId = $("#newEventType").val();
         var description = $("#newEventDescription").val();

         window.Android.logDebug('MainActivity', 'Event ID: ' + eventId);
         window.Android.logDebug('MainActivity', 'Event Mead ID: ' + meadId);
         window.Android.logDebug('MainActivity', 'Event Date: ' + date);
         window.Android.logDebug('MainActivity', 'Event Type: ' + typeId);
         window.Android.logDebug('MainActivity', 'Event Description: ' + description);

         if(eventId)
         {
             window.Android.updateEvent(eventId, meadId, date, typeId, description);
             window.Android.logInfo('MainActivity', 'Event ' + eventId + ' updated!');
         }
         else
         {
             window.Android.addEvent(meadId, date, typeId, description);
             window.Android.logInfo('MainActivity', 'New event saved!');
         }

         viewEvents(meadId);
     }
 }
 else
 {
     $.alert({
         theme: confirmTheme,
         title: 'Alert',
         content:'Android Javascript bridge is not available'
     });
 }

});

$("#calcButton").on("tap", function(event) {

    event.preventDefault();

    if($("#abv-form").valid()){

        var ig = $('#initialGravity').val();
        var ng = $('#newGravity').val();

        var result = calculateAbv(ig,ng);

        var abvDisplayValue = getPreferredAbvValue(result);

        $("#abvResult").text(abvDisplayValue);
    }

});

$("#saveCalendarEventButton").on("tap", function(event){
    event.preventDefault();

    if(window.Android)
    {
        if($("#calendar-event-form").valid()){
            // craft message strings and call JS bridge with data
            // set field values
            //var meadId = $("#newCalendarEventMeadId").val();
            var title = $("#newCalendarEventTitle").val();
            var desc = $("#newCalendarEventDescription option:selected").text(); // selected option
            var date = $("#newCalendarEventDate").val();
            var notes = $("#newCalendarEventNotes").val();
            var linebreaks = "\r\n\r\n";

            if(desc == 'Other')
            {
                // Not useful to include in description
                desc = '';
                linebreaks = '';
            }

            var fullDescription = "It's Time to " + desc + linebreaks + notes;

            window.Android.logDebug("Calendar Event", title + " - " + fullDescription);

            window.Android.createEvent(title, fullDescription, date);
        }
    }
    else
    {
        $.alert({
            theme: confirmTheme,
            title: 'Alert',
            content:'Android Javascript bridge is not available'
        });
    }
});



// Select change events
$("#abv-formula-pref").change(function() {
    localStorage.setItem(abvPrefKeyName,this.value);
    window.Android.logDebug('ChangeEvent','Formula preference set to: ' + this.value);
});

$("#sort-pref").change(function() {
    localStorage.setItem(sortPrefKeyName,this.value);
    window.Android.logDebug('ChangeEvent','Sort preference set to: ' + this.value);
});

$("#date-format-pref").change(function() {
    localStorage.setItem(dateFormatPrefKeyName,this.value);
    window.Android.logDebug('ChangeEvent','Date Format preference set to: ' + this.value);
});

$("#theme-pref").change(function() {
    localStorage.setItem(themeModePrefKeyName,this.value);
    window.Android.logDebug('ChangeEvent','Theme preference set to: ' + this.value);
    // This function is special. It will cause the webview to load a new file.
    // Testing this method since switching the theme is JS was harder than it should have been.
    window.Android.activateTheme(this.value);
});

// Custom app functions
function loadMyMeadsListView()
{
    if(window.Android)
    {
        window.Android.logInfo('MainActivity','JS Bridge available. Starting data fetch for mead list.');

        // Retrieve user preference
        var sortPref = localStorage.getItem(sortPrefKeyName) ?? 'byId';
        var includeArchivedString = localStorage.getItem(archivePrefKeyName) ?? "false";

        var includeArchived = includeArchivedString === "true";

        // Clear list
        $("#mead-list").empty();

        // Fetch data from database
        var meadsJson = window.Android.fetchMeads(sortPref, includeArchived);
        var meadsData = JSON.parse(meadsJson);

        window.Android.logDebug('MainActivity', 'Fetched JSON: ' + meadsJson);

        // Append to list
        for (var i = 0; i < meadsData.length; i++) {

            var meadName = meadsData[i].name;

            if(meadsData[i].archived)
            {
                meadName = '<span class="archived">' + meadName + '</span>';
            }

            $("#mead-list").append('<li><a href="javascript:viewMead(' + meadsData[i].id + ');" data-ajax="false">' + meadName + '</a></li>');
        }

        $("#mead-list").listview("refresh");

        window.Android.logInfo('MainActivity','Mead list loaded and refreshed.');
    }
    else
    {
        // Insert mock item for layout testing
        $("#mead-list").empty();
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false"><span class="archived">My First Mead</span></a></li>');
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").listview("refresh");
    }
}

function loadMyRecipesListView()
{
    if(window.Android)
    {
        window.Android.logInfo('MainActivity','JS Bridge available. Starting data fetch for recipe list.');

        // Clear list
        $("#recipe-list").empty();

        // Fetch data from database
        var recipesJson = window.Android.fetchRecipes();
        var recipesData = JSON.parse(recipesJson);

        window.Android.logDebug('MainActivity', 'Fetched JSON: ' + recipesJson);

        // Append to list
        for (var i = 0; i < recipesData.length; i++) {

            var recipeName = recipesData[i].name;

            $("#recipe-list").append('<li><a href="javascript:viewRecipe(' + recipesData[i].id + ');" data-ajax="false">' + recipeName + '</a></li>');
        }

        $("#recipe-list").listview("refresh");

        window.Android.logInfo('MainActivity','Recipe list loaded and refreshed.');
    }
    else
    {
        // Insert mock item for layout testing
        $("#recipe-list").empty();
        $("#recipe-list").append('<li><a href="javascript:viewRecipe(0);" data-ajax="false">My First Recipe</a></li>');
        $("#recipe-list").append('<li><a href="javascript:viewRecipe(0);" data-ajax="false">My Second Recipe</a></li>');
        $("#recipe-list").listview("refresh");
    }
}

function viewReadings(meadId)
{
    if(window.Android && meadId > 0)
    {
        window.Android.logInfo('MainActivity', 'Fetching Readings for mead ID ' + meadId);

        // Fetch data from database
        var meadJson = window.Android.fetchMead(meadId);
        var meadData = JSON.parse(meadJson);

        var readingsJson = window.Android.fetchReadings(meadId);
        var readingsData = JSON.parse(readingsJson);

        window.Android.logDebug('MainActivity', meadJson);
        window.Android.logDebug('MainActivity', readingsJson);

        // Set mead name on page
        $("#readings-mead-name").text(meadData.name);

        // Clear existing rows from table
        // Clear list
        $("#reading-list tbody").empty();

        // Append original gravity reading to list
        $("#reading-list tbody").append('<tr><td>' + formatDisplayDate(meadData.startDate) + '</td><td>' + meadData.originalGravity + '</td><td>N/A</td><td>&nbsp;</td></tr>');

        // Holding variable for original or previous gravity
        var og = meadData.originalGravity;

        // This array will be the same length as the readings array
        var results = calculateAbvByReadings(og, readingsData);

        // Append to list
        for (var i = 0; i < readingsData.length; i++) {

            var abvDisplayValue = getPreferredAbvValue(results[i]);

            if(results[i] && results[i].specificGravityDifference)
            {
                abvDisplayValue = "+" + results[i].specificGravityDifference.toFixed(3);
            }

            $("#reading-list tbody").append('<tr><td>' + formatDisplayDate(readingsData[i].date) + '</td><td>' + readingsData[i].specificGravity + '</td><td>' + abvDisplayValue + '</td><td><a href="javascript:deleteReading(' + meadId + ',' + readingsData[i].id + ');" class="ui-btn ui-shadow ui-corner-all ui-icon-delete ui-btn-icon-notext">Delete</a></td></tr>');
        }

        $("#newReadingButton").off("tap"); // clear existing event handlers
        $("#backToMeadViewButton").off("tap"); // clear existing event handlers

        $("#newReadingButton").on("tap", { meadId: meadId }, function(event) {
            event.preventDefault();

            window.Android.logDebug('MainActivity', 'New Reading Button pressed. Mead ID: ' + event.data.meadId);

            // set value of hidden form
            $("#newReadingMeadId").val(event.data.meadId);

            $(":mobile-pagecontainer").pagecontainer("change", "#new-reading",{changeHash:false});
        });
        $("#backToMeadViewButton").on("tap", { meadId: meadId }, function(event) {
            event.preventDefault();

            window.Android.logDebug('MainActivity', 'Back To Mead View Button pressed. Mead ID: ' + event.data.meadId);

            viewMead(event.data.meadId);
        });
    }
    else
    {
        // Populate sample data
    }

    $(":mobile-pagecontainer").pagecontainer("change", "#readings");
 }

function viewEvents(meadId)
{
    if(window.Android && meadId > 0)
    {
        window.Android.logInfo('MainActivity', 'Fetching Events for mead ID ' + meadId);

        // Fetch data from database
        var meadJson = window.Android.fetchMead(meadId);
        var meadData = JSON.parse(meadJson);

        var eventsJson = window.Android.fetchEvents(meadId);
        var eventsData = JSON.parse(eventsJson);

        window.Android.logDebug('MainActivity', meadJson);
        window.Android.logDebug('MainActivity', eventsJson);

        // Set mead name on page
        $("#events-mead-name").text(meadData.name);

        // Clear existing rows from table
        // Clear list
        $("#events-list tbody").empty();

        // Append to data
        for (var i = 0; i < eventsData.length; i++) {

            var disableButtonFlag = 'ui-state-disabled';
            var daysAgoOutput = '';
            var daysAgo = daysSince(eventsData[i].date);

            // Showing 45 days, 60 days, 120 days starts to seem weird.
            // Adding more logic to switch to weeks might be ok, but dates should be fine for now
            if(daysAgo > 0 && daysAgo < 32)
            {
                daysAgoOutput = '(' + daysAgo + ' days ago)';
            }

            if(eventsData[i].description)
            {
                disableButtonFlag = '';
            }

            // Append data to list
            $("#events-list tbody").append('<tr><td style="white-space: nowrap; text-align: center;">' + formatDisplayDate(eventsData[i].date) + '<br>' + daysAgoOutput + '</td><td>' + eventsData[i].typeName + '</td><td style="white-space: nowrap; text-align: center;">' +
                '<a href="javascript:showEventDescription(' + eventsData[i].id + ',\'' + formatDisplayDate(eventsData[i].date) + '\');" class="ui-btn ui-mini ui-btn-inline ui-shadow ui-corner-all ui-icon-comment ui-btn-icon-notext ' + disableButtonFlag + '">Show</a>' +
                '<a href="javascript:editEvent(' + eventsData[i].id + ');" class="ui-btn ui-mini ui-btn-inline ui-shadow ui-corner-all ui-icon-edit ui-btn-icon-notext">Edit</a>' +
                '<a href="javascript:deleteEvent(' + meadId + ',' + eventsData[i].id + ');" class="ui-btn ui-mini ui-btn-inline ui-shadow ui-corner-all ui-icon-delete ui-btn-icon-notext">Delete</a>' +
                '</td></tr>');
        }

        $("#newEventButton").off("tap"); // clear existing event handlers

        $("#newEventButton").on("tap", { meadId: meadId }, function(event) {
            event.preventDefault();

            window.Android.logDebug('MainActivity', 'New Event Button pressed. Mead ID: ' + event.data.meadId);

            // set value of hidden form fields
            $("#eventId").val('');
            $("#newEventMeadId").val(event.data.meadId);

            $(":mobile-pagecontainer").pagecontainer("change", "#new-event", {changeHash:false});
        });
    }
    else
    {
        // Populate sample data
    }

    $(":mobile-pagecontainer").pagecontainer("change", "#events");
 }

function deleteReading(meadId, readingId)
{
    window.Android.logDebug('MainActivity', 'Delete Reading Button pressed. Mead ID: ' + meadId);
    window.Android.logDebug('MainActivity', 'Reading ID: ' + readingId);

    if(window.Android && meadId > 0 && readingId > 0)
    {
        $.confirm({
            theme: confirmTheme,
            title: 'Delete Reading',
            content: 'Are you sure?',
            animation: 'top',
            closeAnimation: 'top',
            buttons: {
                confirm: function () {
                    window.Android.deleteReading(readingId);

                    viewReadings(meadId);
                },
                cancel: function () {
                    // do nothing
                }
            }
        });
    }
    else
    {
        $.alert({
            theme: confirmTheme,
            title: 'Alert',
            content:'Android Javascript bridge is not available'
        });
    }

    // Keep link from doing anything
    return false;
 }

function deleteEvent(meadId, eventId)
{
     window.Android.logDebug('MainActivity', 'Delete Event Button pressed. Mead ID: ' + meadId);
     window.Android.logDebug('MainActivity', 'Log Entry ID: ' + eventId);

     if(window.Android && meadId > 0 && eventId > 0)
     {
         $.confirm({
             theme: confirmTheme,
             title: 'Delete Event',
             content: 'Are you sure?',
             animation: 'top',
             closeAnimation: 'top',
             buttons: {
                 confirm: function () {
                     window.Android.deleteEvent(eventId);

                     viewEvents(meadId);
                 },
                 cancel: function () {
                     // do nothing
                 }
             }
         });
     }
     else
     {
         $.alert({
             theme: confirmTheme,
             title: 'Alert',
             content:'Android Javascript bridge is not available'
         });
     }

     // Keep link from doing anything
     return false;
  }

function viewMead(id)
{
    if(window.Android && id > 0)
    {
        window.Android.logInfo('MainActivity', 'Fetching Mead by ID: ' + id);

        // Fetch data from database
        var meadJson = window.Android.fetchMead(id);
        var meadData = JSON.parse(meadJson);

        var tagsJson = window.Android.fetchMeadTags(id);
        var tagsData = JSON.parse(tagsJson);

        var readingsJson = window.Android.fetchReadings(id);
        var readingsData = JSON.parse(readingsJson);

        var eventsJson = window.Android.fetchEvents(id);
        var eventsData = JSON.parse(eventsJson);

        window.Android.logDebug('MainActivity', meadJson);
        window.Android.logDebug('MainActivity', tagsJson);
        window.Android.logDebug('MainActivity', readingsJson);
        window.Android.logDebug('MainActivity', eventsJson);

        var lastReadingDisplayValue = "N/A";
        var lastEventDisplayValue = "N/A";

        if(readingsData.length > 0)
        {
            var lastReading = readingsData[readingsData.length - 1];

            //var result = calculateAbv(meadData.originalGravity,lastReading.specificGravity);

            // This now returns an array of ABV results
            var expResults = calculateAbvByReadings(meadData.originalGravity, readingsData);

            var abvDisplayValue = getPreferredAbvValue(expResults[expResults.length - 1]);

            lastReadingDisplayValue = lastReading.specificGravity + " (" + abvDisplayValue + ") as of " + formatDisplayDate(lastReading.date);
        }

        if(eventsData.length > 0)
        {
            var lastEvent = eventsData[eventsData.length - 1];
            lastEventDisplayValue = lastEvent.typeName + " on " + formatDisplayDate(lastEvent.date);
        }

        // Populate fields
        //$("#mead-id").text(meadData.id); was used for debugging; not really useful for the user
        $("#mead-name").text(meadData.name);
        $("#mead-start-date").text(formatDisplayDate(meadData.startDate));
        $("#mead-description").text(meadData.description);
        $("#mead-original-gravity").text(meadData.originalGravity);
        $("#mead-last-reading").text(lastReadingDisplayValue);
        $("#mead-last-event").text(lastEventDisplayValue);

        // Clear & update tags
        $("#mead-tags span").remove();
        displayMeadTags(tagsData);

        // add event handlers for buttons
        $("#deleteMeadButton").off("tap"); // clear existing event handlers
        $("#readingsButton").off("tap"); // clear existing event handlers
        $("#eventsButton").off("tap"); // clear existing event handlers
        $("#editMeadButton").off("tap"); // clear existing event handlers
        $("#calendarEventButton").off("tap"); // clear existing event handlers
        $("#tagsButton").off("tap"); //clear existing event handlers
        $("#splitButton").off("tap"); //clear existing event handlers
        $("#archiveButton").off("tap"); //clear existing event handlers
        $("#mead-tags").off("taphold"); //clear existing event handlers

        $("#deleteMeadButton").on("tap", { value: id }, function(event) {
            event.preventDefault();

            var id = event.data.value;

            $.confirm({
                theme: confirmTheme,
                title: 'Delete Mead Entry',
                content: 'Are you sure?',
                animation: 'top',
                closeAnimation: 'top',
                buttons: {
                    confirm: function () {
                        window.Android.deleteMead(id);

                        $.mobile.navigate("#my-meads");
                    },
                    cancel: function () {
                        // do nothing
                    }
                }
            });
        });
        $("#readingsButton").on("tap", { meadId: meadData.id, meadName: meadData.name, meadStartDate: meadData.startDate, meadOriginalGravity: meadData.originalGravity }, function(event) {
            event.preventDefault();

            viewReadings(event.data.meadId);
        });
        $("#eventsButton").on("tap", { meadId: meadData.id, meadName: meadData.name, meadStartDate: meadData.startDate, meadOriginalGravity: meadData.originalGravity }, function(event) {
            event.preventDefault();

            viewEvents(event.data.meadId);
        });
        $("#editMeadButton").on("tap", { meadId: meadData.id, meadName: meadData.name, meadStartDate: meadData.startDate, meadOriginalGravity: meadData.originalGravity, meadDescription: meadData.description }, function(event) {

            event.preventDefault();

            // Populate data
            $("#meadId").val(event.data.meadId);
            $("#newMeadName").val(event.data.meadName);
            $("#newMeadStartDate").val(event.data.meadStartDate);
            $("#newMeadOriginalGravity").val(event.data.meadOriginalGravity);
            $("#newMeadDescription").val(event.data.meadDescription);

            $(":mobile-pagecontainer").pagecontainer("change", "#new-mead");
        });
        $("#calendarEventButton").on("tap", { meadId: meadData.id, meadName: meadData.name }, function(event) {

            event.preventDefault();

            // set field values
            $("#newCalendarEventMeadId").val(event.data.meadId);
            $("#newCalendarEventMeadName").val(event.data.meadName);

            $("#newCalendarEventTitle").val("Mead Mate: '" + event.data.meadName + "'");
            $("#newCalendarEventDescription").prop("selectedIndex", 0);
            $("#newCalendarEventDate").val("");
            $("#newCalendarEventNotes").val("");

            // transition to view
            $(":mobile-pagecontainer").pagecontainer("change", "#calendar-event");
        });
        $("#tagsButton").on("tap", { meadId: meadData.id }, function(event) {

            event.preventDefault();

            $.confirm({
                theme: confirmTheme,
                title: 'Add Tag',
                content: '<input id="newTagMeadId" name="newTagMeadId" type="hidden" value="' + event.data.meadId + '"><input id="newTag" name="newTag" type="text">',
                buttons: {
                    save: function () {
                        var meadId = $("#newTagMeadId").val();
                        var tag = $("#newTag").val();
                        window.Android.addMeadTag(meadId, tag);
                        displayTag(tag); // Update DOM so we don't have to re-fetch data
                        displayTagTip();
                    },
                    cancel: function () {
                        // do nothing
                    }
                }
            });
        });
        $("#splitButton").on("tap", { meadId: meadData.id }, function(event) {

            event.preventDefault();

            $.confirm({
                theme: confirmTheme,
                title: 'Split Mead Batch',
                content: '<label for="splitCount">Split into how many batches? (2 to 20)</label><br>' +
                        '<input id="splitMeadId" name="splitMeadId" type="hidden" value="' + event.data.meadId + '">' +
                        '<input type="number" id="splitCount" name="splitCount" min="2" max="20"><br><br>' +
                        '<label for="splitDeleteOriginal">Delete Original Mead Record?</label>' +
                        '<input type="checkbox" id="splitDeleteOriginal" name="splitDeleteOriginal" checked> Delete Original' +
                        '<p id="splitDescription">Deleting the original record is recommended, but can be skipped and done later, if desired.</p>',
                buttons: {
                        formSubmit: {
                            text: 'Save',
                            action: function () {
                                var count = $('#splitCount').val();

                                if(isNaN(count) || count < 2 || count > 20){

                                    $.alert({
                                        theme: confirmTheme,
                                        title: 'Error',
                                        content: 'Please enter a valid split value (2 - 20)'
                                    });

                                    return false;
                                }

                                var meadId = $("#splitMeadId").val();
                                var splitCount = $("#splitCount").val();
                                var deleteOriginal = $("#splitDeleteOriginal").is(':checked')
                                window.Android.splitMead(meadId, splitCount, deleteOriginal);

                                $.mobile.navigate("#my-meads");
                            }
                        },
                        cancel: function () {
                            //close
                        }
                }
            });
        });

        $("#archiveButton").on("tap", { meadId: meadData.id }, function(event) {
            event.preventDefault();

            window.Android.logDebug('MainActivity', 'Toggling archive bit for mead ID ' + event.data.meadId);

            window.Android.toggleMeadArchiveFlag(event.data.meadId);

            $.mobile.navigate("#my-meads");
        });

        // Update link text on archive button to make it clear what clicking it will do
        if(meadData.archived)
        {
            $("#archiveButton").text("Unhide Mead");
        }
        else
        {
            $("#archiveButton").text("Hide Mead");
        }

        $("#mead-tags").on("taphold", "span", function(event)
            {
                var tagName = $(event.target).text();

                $.confirm({
                    theme: confirmTheme,
                    title: "Remove Tag '" + tagName + "'",
                    content: 'Are you sure?',
                    animation: 'top',
                    closeAnimation: 'top',
                    buttons: {
                        confirm: function () {
                            window.Android.deleteMeadTag(id, tagName);
                            $(event.target).remove();
                        },
                        cancel: function () {
                            // do nothing
                        }
                    }
                });
            });
    }
    else
    {
        // Populate sample data
        //$("#mead-id").text("0");
        $("#mead-name").text("My First Mead");
        $("#mead-start-date").text(formatDisplayDate("2022-01-01"));
        $("#mead-description").text("Sample data");
        $("#mead-original-gravity").text("1.000");
    }

    $(":mobile-pagecontainer").pagecontainer("change", "#mead-view");
 }

function viewRecipe(id)
{
    if(window.Android && id > 0)
    {
        window.Android.logInfo('MainActivity', 'Fetching Recipe by ID: ' + id);

        // Fetch data from database
        var recipeJson = window.Android.fetchRecipe(id);
        var recipeData = JSON.parse(recipeJson);

        window.Android.logDebug('MainActivity', recipeJson);

        // Populate fields
        $("#recipe-name").text(recipeData.name);
        $("#recipe-description").text(recipeData.description);

        // add event handlers for buttons
        $("#deleteRecipeButton").off("tap"); // clear existing event handlers
        $("#editRecipeButton").off("tap"); // clear existing event handlers
        $("#createBatchButton").off("tap"); //clear existing event handlers

        $("#deleteRecipeButton").on("tap", { value: id }, function(event) {
            event.preventDefault();

            var id = event.data.value;

            $.confirm({
                theme: confirmTheme,
                title: 'Delete Recipe Entry',
                content: 'Are you sure?',
                animation: 'top',
                closeAnimation: 'top',
                buttons: {
                    confirm: function () {
                        window.Android.deleteRecipe(id);

                        $.mobile.navigate("#my-recipes");
                    },
                    cancel: function () {
                        // do nothing
                    }
                }
            });
        });

        $("#editRecipeButton").on("tap", { recipeId: recipeData.id, recipeName: recipeData.name, recipeDescription: recipeData.description }, function(event) {

            // Populate data
            $("#recipeId").val(event.data.recipeId);
            $("#newRecipeName").val(event.data.recipeName);
            $("#newRecipeDescription").val(event.data.recipeDescription);

            $(":mobile-pagecontainer").pagecontainer("change", "#new-recipe");
        });

        $("#createBatchButton").on("tap", { recipeName: recipeData.name, recipeDescription: recipeData.description }, function(event) {

            event.preventDefault();

            var now = new Date();
            var today = now.toISOString().slice(0, 10);

            window.Android.logInfo('MainActivity', 'Formatted batch date: ' + today);

            $("#meadId").val("FromRecipe"); // Used as trigger
            $("#newMeadName").val(event.data.recipeName);
            $("#newMeadStartDate").val(today);
            $("#newMeadOriginalGravity").val("");
            $("#newMeadDescription").val(event.data.recipeDescription);

            $(":mobile-pagecontainer").pagecontainer("change", "#new-mead");
        });
    }
    else
    {
        // Populate sample data
        $("#recipe-name").text("My First Recipe");
        $("#recipe-description").text("Sample data");
    }

    $(":mobile-pagecontainer").pagecontainer("change", "#recipe-view");
 }

function editEvent(eventId)
{
    window.Android.logDebug('MainActivity', 'Edit Event Button pressed. Event ID: ' + eventId);

    // query event details
    if(window.Android && eventId > 0)
    {
        window.Android.logInfo('MainActivity', 'Fetching event ' + eventId);

        // Fetch data from database
        var eventJson = window.Android.fetchEvent(eventId);
        var eventData = JSON.parse(eventJson);

        window.Android.logDebug('MainActivity', eventJson);

        // set form fields on event form
        $("#eventId").val(eventId);
        $("#newEventMeadId").val(eventData.meadId);
        $("#newEventDate").val(eventData.date);
        $("#newEventType").val(eventData.typeId);
        $("#newEventType").selectmenu("refresh", true);
        $("#newEventDescription").val(eventData.description);

        // transition to event form
        $(":mobile-pagecontainer").pagecontainer("change", "#new-event", {changeHash:false});
    }
    else
    {
        $.alert({
            theme: confirmTheme,
            title: 'Alert',
            content: 'Android Javascript bridge is not available'
        });
    }
}

// This method takes added sugar into consideration and returns
// the ABV value across all readings.
function calculateAbvByReadings(initialGravity, batchReadings)
{
    // Initial array for holding results objects
    var results = new Array();

    if(isNaN(parseFloat(initialGravity)))
    {
        // return array with single object
        results.push(new AbvResultSet());
        return results;
    }

    if(!Array.isArray(batchReadings) || batchReadings.length == 0)
    {
        // return array with single object
        results.push(new AbvResultSet());
        return results;
    }

    window.Android.logInfo('MainActivity', 'calculateAbvByReadings Params: ' + initialGravity + ', ' + batchReadings.length);

    // hold initial value
    // start loop, hold first value
    // compare each value to previous
    // if current is higher than previous, add difference to initial
    // calculate ABV from modified initial and final gravities
    var ig = new Decimal(initialGravity);
    var prevReading = ig;

    window.Android.logInfo('MainActivity', 'Initial Gravity: ' + ig.toFixed(3));
    window.Android.logInfo('MainActivity', 'Readings count: ' + batchReadings.length);

    for (let i = 0; i < batchReadings.length; i++) {

        var sg = new Decimal(batchReadings[i].specificGravity);
        var diff = null;

        window.Android.logInfo('MainActivity', 'Reading this pass: ' + sg.toFixed(3));

        if(sg.greaterThan(prevReading))
        {
            window.Android.logInfo('MainActivity', 'Previous gravity reading: ' + prevReading.toFixed(3));
            window.Android.logInfo('MainActivity', 'Larger gravity reading found: ' + sg.toFixed(3));

            // A reading was found that is greater than the previous
            // This indicates that more sugar was added to the batch
            diff = sg.minus(prevReading);

            // Add the difference to the initial gravity
            ig = ig.plus(diff);
        }

        // Calculate ABV at this step, add to array
        var abvResult = calculateAbv(ig, sg);

        // Appending property to object when there's a positive difference
        if(diff)
        {
            abvResult.specificGravityDifference = diff;
        }

        results.push(abvResult);

        // Overwrite the previous reading with the current reading
        prevReading = sg;
    }

    // Now, if sugar was added, the initial value should be higher and we can do a normal ABV calc
    window.Android.logInfo('MainActivity', 'Modified Initial Gravity: ' + ig.toFixed(3));
    window.Android.logInfo('MainActivity', 'Last Gravity: ' + prevReading.toFixed(3));

    return results;
}

function calculateAbv(initialGravity, subsequentGravity)
{
    // Initial model error result first
    var result = new AbvResultSet();

    if(isNaN(parseFloat(initialGravity)))
    {
        // return error result
        return result;
    }

    if(isNaN(parseFloat(subsequentGravity)))
    {
        // return error result
        return result;
    }

    var std = calculateAbvStandard(initialGravity, subsequentGravity);
    var highstd = calculateAbvHighStandard(initialGravity, subsequentGravity);
    var alt = calculateAbvAlternate(initialGravity, subsequentGravity);
    var wine = calculateAbvWine(initialGravity, subsequentGravity);

    result.standard = std.toFixed(2) + '%';
    result.highstandard = highstd.toFixed(2) + '%';
    result.alternate = alt.toFixed(2) + '%';
    result.wine = wine.toFixed(2) + '%';

    return result;
}

function calculateAbvStandard(initialGravity, subsequentGravity)
{
    try
    {
        // ABV = (ig - sg) * 131.25
        var ig = new Decimal(initialGravity);
        var sg = new Decimal(subsequentGravity);
        var c1 = new Decimal('131.25');

        return ig.minus(sg).times(c1);
    }
    catch(error)
    {
        window.Android.logError('CalcAbvStd', error);

        return new Decimal('0'); // Make sure the toFixed(2) method fires correctly.
    }
}

function calculateAbvHighStandard(initialGravity, subsequentGravity)
{
    try
    {
        // ABV = (ig - sg) * 135
        var ig = new Decimal(initialGravity);
        var sg = new Decimal(subsequentGravity);
        var c1 = new Decimal('135');

        return ig.minus(sg).times(c1);
    }
    catch(error)
    {
        window.Android.logError('CalcAbvHighStd', error);

        return new Decimal('0'); // Make sure the toFixed(2) method fires correctly.
    }
}

function calculateAbvAlternate(initialGravity, subsequentGravity)
{
    try
    {
        //OE = –668.962 + 1262.45 OG – 776.43 OG^2 + 182.94 OG^3
        //AE = –668.962 + 1262.45 FG – 776.43 FG^2 + 182.94 FG^3
        //q = 0.22 + 0.001 OE
        //RE = (q OE + AE) / (1 + q)
        //A%w = (OE – RE) /  (2.0665 – 0.010665 OE)
        //A%v = A%w (FG / 0.794)

        var ig = new Decimal(initialGravity);
        var sg = new Decimal(subsequentGravity);

        const c1 = new Decimal('-668.962');
        const c2 = new Decimal('1262.45');
        const c3 = new Decimal('776.43');
        const c4 = new Decimal('182.94');
        const c5 = new Decimal('0.22');
        const c6 = new Decimal('0.001');
        const c7 = new Decimal('2.0665');
        const c8 = new Decimal('0.010665');
        const c9 = new Decimal('0.794');

        var oe = c1.add(c2.times(ig)).minus(c3.times(ig.pow(2))).add(c4.times(ig.pow(3)));
        window.Android.logDebug('CalcAbvAlt', 'oe: ' + oe);

        var ae = c1.add(c2.times(sg)).minus(c3.times(sg.pow(2))).add(c4.times(sg.pow(3)));
        window.Android.logDebug('CalcAbvAlt', 'ae: ' + ae);

        var q = c5.add(c6.times(oe));
        window.Android.logDebug('CalcAbvAlt', 'q: ' + q);

        var re1 = q.times(oe).add(ae);
        window.Android.logDebug('CalcAbvAlt', 're1: ' + re1);

        var re2 = q.add(1);
        window.Android.logDebug('CalcAbvAlt', 're2: ' + re2);

        var re = re1.div(re2);
        window.Android.logDebug('CalcAbvAlt', 're: ' + re);

        var abw1 = oe.minus(re);
        window.Android.logDebug('CalcAbvAlt', 'abw1: ' + abw1);

        var abw2 = c7.minus(c8.times(oe));
        window.Android.logDebug('CalcAbvAlt', 'abw2: ' + abw2);

        var abw = abw1.div(abw2);
        window.Android.logDebug('CalcAbvAlt', 'abw: ' + abw);

        var abv = abw.times(sg.div(c9));
        window.Android.logDebug('CalcAbvAlt', 'abv: ' + abv);

        return abv;
    }
    catch(error)
    {
        window.Android.logError('CalcAbvAlt', error);

        return new Decimal('0'); // Make sure the toFixed(2) method fires correctly.
    }
}

function calculateAbvWine(initialGravity, subsequentGravity)
{
    try
    {
        // ABV = (ig - sg) / 7.36 * 1000

        var ig = new Decimal(initialGravity);
        var sg = new Decimal(subsequentGravity);
        var c1 = new Decimal('7.36');
        var c2 = new Decimal('1000');

        var gravdiff = ig.minus(sg);
        var ratio = gravdiff.dividedBy(c1);

        return ratio.times(c2);
    }
    catch(error)
    {
        window.Android.logError('CalcAbvWine', error);

        return new Decimal('0'); // Make sure the toFixed(2) method fires correctly.
    }
}

function showEventDescription(id, dateString)
{
    if(window.Android && id > 0)
    {
        window.Android.logInfo('MainActivity', 'Fetching event for Event ' + id);

        // Fetch data from database
        var description = window.Android.fetchEventDescription(id);

        window.Android.logDebug('MainActivity', description);

        $.alert({
            theme: confirmTheme,
            title: dateString,
            content: '<span class="description">' + description + '</span>',
            animation: 'top',
            closeAnimation: 'top',
            buttons: {
                ok: function () {
                    // Do nothing
                }
            }
        });
    }
    else
    {
        $.alert({
            theme: confirmTheme,
            title: 'Alert',
            content: 'Android Javascript bridge is not available'
        });
    }

    // Tell browser not to activate link
    return false;
}

function daysSince(strDate)
{
    var result = 0;

    if(strDate)
    {
        try
        {
            var now = new Date()
            now.setHours(0,0,0,0);
            var nowTime = now.getTime();

            var date = new Date(strDate);
            var dateTime = date.getTime();

            var diff = (nowTime - dateTime)/(24*60*60*1000);

            result = Math.floor(diff);
        }
        catch(error)
        {
            window.Android.logError('daysSince', 'Parameter: ' + strDate);
            window.Android.logError('daysSince', error);
        }
    }

    return result;
}

function displayTag(tag)
{
    if(tag)
    {
        $("#mead-tags span:contains(" + tag + ")").length;

        if(length === 0)
        {
            $("#mead-tags").append("<span>" + $.trim(tag) + "</span>");

            /* Just in case the tags part of the UI is hidden, show it upon adding */
            $("#mead-tags-label").show();
            $("#mead-tags").show();
        }
    }
}

function displayMeadTags(tagData)
{
    if(Array.isArray(tagData) && tagData.length > 0)
    {
        window.Android.logDebug('displayMeadTags', 'Adding tags to view.');

        $.each(tagData, function (i, item) {
            displayTag(item.name);
        });
    }
    else
    {
        $("#mead-tags-label").hide();
        $("#mead-tags").hide();
    }
}

function displayTagTip()
{
    $("#tag-tip").show().delay(10000).fadeOut();
}

function getPreferredAbvValue(result)
{
    var value = '';

    if(!result)
    {
        window.Android.logDebug('getPreferredAbvValue',"Result parameter is null or undefined.");
        var errorResult = new AbvResultSet();
        return errorResult.standard;
    }

    // Fetch ABV formula preference
    var abvPref = localStorage.getItem(abvPrefKeyName) ?? 'std';

    switch(abvPref)
    {
        case 'std':
            value = result.standard;
            window.Android.logDebug('getPreferredAbvValue',"avbPref detected as 'std'.");
            break;
        case 'highstd':
            value = result.highstandard;
            window.Android.logDebug('getPreferredAbvValue',"avbPref detected as 'highstd'.");
            break;
        case 'alt':
            value = result.alternate;
            window.Android.logDebug('getPreferredAbvValue',"avbPref detected as 'alt'.");
            break;
        case 'wine':
            value = result.wine;
            window.Android.logDebug('getPreferredAbvValue',"avbPref detected as 'wine'.");
            break;
        default:
            value = result.standard;
            window.Android.logError('getPreferredAbvValue','avbPref variable fell through switch.');
    }

    return value;
}

function calendarEventCallback(resultValue)
{
    if(resultValue == -1)
    {
        // Intent call for scheduling reminder doesn't return extra values
        // Needed to pull meadId from form that invoked the new activity
        var meadId = parseInt($("#newCalendarEventMeadId").val());

        if(meadId)
        {
            viewMead(meadId);
        }
    }
}

function formatDisplayDate(dateString)
{
    // I'm very concerned about converting a known string format to a date for reformatting.
    // The UTC vs local thing is currently haunting the app in other ways.
    // I'm going to do this as an explicit string manipulation

    // We know the date has dashes in it from the database, we only need to switch the string
    // if the user preference is freedom units

    // This is a helper function and not a library function, so I'm not going to make this bulletproof
    // right now.

    // Fetch ABV formula preference
    var dateFormatPref = localStorage.getItem(dateFormatPrefKeyName) ?? 'ISO';

    if(dateString && dateFormatPref)
    {
        if(dateFormatPref === "US")
        {
            // Database dates will have dashes in them
            const [year, month, day] = dateString.split('-');

            // if the split was clean, reassemble date to preferred format
            if(year && month && day)
            {
                return month + '/' + day + '/' + year;
            }
        }
    }

    return dateString;
}

function changeGlobalTheme(theme)
{
    // These themes will be cleared, add more
    // swatch letters as needed.
    var themes = " a b c d e";

    // Updates the theme for all elements that match the
    // CSS selector with the specified theme class.
    function setTheme(cssSelector, themeClass, theme)
    {
        $(cssSelector)
                .removeClass(themes.split(" ").join(" " + themeClass + "-"))
                .addClass(themeClass + "-" + theme)
                .attr("data-theme", theme);
    }

    // Add more selectors/theme classes as needed.
    setTheme(".ui-mobile-viewport", "ui-overlay", theme);
    setTheme("[data-role='page']", "ui-page-theme", theme);
    setTheme("[data-role='header']", "ui-bar", theme);
    setTheme("[data-role='listview'] > li", "ui-bar", theme);
    setTheme(".ui-btn", "ui-btn-up", theme);
    setTheme(".ui-btn", "ui-btn-hover", theme);
}