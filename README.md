# Weather History App

The Weather History App is designed for users who want to download and store historical weather data for a foreign country. It provides functionality to retrieve weather information using a free weather API, input specific dates and years, and display maximum and minimum temperatures for those dates. Additionally, the app allows users to store weather data locally using *ROOM database*, ensuring access even without an internet connection.


## Features

### Download Historical Weather Data

- Utilizes Meteo Historical Weahter API to retrieve historical weather data in JSON format.
- Parses the JSON response to extract relevant weather information.

### Input Date and Year

- Users can input a specific date and year to view the maximum and minimum temperature for that date.

### Local Database Storage (Using ROOM)

- The app uses *ROOM database* for local storage.
- Upon initialization, it creates the necessary database schema and tables.
- Weather data can be inserted into the database and retrieved as needed.

### Handling Future Dates

- In cases where the input date is in the future, the app calculates the average of the last 10 available years' temperatures.

## Getting Started

To start using the Weather History App, follow these steps:

1. *Prerequisites:*
   - Ensure you have Android Studio installed on your computer.
   - An internet connection is required to download dependencies and access the weather API.

2. *Installation:*
   - Clone this repository to your local machine using the command:
     
     [git clone https://github.com/yourusername/weather-history-app.git](https://github.com/param934/Android_A2.git)
     
   - Open the project in Android Studio.
   - Build the project to download necessary dependencies.
   - Run the app on an Android emulator or physical device.

## Usage

1. *Input Date and Year:*
   - Upon launching the app, you'll see an option to input a date and year.
   - Enter the desired date and year, then click the "Search" button.

2. *View Temperature Data:*
   - The app will retrieve and display the maximum and minimum temperature for the specified date.

3. *Local Storage (Optional):*
   - Click the "Save" button to store the weather data locally using *ROOM database*.

## Implementation Details

### Weather API Integration

- Utilizes Meteo Historical Weather API to download historical weather data in JSON format.
- Parses the JSON response to extract relevant weather information.

### ROOM Database Integration

- Uses ROOM for local storage.
- Creates necessary database schema and tables during app initialization.
- Inserts weather data into the database and retrieves it when needed.

### UI Design

- Designed a user-friendly interface using Compose UI toolkit.
- Validates user input and displays appropriate error messages.
- Provides smooth navigation and intuitive controls.
