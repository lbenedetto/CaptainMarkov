# CaptainMarkov
A project that utilizes a database of Star Trek scripts to generate new phrases, such as captains logs, commands to the computer, or lines of dialogue from a specified character

## How It Works
Scripts are pulled from http://www.chakoteya.net/ using the ScriptScraper class and saved into "./scripts/Episode num.txt"

The KeyWord class is then used to save all lines containing the key phrase, such as "Captain's log" or "Computer, "
It includes the option to cut the line to the keyphrase so you don't get something like this:

> TROI: Let's ask the computer. Computer, identify type of Riker.

The Character class can be used to capture all the lines spoken by a given character.

The markov class then reads through the lines provided by the instance of the abstract class LineGetter to build the statistical model.

The statistical model is then used to generate new dialogue

Example outputs can be found at https://twitter.com/captain_markov

Star Trek and related marks are trademarks of CBS Studios Inc. Copyright 1966, Present. The Star Trek scripts in this repo are for educational and entertainment purposes only. All other copyrights property of their respective holders.
