## Hello! Hi! Why are you looking at my code..

Welcome to the Repo! I'm sorry it is such a mess, it will not get better.

This aim of this project is to make a program that merges multiple livesplit files together into one.
This would be useful for mutligame runners, such as people who run the Multi Mario 602.

## Future Upkeep

There are definitely some areas where this can be improved... The entire way the program finds the correct XML tag at every stage in the program needs to be redone, using a proper tree searching algorithm. Maybe add support for importing more information such as combined PB comparisons and other comparisons. Also adding support to keep split images, although since they are stored as strings internally it would be difficult depending on the image's file size.

All of these would probably be done outwith this specific project, and in a different language. Although java is a powerful programming language and is designed to be portable, building this project on JS/typescript and having it accessible via a webapp would be best for accessibility. 

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources and class files, cause I didnt send them to a different folder... whoops.
- `lib`: the folder to maintain dependencies
