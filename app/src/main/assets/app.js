// Page Transition events

$(document).on("pagebeforeshow","#my-meads",function() {
    if(window.Android)
    {
        window.Android.logInfo('MainActivity','JS Bridge available. Starting data fetch for mead list.');

        // Clear list
        $("#mead-list").empty();

        // Fetch data from database
        var results = window.Android.fetchMeads();
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

    // Clear form values
    $("#newMeadName").val('');
    $("#newMeadStartDate").val('');
    $("#newMeadOriginalGravity").val('');
    $("#newMeadDescription").val('');

    $("#new-mead-savemsg").hide(0);
});

$(document).on("pagebeforeshow","#new-reading",function() {

    // Clear form
    $("#newReadingDate").val('');
    $("#newReadingGravity").val('');

});

$(document).on("pagebeforeshow","#new-event",function() {
    //Clear form
    $("#newEventDate").val('');
    $("#newEventType").val('');
    $("#newEventType").selectmenu("refresh", true);
    $("#newEventEntry").text('');
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
            required: true
        }
    },
    messages: {
        newMeadName: "Mead Name is required.",
        newMeadStartDate: "Start Date is required.",
        newMeadOriginalGravity: "Specific Gravity is required."
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
            required: true
        }
    },
    messages: {
        newReadingDate: "Reading Date is required.",
        newReadingGravity: "Specific Gravity is required."
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

// Button tap events

 $("#new-mead-save").on("tap", function(event){

    if(window.Android)
    {
        if($("#new-mead-form").valid()){

            // Persist data
            var mName = $("#newMeadName").val();
            var mDate = $("#newMeadStartDate").val();
            var mGravity = $("#newMeadOriginalGravity").val();
            var mDesc = $("#newMeadDescription").val();

            window.Android.addMead(mName, mDate, mGravity, mDesc);

            window.Android.logInfo('MainActivity', 'New mead saved!');

            // Set result message and alert user
            $("#new-mead-savemsg").text("Saved!");
            $("#new-mead-savemsg").show();

            setTimeout(function() {
                $("#new-mead-savemsg").hide(1500);
            }, 2000);
        }
    }
    else
    {
        $("#new-mead-savemsg").text("Save not available outside Android environment.");
        $("#new-mead-savemsg").show();

        setTimeout(function() {
            $("#new-mead-savemsg").hide(1500);
        }, 2000);
    }

 });

 $("#saveReadingButton").on("tap", function(event){

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

            $.alert({
                title: '',
                content: 'New Reading Saved!',
                buttons: {
                    ok: function () {

                        viewReadings(meadId);
                    }
                }
            });
        }
    }
    else
    {
        $.alert('Android Javascript bridge is not available');
    }

 });

 $("#saveEventButton").on("tap", function(event){

     if(window.Android)
     {
         if($("#new-event-form").valid()){

             // Persist data
             var meadId = $("#newEventMeadId").val();
             var date = $("#newEventDate").val();
             var type = $("#newEventType").val();
             var entry = $("#newEventEntry").val();

             window.Android.logDebug('MainActivity', 'MeadID: ' + meadId);
             window.Android.logDebug('MainActivity', 'Date: ' + date);
             window.Android.logDebug('MainActivity', 'Type: ' + type);
             window.Android.logDebug('MainActivity', 'Entry: ' + entry);

             window.Android.addLogEntry(meadId, date, type, entry);

             window.Android.logInfo('MainActivity', 'New mead log entry saved!');

             $.alert({
                 title: '',
                 content: 'New Event Saved!',
                 buttons: {
                     ok: function () {

                         viewEvents(meadId);
                     }
                 }
             });
         }
     }
     else
     {
         $.alert('Android Javascript bridge is not available');
     }

  });

 $("#calcButton").on("tap", function(event) {
     var ig = $('#initialGravity').val();
     var ng = $('#newGravity').val();

     var result = calculateAbv(ig,ng);

     $('#abvResult').text(result);
 });

 $("#exportButton").on("tap", function(event) {

 });

// Custom app functions

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

            $("#reading-list tbody").append('<tr><td>' + readingsData[i].date + '</td><td>' + sg + '</td><td>' + result + '</td><td><a href="javascript:deleteReading(' + meadId + ',' + readingsData[i].id + ');" class="ui-btn ui-shadow ui-corner-all ui-icon-delete ui-btn-icon-notext">Delete</a></td></tr>');
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

        var eventsJson = window.Android.fetchLogEntries(meadId);
        var eventsData = JSON.parse(eventsJson);

        window.Android.logDebug('MainActivity', meadJson);
        window.Android.logDebug('MainActivity', eventsJson);

        // Clear existing rows from table
        // Clear list
        $("#events-list tbody").empty();

        // Append to data
        for (var i = 0; i < eventsData.length; i++) {

            var disableButtonFlag = 'ui-state-disabled';

            if(eventsData[i].entry)
            {
                disableButtonFlag = '';
            }

            // Append data to list
            $("#events-list tbody").append('<tr><td style="white-space: nowrap;">' + eventsData[i].date + '</td><td>' + eventsData[i].typeName + '</td><td style="white-space: nowrap;">' +
                '<a href="javascript:showEntry(' + eventsData[i].id + ');" class="ui-btn ui-mini ui-btn-inline ui-shadow ui-corner-all ui-icon-comment ui-btn-icon-notext ' + disableButtonFlag + '">Show</a>' +
                '<a href="javascript:deleteEvent(' + meadId + ',' + eventsData[i].id + ');" class="ui-btn un-mini ui-btn-inline ui-shadow ui-corner-all ui-icon-delete ui-btn-icon-notext">Delete</a>' +
                '</td></tr>');
        }

        $("#newEventButton").off("tap"); // clear existing event handlers

        $("#newEventButton").on("tap", { meadId: meadId }, function(event) {
            event.preventDefault();

            window.Android.logDebug('MainActivity', 'New Event Button pressed. Mead ID: ' + event.data.meadId);

            // set value of hidden form
            $("#newEventMeadId").val(event.data.meadId);

            $(":mobile-pagecontainer").pagecontainer("change", "#new-event",{changeHash:false});
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
             buttons: {
                 confirm: function () {
                     window.Android.deleteLogEntry(eventId);

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

        $("#deleteMeadButton").on("tap", { value: id }, function(event) {
            event.preventDefault();

            var id = event.data.value;

            $.confirm({
                title: 'Delete Mead Entry',
                content: 'Are you sure?',
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

function calculateAbv(initialGravity, subsequentGravity)
{
    // ABV = (OG - FG) * 131.25

    if(isNaN(parseFloat(initialGravity)))
    {
        return "Initial Gravity value is invalid.";
    }

    if(isNaN(parseFloat(subsequentGravity)))
    {
        return "Gravity value is invalid."
    }

    var ig = new Decimal(initialGravity);
    var sg = new Decimal(subsequentGravity);

    var result = ig.minus(sg).times('131.25');

    return 'ABV ' + result.toFixed(2) + '%';
}

function showEntry(id)
{
    if(window.Android && id > 0)
    {
        window.Android.logInfo('MainActivity', 'Fetching entry for LogEntry ' + id);

        // Fetch data from database
        var entry = window.Android.fetchLogEntry(id);

        window.Android.logDebug('MainActivity', entry);

        $.alert({
            title: '',
            content: entry,
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
            title: '',
            content: 'Android Javascript Bridge is not available.',
            buttons: {
                ok: function () {
                    // Do nothing
                }
            }
        });
    }

    // Tell browser not to activate link
    return false;
}