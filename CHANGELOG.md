### We manage the development using [Github releases](https://github.com/RWTH-i5-IDSG/steve/releases). This file is obsolete, won't be updated anymore.

## 1.0.2
Complete rewrite of the backend:

 - Using Spring, Hibernate Validator and JOOQ with the embedded Jetty server now. Doing database migrations with Flyway.
 - Printing form validation errors on the Web page.
 - Switched to async OCPP calls. "Tasks" menu to track the call results.
 - Transactions can be exported as CSV.
 - Better reservation modeling and handling.
 - Date and time picker for Web frontend.
 
## 1.0.1
 - Fix: Start and stop date/time values for Get Diagnostics must be in the past. Frontend allows to do that now.
 - New: Backend validates input date/time variables for Get Diagnostics.

## 1.0.0
 - DB updated to 0.6.7.
 - Home page displays various statistics now (See the screenshot). Data is returned by a stored procedure in DB.
 - Transactions can be accessed under Data Management.
 - Latest received heartbeats are stored in DB and can be accessed from home page.
 - Latest connector status information can be accessed from home page.
 - "Humanize" the date if it's from today or yesterday.
 - Drop-down date picker (JQuery plugin) for user-friendly input.
 - Time input field defaults to 00:00 when left empty.
 - Drop-down select list of user and parent id tags where applicable.
 - Drop-down select list for connector, transaction and reservation ids (via Ajax calls) for user-friendly input after a charge point is selected for an OCPP operation.
 - Client-side input control for required fields (needs HTML5).
 - Server-side fixes for mandatory/optional fields to conform to OCPP.
 - User ID Tag restriction: It must be between 1 and 20 characters long. Allowed characters are upper or lower case letters, numbers and dot, dash, underscore symbols.
 
## 0.6.8
 - Started using MVC, JQuery and Ajax.
 - InputUtils for user input validation.
 - New function to update user info.
 - Tabbed UI for data management operations.
 - CSS updates for better cross-browser compatibility.
	
## 0.6.7
 - New about page.
 - Dropped update function because of difficulties with Linux permissions.
	
## 0.6.6
 - Started versioning the app and db dump.