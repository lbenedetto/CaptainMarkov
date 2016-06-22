# CaptainMarkov
A project that utilizes a database of Star Trek scripts to generate new phrases, such as captains logs, commands to the computer, or lines of dialogue from a specified character

## How It Works
Scripts are pulled from http://www.chakoteya.net/ using the ScriptScraper class and saved into "./scripts/SeriesName/EpisodeNum.txt". This is done the first time your run the program, and can take a long time.

A GUI is opened to let you customize how the chain will be built. You can generate lines of dialogue from characters by inputing how their lines appear in the scripts. E.g. "PICARD:" and using the check box to select which series to search through. There are also a few preset generators for you to choose from.

A MarkovChain (type of statistical model) is built based off of the scripts fed in and is used to generate phrases.

Example outputs can be found at https://twitter.com/captain_markov

Star Trek and related marks are trademarks of CBS Studios Inc. Copyright 1966, Present. The Star Trek scripts in this repo are for educational and entertainment purposes only. All other copyrights property of their respective holders.
