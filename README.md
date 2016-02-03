# CaptainMarkov
A project that utilizes a database of Star Trek scripts to generate new captains logs

Scripts are pulled from http://www.chakoteya.net/ using the ScriptScraper class and saved into "./scripts/Episode num.txt"

The KeyWord class is then used to save all lines containing the key phrase, such as "Captain's log" or "Computer, "
It includes the option to cut the line to the keyphrase so you don't get something like
    TROI: Lets ask the computer. Computer, identify type of Riker.

The Character class can be used to capture all the lines spoken by a given character.

The markov class then reads through the lines provided by the instance of the abstract class LineGetter to build the statistical model.

The statistical model is then used to generate new dialogue

Example outputs can be found at https://twitter.com/captain_markov
