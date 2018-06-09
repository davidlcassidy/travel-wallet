
# Travel Wallet

Travel Wallet is an Android mobile app that can be used to manage travel loyalty programs and credit cards. Built for 
frequent travelers and  regular users of rewards credit cards, Travel Wallet allows you to track all of your frequent 
flier miles, hotel/rental car points, and credit cards all in one location. Travel Wallet also monitors all of these 
items and provides helpful notifications to its users. 

Finally, after the initial instalation, Travel Wallet never connects to the internet and functions completely offline. 
Along with the superior privacy implications of preventing user data from ever leaving the device, this also makes 
Travel Wallet an ideal travel app for any world traveler.

Features:
- Simple design
- Ad-free
- Works offline
- Point expiration notifications
- Annual fee notifications
- Supports 40+ loyalty programs, 130+ credit cards


## Installation

This application is listed on the Google Play store and can be downloaded and installed directly from there.

This application requireds Android 4.0.3 "Ice Cream Sandwich" or higher (API 15+).

## Getting Started

### Introduction

Travel Wallet consist of three interfaces, each with it's own tab and display window.

- Notifications Interface - Displays notifications from both the loyalty programs and the credit cards
- Loyalty Program Interface - Provides users the ability add, view, edit, and delete loyalty programs
- Credit Card Interface - Provides users the ability add, view, edit, and delete credit cards

Along with these three interfaces, there are three buttons on the top of the main screen.

- Info - Displays generic app information
- Summary - Provides a summary report user's loyalty programs and credit cards
- Settings - Location where user preferences can be edited

### Notifications Interface

Both loyalty program and credit card notifications will appear in the first tab titled Notifications 
and are sorted by time remaining until the approaching event date. There are currently two types of 
event notifications that are sent:

- Upcoming loyalty program points expiration
- Upcoming credit card annual fee

In the Settings, the user can adjust the period of time before these events that a notification will 
be sent. Futhermore, monitoring of each individual loyalty program and credit card can be turned on 
or off, dictating whether or not notifications will be sent. This makes for a flexable notifications 
interface that is customizable to the needs of each user.

Along with the Notifications tab, Travel Wallet can also send push notifications to the phone status 
bar in real time. This can be useful for users who would like to be automatically notificed of events 
rather than regularly opening the app and checking their notifications manually. These phone 
notifications can be turned on/off in the Settings. 

Phone notifications can be cleared, but individual notifications within the app can not currently be 
cleared or snoozed. They will remain until they are resolved or their monitoring turned off. Finally, 
note that clicking on each notification within the app will route the user to the loyalty program or 
credit card causing the notification. 

### Loyalty Programs Interface

Loyalty programs will appear in the second tab titled Loyalty Programs. New loyalty programs are added 
by clicking the pink plus button in the bottom right. Once programs have been added, the main screen 
will show a summary of all of the programs. The sort order and primary display field can be configured 
in the Settings. Clicking on an program will pull up that programs's detail screen displaying all fields 
for that program along a button to toggle the notification monitoring for the paticular program. On the 
top of the detail screen, there are also buttons for editing or deleting the specific program.

### Credit Cards Interface

Credit cards will appear in the third tab titled Credit Cards. The credit card interface fuctions 
essentually the same as the loyalty program interface in regards to adding, viewing, editing, and deleting 
credit cards.

## Support

I hope you are able to find some value out of this app. If you have any feature requests, bug reports, or 
any other feedback, please feel free to shoot me an email. I love hearing back about my projects!

- Email:  travelwallet@davidlcassidy.com
- Website:  www.DavidLCassidy.com

## Building From Source

### Requirements

- Android SDK v15+
- Latest Android Build Tools
- Android Support Repository

### Installation

This project uses the Gradle build system.

1. Download the project by cloning this repository or downloading a snapshot.
1. In Android Studio, create a new project and choose the "Import Project" option.
1. Select the "Travel Wallet" directory that you downloaded with this repository.
1. If prompted for a gradle configuration, accept the default settings.
  Alternatively use the "gradlew build" command to build the project directly.

## Contributions

Pull requests and GitHub issues are welcome!

If you would like to do a pull request, please get in touch with me before you start writing code so we 
can avoid duplicated effort or unnecessary work.

## License

Copyright (C) 2018 David Cassidy.

This project is open source and licensed under the GNU AGPLv3 license, Version 3.0 - see the [LICENSE](LICENSE) 
file distributed with this project for additional information. You may not use this project or its accociated 
files except in compliance with the License. 

Unless required by applicable law or agreed to in writing, software distributed under the License is 
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and limitations under the License.
