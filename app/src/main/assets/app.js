const abvPrefKeyName = 'ABVPREF';
const sortPrefKeyName = 'SORTPREF';
const specificGravityRange = [0.980, 1.160];

// One-time code executions

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

        // Add database table values
        // Function returns a JSON string, need to convert it
        var eventTypesJson = window.Android.fetchEventTypes();

        var eventTypes = JSON.parse(eventTypesJson);

        $.each(eventTypes, function (i, item) {
            $('#newEventType').append($('<option>', {
                value: item.id,
                text : item.name
            }));
        });
    }

    $("#newEventType").selectmenu();
});

// Page Transition events

$(document).on("pagebeforeshow","#abv", function(){

    var abvPref = localStorage.getItem(abvPrefKeyName) ?? 'std';

    switch(abvPref)
    {
        case 'std':
            $("#abvFormulaPref").html("Currently using <strong>Standard</strong> formula.");
            window.Android.logDebug('abvShow',"avbPref detected as 'std'.");
            break;
        case 'alt':
            $("#abvFormulaPref").html("Currently using <strong>Alternate</strong> formula.");
            window.Android.logDebug('calcButton',"avbPref detected as 'alt'.");
            break;
        case 'wine':
            $("#abvFormulaPref").html("Currently using <strong>Wine</strong> formula.");
            window.Android.logDebug('calcButton',"avbPref detected as 'wine'.");
            break;
        default:
            abvDisplayValue = result.standard;
            $("#abvFormulaPref").html("Preference not found. Using <strong>Wine</strong> formula.");
            window.Android.logError('calcButton','avbPref variable fell through switch.');
    }

    $('#initialGravity').val('');
    $('#newGravity').val('');
    $("#abvResult").text('0.00%');
});

$(document).on("pagebeforeshow","#my-meads",function() {

    if(window.Android)
    {
        window.Android.logInfo('MainActivity','JS Bridge available. Starting data fetch for mead list.');

        // Retrieve user preference
        var sortPref = localStorage.getItem(sortPrefKeyName) ?? 'byId';

        // Clear list
        $("#mead-list").empty();

        // Fetch data from database
        var results = window.Android.fetchMeads(sortPref);
        var jsonData = JSON.parse(results);

        window.Android.logDebug('MainActivity', 'Fetched JSON: ' + results);

        // Append to list
        for (var i = 0; i < jsonData.length; i++) {

            $("#mead-list").append('<li><a href="javascript:viewMead(' + jsonData[i].id + ');" data-ajax="false">' + jsonData[i].name + '</a></li>');
        }

        $("#mead-list").listview("refresh");

        window.Android.logInfo('MainActivity','Mead list loaded and refreshed.');
    }
    else
    {
        // Insert mock item for layout testing
        $("#mead-list").empty();
        $("#mead-list").append('<li><a href="javascript:viewMead(0);" data-ajax="false">My First Mead</a></li>');
        $("#mead-list").listview("refresh");
    }
});

