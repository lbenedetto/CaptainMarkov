# CaptainMarkov
A project that utilizes a database of Star Trek scripts to generate new captains logs

Scripts are pulled from http://www.chakoteya.net/ using the ScriptScraper class and saved into "Episode num.txt"

The ScriptReader class is then used to save all the Captain's Logs to "Captains Log.txt" 
Example output: http://hastebin.com/epatiqecul.txt

The markov class then reads through the Captains Logs to build the statistical model.

The statistical model is then used to generate new captains logs.
