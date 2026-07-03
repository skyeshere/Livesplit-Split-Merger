## Hello! Hi! Why are you looking at my code..

Welcome to the Repo! I'm sorry it is such a mess, it will not get better.

This aim of this project is to make a program that merges multiple livesplit files together into one.
This would be useful for mutligame runners, such as people who run the Multi Mario 602.

## Dependencies
[Latest version of Java SE](https://www.oracle.com/java/technologies/downloads/#java26) is recommended

## Usage

The program is run from terminal using the following command:

`java -jar lsm.jar`

If you wish to build from source. Run the following command from the /src directory:

`jar cvfe lsm.jar App App.class Split.class SplitsContainer.class SplitPuller.class SplitMerger.class`

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources and class files, cause I didnt send them to a different folder... whoops.
- `lib`: the folder to maintain dependencies
