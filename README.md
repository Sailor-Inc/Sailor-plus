# Sailor +

## Table of Contents
1. [Overview](#Overview)
2. [Product Spec](#Product-Spec)
3. [Wireframes](#Wireframes)
4. [Schema](#Schema)

## Overview
### Description
Sailor+ is an app created to find and share the best places to travel around the world, follow your friends and start exploring locations, attractions, nature, and more!!. No matter where you are at or if you're just sitting in your house because you can find new places on a simple click.

### App Evaluation
- **Category:** Traveling/Lifestyle adventure
- **Mobile:** The app is suited for mobile use since it gives users easy access to a lot of features in a easy and effective way
- **Story:** Most people loves to travel, but there are limitations every time we want to, for example: money, time, and the most important part, you don't know where to go – with Sailor+ you can view a map full of locations, visit places virtually before you visit them in person, view pictures of people around the world, follow people, give "tops"(a like alternative) to the places and photos you like the most and start uploading your own places!.
- **Market:** Influencers, people that travel & like to take photos, people that love scenery photography, People with free time, etc.
- **Habit:** The users would open this app every day to check the people they follow, or to explore a zone, see the most popular places and photos before they start traveling, or just for fun and to kill time.
- **Scope:** The application would have a good amount of active users that continue uploading photos of the places they travel and start making a community, also the possibility to find travel agencies, promos and the best prices when searching for places.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**
  * **Log up/in Screen**
    - [ ] User can sign in/sign up to an account using Parse

  * **Map**
    - [ ] User can see a map with the top 20 places
    - [ ] User can see a list of the top user photos of a selected place inside a modal/new activity
    - [ ] User can filter by friends locations
    - [ ] User can add a photo of his travel location, and a caption

  * **Discover/Feed screen**
    - [ ] User can see a feed with latest photos and details of the places of the people they follow
    - [ ] User can click on the image to see a detailed view
    - [ ] User can double click an image to top a location/post
    - [ ] User can click on the location of the selected post to see a modal/activity of the top posts of that location
    - [ ] User can scroll down with endless scrolling (or until there are no more photos to show)

  * **Profile screen**
    - [ ] User can click on a button to see the settings of his account
      * Settings screen
      - [ ] User can log out
     - [ ] User can change his profile picture
     - [ ] User can see the his followers
     - [ ] User can see the people they follow
     - [ ] User can scroll down with endless scrolling (or until there are no more photos to show)
     - [ ] User can see his location posts in a grid layout
     - [ ] User can click on a location post and see details

  * **Selected profile screen**
     - [ ] User can see the followers
     - [ ] User can see the people that account follows
     - [ ] User can scroll down with endless scrolling (or until there are no more photos to show)
     - [ ] User can see that account location posts in a grid layout
     - [ ] User can click on a location post and see details

  * **Visuals**
     - [ ] The app visuals must be approved by 10 of my friends
     - [ ] The app visuals must be approved by my manager

 **Optional Nice-to-have Stories**
 * User can see an intelligent recommendation of locations in the feed
 * Extra polished UI design
 * User can filter the locations with parameters like : recommended, number of reviews, country, state
 * User can send direct messages to other users
 * Comment on a post

### 2. Screen Archetypes

* Log up/in Screen
  * User can sign in/sign up to an account using Parse

* Map
  * User can see a map with the top 20 places
  * User can see a list of the top user photos of a selected place inside a modal/new activity
  * User can filter by friends locations
  * User can add a photo of his travel location, and a caption

* Discover screen
  * User can see a feed with latest photos and details of the places of the people they follow
  * User can click on the image to see a detailed view
  * User can click on the location of the selected post to see a modal/activity of the top posts of that location
  * User can scroll down with endless scrolling (or until there are no more photos to show)

* Profile screen
  * User can click on a button to see the settings of his account
    * Settings screen
      * User can log out
  * User can change his profile picture
  * User can see the his followers
  * User can see the people they follow
  * User can scroll down with endless scrolling (or until there are no more photos to show)
  * User can see his location posts in a grid layout
  * User can click on a location post and see details

* Selected profile screen
  * User can see the followers
  * User can see the people that account follows
  * User can scroll down with endless scrolling (or until there are no more photos to show)
  * User can see that account location posts in a grid layout
  * User can click on a location post and see details

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Map screen
* Feed Screen
* Profile screen
* Detailed post screen
* Selected profile screen
* followers/following screen
* Location posts modal/activity screen

**Flow Navigation** (Screen to Screen)

* Map screen
   * Location posts
   * Create location
   *
* [list second screen here]
   * [list screen navigation here]
   * ...

## Wireframes
<img src="wireframes/firstwireframe.jpg" width=300>
<img src="wireframes/secondwireframe.jpg" width=300>
<img src="wireframes/thirdwireframe.jpg" width=300>


### [BONUS] Digital Wireframes & Mockups

<img src="wireframes/wireframe.jpeg" width=300>

### [BONUS] Interactive Prototype

<img src="wireframes/wireframe.gif" width=300>

## Schema
### Models
#### User

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | name          | String   | Name of the user |
   | password      | String   | Password of the user |
   | followers     | List<Pointer to user>| Followers of the user |
   | following     | List<Pointer to user> | People followed by the user |
   | profilePicture| File | User profile picture|

#### Post
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | author        | Pointer to User| Post author |
   | image         | File     | image that user posts |
   | caption       | String   | image caption by author |
   | toppedBy      | List     | people that have gave a top to that post |
   | location      | Pointer to location | object location for a post|
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |

 #### Location
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | gmapsid       | String   | Name of the location |
   | locationPosts | List<Pointer to Post>  | List of Posts of that location |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |

### Networking
#### List of network requests by screen
   - Home Feed Screen
      - (Read/GET) Query all posts where user is author
         ```swift
         let query = PFQuery(className:"Post")
         query.whereKey("author", equalTo: currentUser)
         query.order(byDescending: "createdAt")
         query.findObjectsInBackground { (posts: [PFObject]?, error: Error?) in
            if let error = error {
               print(error.localizedDescription)
            } else if let posts = posts {
               print("Successfully retrieved \(posts.count) posts.")
           // TODO: Do something with posts...
            }
         }
         ```
      - (Create/POST) Create a new like on a post
      - (Delete) Delete existing like
      - (Create/POST) Create a new comment on a post
      - (Delete) Delete existing comment
   - Create Post Screen
      - (Create/POST) Create a new post object
   - Profile Screen
      - (Read/GET) Query logged in user object
      - (Update/PUT) Update user profile image

#### [OPTIONAL:] Existing API Endpoints
##### An API Of Ice And Fire
- Base URL - [http://www.anapioficeandfire.com/api](http://www.anapioficeandfire.com/api)

   HTTP Verb | Endpoint | Description
   ----------|----------|------------
    `GET`    | /characters | get all characters
    `GET`    | /characters/?name=name | return specific character by name
    `GET`    | /houses   | get all houses
    `GET`    | /houses/?name=name | return specific house by name

##### Game of Thrones API
- Base URL - [https://api.got.show/api](https://api.got.show/api)

   HTTP Verb | Endpoint | Description
   ----------|----------|------------
    `GET`    | /cities | gets all cities
    `GET`    | /cities/byId/:id | gets specific city by :id
    `GET`    | /continents | gets all continents
    `GET`    | /continents/byId/:id | gets specific continent by :id
    `GET`    | /regions | gets all regions
    `GET`    | /regions/byId/:id | gets specific region by :id
    `GET`    | /characters/paths/:name | gets a character's path with a given name


 ********************************

Must have app requirements

- [X] Your app has multiple views
- [X] Your app interacts with a database (e.g. Parse)
- [X] You can log in/log out of your app as a user
- [X] You can sign up with a new user profile
- [X] Your app integrates with at least one SDK (e.g. Google Maps SDK, Facebook SDK) or API (that you didn’t learn about in CodePath)
- [X] Your app uses at least one gesture (e.g. double tap to like, e.g. pinch to scale)
- [X] Your app uses at least one animation (e.g. fade in/out, e.g. animating a view growing and shrinking)
- [ ] Your app incorporates at least one external library to add visual polish
- [X] Your app provides opportunities for you to overcome difficult/ambiguous technical problems (more below)


### 1. User Stories by week (Required and Optional)

* **WEEK 1**
    * **Visuals**
    - [X] User uses fragments to move screen to screen
    * **Log in**
    - [X] User can sign in/sign up to an account using Parse
    - [X] User is persisted
    * **Map**
    - [ ] User can see a map with pins of the top 20 places
      - [X] User can use see a Google map
      - [ ] The map execute a query to load most popular locations
    - [ ] User can see a list of the top user photos of a selected place inside a modal/new activity
    - [X] User can add a photo of his travel location, and a caption
    * **Profile**
    - [X] User can click on a button in his profile page to see the settings of his account
      * **Settings screen**
      - [X] User can log out
    - [X] User can change his profile picture

    ### FIRST SPRINT

   <img src="wireframes/firstsprint.gif" width=300>

* **WEEK 2**
    * Map
     - [ ] User can filter by friends locations
       - [ ] The app executes a query that shows the most popular locations of the places your friends have visited
     - [ ] User can filter by distance radius
       - [ ] The app executes a query that shows the places at a certain distance radius starting at your postition
     - [ ] User can click on the location of the selected post to see a modal/activity of the top posts of that location
     - [X] User can double click an image to top a location/post
    * Profile (Personal and specific clicked account)
    - [X] User can see that account location posts in a grid layout
       - [ ] The posts will be displayed from newer to oldest
    - [X] User can click on a location post and see details
    - [X] User can see an animation when a specific post is selected to show details
    - [ ] User can see the followers of that account
    - [ ] User can see the people that account follows

 * **WEEK 3**
     * App
     - [ ] At least one notification
     - [ ] The app can detect NSFW images and avoid uploading them
     * Map
     - [ ] User can filter by location type
     - [ ] User can scroll down with endless scrolling (or until there are no more photos to show)(posts by location)
     * Profile
     - [ ] User can scroll down with endless scrolling (or until there are no more photos to show)(posts)
     * Feed
     - [ ] User can see a feed with latest photos and details of the places of the people they follow
     - [ ] User can see a feed with the posts your friends gave tops
     - [ ] User can scroll down with endless scrolling (or until there are no more photos to show)(posts)
     * Visuals
     - [ ] The app visuals must be approved by my manager