$(document).on("pagebeforeshow","#new-mead",function() {

    // use hidden field as the trigger for the form mode

    var meadId = $("#meadId").val();

    if(meadId)
    {
        // Field values should already be set from invoking function
        $("#new-mead-content-title").text("Edit Mead");
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

    $("#abv-formula-pref").val(abvPref);
    $("#abv-formula-pref").selectmenu("refresh", true);

    $("#sort-pref").val(sortPref);
    $("#sort-pref").selectmenu("refresh", true);

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
            required: true,
            range: specificGravityRange
        }
    },
    messages: {
        newMeadName: "Mead Name is required.",
        newMeadStartDate: "Start Date is required.",
        newMeadOriginalGravity: "Specific Gravity between " + specificGravityRange[0] + " and " + specificGravityRange[1] + " is required.",
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

// Button tap events
$("#newMeadButton").on("tap", function(event) {

    event.preventDefault();

    // Set hidden field value so form switches to correct mode
    $("#meadId").val('');
    $(":mobile-pagecontainer").pagecontainer("change", "#new-mead");
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
    $.alert('Android Javascript bridge is not available');
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
    $.alert('Android Javascript bridge is not available');
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
     $.alert('Android Javascript bridge is not available');
 }

});

$("#calcButton").on("tap", function(event) {

    event.preventDefault();

    if($("#abv-form").valid()){

        var ig = $('#initialGravity').val();
        var ng = $('#newGravity').val();

        var result = calculateAbv(ig,ng);

        var abvPref = localStorage.getItem(abvPrefKeyName) ?? 'std';

        switch(abvPref)
        {
            case 'std':
                abvDisplayValue = result.standard;
                window.Android.logDebug('calcButton',"avbPref detected as 'std'.");
                break;
            case 'alt':
                abvDisplayValue = result.alternate;
                window.Android.logDebug('calcButton',"avbPref detected as 'alt'.");
                break;
            case 'wine':
                abvDisplayValue = result.wine;
                window.Android.logDebug('calcButton',"avbPref detected as 'wine'.");
                break;
            default:
                abvDisplayValue = result.standard;
                window.Android.logError('calcButton','avbPref variable fell through switch.');
        }

        $("#abvResult").text(abvDisplayValue);
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

// Custom app functions

 function viewReadings(meadId)
 {
    if(window.Android && meadId > 0)
    {
        window.Android.logInfo('MainActivity', 'Fetching Readings for mead ID ' + meadId);

        // Fetch ABV formula preference
        var abvPref = localStorage.getItem(abvPrefKeyName) ?? 'std';

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
        $("#reading-list tbody").append('<tr><td>' + meadData.startDate + '</td><td>' + meadData.originalGravity + '</td><td>N/A</td><td>&nbsp;</td></tr>');

        // Holding variable for original or previous gravity
        var og = meadData.originalGravity;

        // Append to list
        for (var i = 0; i < readingsData.length; i++) {

            var sg = readingsData[i].specificGravity;

            var result = calculateAbv(og,sg);

            var abvDisplayValue = '';

            switch(abvPref)
            {
                case 'std':
                    abvDisplayValue = result.standard;
                    window.Android.logDebug('viewReadings',"avbPref detected as 'std'.");
                    break;
                case 'alt':
                    abvDisplayValue = result.alternate;
                    window.Android.logDebug('viewReadings',"avbPref detected as 'alt'.");
                    break;
                case 'wine':
                    abvDisplayValue = result.wine;
                    window.Android.logDebug('viewReadings',"avbPref detected as 'wine'.");
                    break;
                default:
                    abvDisplayValue = result.standard;
                    window.Android.logError('viewReadings','avbPref variable fell through switch.');
            }

            $("#reading-list tbody").append('<tr><td>' + readingsData[i].date + '</td><td>' + sg + '</td><td>' + abvDisplayValue + '</td><td><a href="javascript:deleteReading(' + meadId + ',' + readingsData[i].id + ');" class="ui-btn ui-shadow ui-corner-all ui-icon-delete ui-btn-icon-notext">Delete</a></td></tr>');
        }

        $("#newReadingButton").off("tap"); // clear existing event handlers

        $("#newReadingButton").on("tap", { meadId: meadId }, function(event) {
            event.preventDefault();

            window.Android.logDebug('MainActivity', 'New Reading Button pressed. Mead ID: ' + event.data.meadId);

            // set value of hidden form
            $("#newReadingMeadId").val(event.data.meadId);

            $(":mobile-pagecontainer").pagecontainer("change", "#new-reading",{changeHash:false});
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
            var daysAgo = Math.floor(daysSince(eventsData[i].date));

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
            $("#events-list tbody").append('<tr><td style="white-space: nowrap; text-align: center;">' + eventsData[i].date + '<br>' + daysAgoOutput + '</td><td>' + eventsData[i].typeName + '</td><td style="white-space: nowrap; text-align: center;">' +
                '<a href="javascript:showEventDescription(' + eventsData[i].id + ',\'' + eventsData[i].date + '\');" class="ui-btn ui-mini ui-btn-inline ui-shadow ui-corner-all ui-icon-comment ui-btn-icon-notext ' + disableButtonFlag + '">Show</a>' +
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
        $.alert('Android Javascript bridge is not available');
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
         $.alert('Android Javascript bridge is not available');
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
        var results = window.Android.fetchMead(id);
        var jsonData = JSON.parse(results);

        window.Android.logInfo('MainActivity', results);

        // Populate fields
        //$("#mead-id").text(jsonData.id); was used for debugging; not really useful for the user
        $("#mead-name").text(jsonData.name);
        $("#mead-start-date").text(jsonData.startDate);
        $("#mead-description").text(jsonData.description);
        $("#mead-original-gravity").text(jsonData.originalGravity);

        // add event handlers for buttons
        $("#deleteMeadButton").off("tap"); // clear existing event handlers
        $("#readingsButton").off("tap"); // clear existing event handlers
        $("#eventsButton").off("tap"); // clear existing event handlers
        $("#editMeadButton").off("tap"); // clear existing event handlers

        $("#deleteMeadButton").on("tap", { value: id }, function(event) {
            event.preventDefault();

            var id = event.data.value;

            $.confirm({
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
        $("#readingsButton").on("tap", { meadId: jsonData.id, meadName: jsonData.name, meadStartDate: jsonData.startDate, meadOriginalGravity: jsonData.originalGravity }, function(event) {
            event.preventDefault();

            viewReadings(event.data.meadId);
        });
        $("#eventsButton").on("tap", { meadId: jsonData.id, meadName: jsonData.name, meadStartDate: jsonData.startDate, meadOriginalGravity: jsonData.originalGravity }, function(event) {
            event.preventDefault();

            viewEvents(event.data.meadId);
        });
        $("#editMeadButton").on("tap", { meadId: jsonData.id, meadName: jsonData.name, meadStartDate: jsonData.startDate, meadOriginalGravity: jsonData.originalGravity, meadDescription: jsonData.description }, function(event) {

            // Populate data
            $("#meadId").val(event.data.meadId);
            $("#newMeadName").val(event.data.meadName);
            $("#newMeadStartDate").val(event.data.meadStartDate);
            $("#newMeadOriginalGravity").val(event.data.meadOriginalGravity);
            $("#newMeadDescription").val(event.data.meadDescription);

            $(":mobile-pagecontainer").pagecontainer("change", "#new-mead");
        });
    }
    else
    {
        // Populate sample data
        $("#mead-id").text("0");
        $("#mead-name").text("My First Mead");
        $("#mead-start-date").text("01/01/2021");
        $("#mead-description").text("Sample data");
        $("#mead-original-gravity").text("1.000");
    }

    $(":mobile-pagecontainer").pagecontainer("change", "#mead-view");
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
        $.alert('Android Javascript bridge is not available');
    }
}

function calculateAbv(initialGravity, subsequentGravity)
{
    // Initial model error result first
    var results = new Object();

    results.standard = '-.--%';
    results.alternate = '-.--%';
    results.wine = '-.--%';

    if(isNaN(parseFloat(initialGravity)))
    {
        // return error result
        return results;
    }

    if(isNaN(parseFloat(subsequentGravity)))
    {
        // return error result
        return results;
    }

    var std = calculateAbvStandard(initialGravity, subsequentGravity);
    var alt = calculateAbvAlternate(initialGravity, subsequentGravity);
    var wine = calculateAbvWine(initialGravity, subsequentGravity);

    results.standard = std.toFixed(2) + '%';
    results.alternate = alt.toFixed(2) + '%';
    results.wine = wine.toFixed(2) + '%';

    return results;
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

function calculateAbvAlternate(initialGravity, subsequentGravity)
{
    try
    {
        // ABV = 76.08 * (ig - sg) / (1.775 - ig) * (sg / 0.794)

        var ig = new Decimal(initialGravity);
        var sg = new Decimal(subsequentGravity);
        var c1 = new Decimal('76.08');
        var c2 = new Decimal('1.775');
        var c3 = new Decimal('0.794');

        //var result = ig.minus(sg).times('131.25');

        var gravdiff = ig.minus(sg);
        var c2diff = c2.minus(ig);
        var sgratio = sg.dividedBy(c3);
        var lowerval = c2diff.times(sgratio);

        return c1.times(gravdiff).dividedBy(lowerval);
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
        $.alert('Android Javascript Bridge is not available.');
    }

    // Tell browser not to activate link
    return false;
}

function daysSince(date)
{
    var now = new Date(); // This includes date and time UTC. Most methods called will localize the time.
    var prevDate = new Date(date);

    // Only the date part is needed; cutting off the time part to prevent temporary off-by-one errors.
    var today = new Date(now.toISOString().substring(0, 10));

    // To calculate the time difference of two dates
    var Difference_In_Time = today.getTime() - prevDate.getTime();

    // To calculate the no. of days between two dates
    var Difference_In_Days = Difference_In_Time / (1000 * 3600 * 24);

    return Difference_In_Days;
}