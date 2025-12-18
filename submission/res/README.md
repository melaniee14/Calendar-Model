a\.Changes: 
Assignment 6:
- Added controller and view GUI interfaces, and implementation classes for both interfaces: this was
done to support the GUI that needed to be added for assignment 6

- Added interfaces CalendarModelGUISupport and MultipleCalendarModelAllNames in order to add
new methods to the singular calendar mode and multiple calendar model classes in alignment with
SOLID principles

- Changed useCalendars signature because the interface for CalendarModelImpl changed to 
CalendarModelGUISupport

- Added new methods eventsToBeShown, getAllEventNames, and findEvent to CalendarModelGUISupport 
interface 
- Added getAllNames method to MultipleCalendarModelAllNames interface

- Added new class DatePickerPanel for GUI implementation to select a valid date 
- Changed EventBuilder signature for getLocation and setLocation to be an EventLocation because
it wasn't compatible when it returned a string

- Changed signature of edit event: done to support editing the start/end time of multiple events 
- or series without intefering with the GUI implementation that separates editing the date and time

Assignment 5:
Adding new interface and class for the multiple calendar model implementation: We felt this was 
justified because our current implementation should still be able to work without the requirement
of having multiple calendars. 

Adding a new interface for all the helper methods and extending that instead of CalendarModel: 
We made this change because we needed to adjust our code because it wasnt following proper MVC 
design. We were doing operations inside of the controller when that was supposed to be done in the
model. We made a new interface to follow SOLID principles.

Rehauled our editevents function: Purely we made this change because it was too long and didnt 
follow the assignment exactly. So we fixed it and made it better but it is a new public function.

Changed signature for EditEvent method: This was done in order to properly support editing events
but not the entire series. Without this change, the events would not update correctly in the
hash map and would contain outdated values.

New Public Fields: Added 2 not final fields to our single calendar implementation.
A calendar needs a name to be identified and a timezone for its events and these are not final
because they need to be changed if the user wants a new timezone or new name.

Changed signature for all command classes in the controller: In order to support the
multiple calendar model, the execute method needed to take in this model to have access
to the specific calendar currently being used.

Changed signature for CalendarCommand interface: Done because the CalendarModelImpl class now
implements the CalendarModelAllHelpers interface so it needed to be changed as well.

Changed signature for getEventsBetween and getEventsOnDate: Done according to feedback from 
assignment 4, made sure to return the interface type not the class type.

New class: Multiple calendar implementation made as per the assignment.

Updated Builder for timezones


b\. Instructions on how to run your program.

To run the program, the user must have the jar on their laptop. Locate the folder
of the jar, type in java -jar Calendar.jar then --mode interactive (for interactive mode)
or --mode headless somefile.txt for headless mode. If they want to use the gui, 
then nothing needs to be typed. In interactive mode, you can type commands one 
at a time and see responses immediately and can end the program by typing exit.
Alternatively, headless allows a file with one command per line and must end
with an exit command to be inputted. The application will execute each
command in sequence and report errors. 

c\. Which features work and which do not.

We believe that all features described in this assignment are working
which is shown in the list below. This also includes any errors that are
meant to be thrown

Working Features List:  

Creating an event

Creating Events

Creating an event series

Editing an event

Editing an event series

Checking events on a certain day

Checking events within a range

Multiple Calendars

Creating a calendar

Editing a calendar

Calendar Time zones

Editing any timezones

Copying Events

Copying one event

GUI:
- Creating an event
- Editing an event
- Creating multiple calendars
- Switching Calendars
- Viewing the schedule

d\. A rough distribution of which team member contributed to which parts
of the assignment.

GUI: 
DatePickerPanel: Matthew

Adding up to 10 events: Melanie

Edit event: Melanie

Create & Switch Calendar: Matthew

Add event: Matthew

View Schedule: Matthew

ControllerGUIImpl: Matthew

Multiple Model updated: Melanie


Model: Adding events done by the both of us, Editing events done by
Matthew, the rest Melanie

Model Updated: Matthew 

Model Helpers: Melanie

Multiple Calendar, copying, and Timezone implementation done by Matthew

Controller: Melanie

Controller Updated: Melanie

Command Design: Melanie

Updated Commands: Melanie

View: Matthew

Model Tests: Matthew

Updated Model Tests: Matthew

View Tests: Matthew

Controller Tests: Melanie

Updated Controller tests: Melanie and Matthew

Other random tests (Ex. EventID tests): Matthew

e\. Anything else you need us to know when we grade.

Nothing
